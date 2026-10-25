package com.example.hava

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // İstek ve cevapları Logcat'e yazar (etiket: OkHttp)
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val cityApi: CityApi by lazy {
        retrofit("https://geocoding-api.open-meteo.com/").create(CityApi::class.java)
    }

    val weatherApi: WeatherApi by lazy {
        retrofit("https://api.open-meteo.com/").create(WeatherApi::class.java)
    }

    // Android Emülatöründen bilgisayardaki backend'e erişmek için 10.0.2.2 kullanılır
    val feedbackApi: FeedbackApi by lazy {
        retrofit("http://10.0.2.2:5000/").create(FeedbackApi::class.java)
    }
}
