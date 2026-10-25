package com.example.hava

import com.google.gson.annotations.SerializedName

// API'nin döndürdüğü tek bir şehir / ilçe
data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    @SerializedName("country_code") val countryCode: String? = null
)

// API'nin tüm cevabı. Sonuç yoksa "results" hiç gelmez, o yüzden nullable
data class CitySearchResponse(
    val results: List<City>? = null
)
