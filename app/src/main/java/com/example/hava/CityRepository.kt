package com.example.hava

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Locale

// Listedeki bir satır: şehir/ilçe + anlık sıcaklık (alınamadıysa null)
data class CityItem(val city: City, val temperature: Double?)

class CityRepository(
    private val api: CityApi = ApiClient.cityApi,
    private val weatherApi: WeatherApi = ApiClient.weatherApi
) {
    private val trLocale = Locale.forLanguageTag("tr-TR")

    suspend fun searchCities(query: String): List<CityItem> = coroutineScope {
        val q = query.trim().lowercase(trLocale)
        // Kullanıcının yazdığı harflerle başlayan il ve ilçeler
        val matches = allSearchLocations.filter { it.lowercase(trLocale).startsWith(q) }

        matches
            .take(15)
            .map { targetName ->
                async {
                    val results = api.searchCities(targetName, count = 5).results.orEmpty()
                    if (results.isEmpty()) return@async null

                    // 1. API sonuçları içinde ismi aradığımız yerle TAM eşleşeni bul
                    // (Örn: "Ahırlı" aratıldığında API'nin 1. sıradaki "Karaburun" hatasını atlayıp 2. sıradaki gerçek "Ahırlı"yı seçer)
                    val matchedCity = results.firstOrNull {
                        it.name.equals(targetName, ignoreCase = true)
                    } ?: results.firstOrNull {
                        // Türkçe karakter duyarsız eşleşme
                        it.name.replace("ı", "i").replace("I", "i")
                            .equals(targetName.replace("ı", "i").replace("I", "i"), ignoreCase = true)
                    } ?: results.first()

                    // Şehir adının kesinlikle aranan yer olmasını garantiye al
                    val finalCity = matchedCity.copy(name = targetName)

                    val temp = try {
                        weatherApi.getCurrentWeather(finalCity.latitude, finalCity.longitude)
                            .current?.temperature
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        null
                    }
                    CityItem(finalCity, temp)
                }
            }
            .awaitAll()
            .filterNotNull()
    }
}
