package com.example.hava

import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "tr",
        @Query("country_code") countryCode: String = "TR"
    ): CitySearchResponse
}
