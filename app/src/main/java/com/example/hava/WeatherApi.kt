package com.example.hava

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String =
            "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m," +
                    "wind_direction_10m,weather_code,is_day,precipitation,cloud_cover,surface_pressure",
        @Query("hourly") hourly: String =
            "temperature_2m,weather_code,precipitation_probability,is_day",
        @Query("daily") daily: String =
            "temperature_2m_max,temperature_2m_min,weather_code,precipitation_probability_max",
        @Query("forecast_hours") forecastHours: Int = 24,
        @Query("forecast_days") forecastDays: Int = 5,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
