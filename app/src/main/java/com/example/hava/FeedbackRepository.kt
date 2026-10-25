package com.example.hava

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class FeedbackRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("hava_feedbacks_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Hızlı timeout ile farklı adresleri sırayla dener
    private val fastHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()

    // Olası backend adresleri:
    // 1. 10.0.2.2: Android Studio Resmi Emülatörü için
    // 2. 10.74.254.50: Wi-Fi üzerindeki fiziksel telefonlar için bilgisayarın yerel IP'si
    // 3. 127.0.0.1: USB ADB reverse (adb reverse tcp:5000 tcp:5000) kullananlar için
    private val candidateUrls: List<String>
        get() {
            val list = mutableListOf<String>()
            val isEmulator = Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86")

            if (isEmulator) {
                list.add("http://10.0.2.2:5000/api/feedback")
                list.add("http://10.74.254.50:5000/api/feedback")
                list.add("http://127.0.0.1:5000/api/feedback")
            } else {
                // Gerçek telefon ise önce yerel Wi-Fi IP'sini dener
                list.add("http://10.74.254.50:5000/api/feedback")
                list.add("http://127.0.0.1:5000/api/feedback")
                list.add("http://10.0.2.2:5000/api/feedback")
            }
            return list
        }

    suspend fun submitFeedback(requestData: FeedbackRequest): Result<String> = withContext(Dispatchers.IO) {
        val jsonPayload = gson.toJson(requestData)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonPayload.toRequestBody(mediaType)

        var lastError: Exception? = null

        // Olası IP adreslerini sırayla dene
        for (url in candidateUrls) {
            try {
                Log.d("FeedbackRepo", "Bağlantı deneniyor: $url")
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                fastHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.i("FeedbackRepo", "Başarıyla iletildi ($url)")
                        return@withContext Result.success("Bildiriminiz sunucuya iletildi! 🎉 (Dashboard'da görebilirsiniz)")
                    } else {
                        Log.w("FeedbackRepo", "Sunucu hata döndü: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.w("FeedbackRepo", "$url adresine ulaşılamadı: ${e.message}")
                lastError = e
            }
        }

        // Sunucuya ulaşılamadıysa çevrimdışı sakla
        saveOffline(requestData)
        Log.w("FeedbackRepo", "Tüm sunucu adresleri başarısız oldu. Çevrimdışı kaydedildi.")
        
        Result.failure(
            Exception(
                "Sunucuya bağlanılamadı!\n" +
                "Lütfen bilgisayarda backend/start.bat dosyasını çalıştırın.\n" +
                "(Bildiriminiz telefon hafızasına kaydedildi.)"
            )
        )
    }

    private fun saveOffline(request: FeedbackRequest) {
        try {
            val list = getOfflineFeedbacks().toMutableList()
            list.add(0, request)
            prefs.edit().putString("offline_list", gson.toJson(list)).apply()
        } catch (e: Exception) {
            Log.e("FeedbackRepo", "Çevrimdışı kayıt hatası", e)
        }
    }

    fun getOfflineFeedbacks(): List<FeedbackRequest> {
        val json = prefs.getString("offline_list", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<FeedbackRequest>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
