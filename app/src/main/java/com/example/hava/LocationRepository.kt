package com.example.hava

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val district: String?,   // ilçe
    val province: String?    // il
)

class LocationRepository(context: Context) {

    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    suspend fun getCurrentLocation(): UserLocation {
        val (lat, lon) = requestCoordinates()
        val (district, province) = reverseGeocode(lat, lon)
        return UserLocation(lat, lon, district, province)
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCoordinates(): Pair<Double, Double> =
        suspendCancellableCoroutine { cont ->
            val cancelSource = CancellationTokenSource()
            cont.invokeOnCancellation { cancelSource.cancel() }

            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancelSource.token
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(location.latitude to location.longitude)
                    } else {
                        cont.resumeWithException(
                            IllegalStateException("Konum alınamadı. Telefonun konum servisi açık mı?")
                        )
                    }
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    // Koordinattan ilçe/il adı. Başarısız olursa (null, null): uygulama yine çalışır
    @Suppress("DEPRECATION")
    private suspend fun reverseGeocode(lat: Double, lon: Double): Pair<String?, String?> =
        withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) return@withContext null to null
                val address = Geocoder(appContext, Locale.forLanguageTag("tr-TR"))
                    .getFromLocation(lat, lon, 1)?.firstOrNull()
                (address?.subAdminArea ?: address?.locality) to address?.adminArea
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null to null
            }
        }
}