package com.example.hava

import com.google.gson.annotations.SerializedName

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int = 0,
    val isDay: Int = 1
)

data class DailyWeather(
    val time: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val weatherCode: Int,
    val precipitationProbabilityMax: Int = 0
)

data class HourlyRaw(
    val time: List<String> = emptyList(),
    @SerializedName("temperature_2m") val temperature: List<Double> = emptyList(),
    @SerializedName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int> = emptyList(),
    @SerializedName("is_day") val isDay: List<Int> = emptyList()
)

data class DailyRaw(
    val time: List<String> = emptyList(),
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double> = emptyList(),
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double> = emptyList(),
    @SerializedName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList()
)

data class WeatherResponse(
    val current: CurrentWeather? = null,
    val hourly: HourlyRaw? = null,
    val daily: DailyRaw? = null
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("is_day") val isDay: Int = 1,
    val time: String? = null,
    val precipitation: Double = 0.0,
    @SerializedName("cloud_cover") val cloudCover: Int = 0,
    @SerializedName("surface_pressure") val pressure: Double = 0.0,
    @SerializedName("wind_direction_10m") val windDirection: Int = 0
)

data class FullWeather(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather> = emptyList(),
    val daily: List<DailyWeather> = emptyList()
)

sealed interface WeatherState {
    data object Idle : WeatherState
    data object Loading : WeatherState
    data class Success(val weather: FullWeather) : WeatherState
    data class Error(val message: String) : WeatherState
}

fun weatherDescription(code: Int): String = when (code) {
    0 -> "Açık"
    1 -> "Çoğunlukla açık"
    2 -> "Parçalı bulutlu"
    3 -> "Kapalı"
    45, 48 -> "Sisli"
    in 51..57 -> "Çise"
    in 61..67 -> "Yağmurlu"
    in 71..77 -> "Karlı"
    in 80..82 -> "Sağanak yağış"
    85, 86 -> "Kar sağanağı"
    in 95..99 -> "Gök gürültülü fırtına"
    else -> "Bilinmiyor"
}

fun weatherIcon(code: Int, isDay: Int = 1): String = when {
    code == 0 && isDay == 1 -> "☀️"
    code == 0 -> "🌙"
    code in 1..2 && isDay == 1 -> "⛅"
    code in 1..2 -> "🌙"
    code == 3 -> "☁️"
    code in 45..48 -> "🌫️"
    code in 51..67 -> "🌧️"
    code in 71..77 -> "❄️"
    code in 80..82 -> "🌦️"
    code in 85..86 -> "🌨️"
    code in 95..99 -> "⛈️"
    else -> "🌡️"
}

fun windDirectionLabel(degrees: Int): String {
    val dirs = listOf("K", "KD", "D", "GD", "G", "GB", "B", "KB")
    return dirs[((degrees + 22) % 360) / 45]
}
