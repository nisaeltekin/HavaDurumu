package com.example.hava

class WeatherRepository(
    private val api: WeatherApi = ApiClient.weatherApi
) {
    suspend fun getWeather(latitude: Double, longitude: Double): FullWeather {
        val response = api.getCurrentWeather(latitude, longitude)
        val current = response.current
            ?: throw IllegalStateException("Hava durumu verisi alınamadı")

        val hourly = buildList {
            val raw = response.hourly ?: return@buildList
            val n = minOf(raw.time.size, raw.temperature.size, raw.weatherCode.size)
            repeat(n) { i ->
                val timeStr = raw.time[i]
                // API'den gelen is_day verisi (1: Gündüz, 0: Gece), yoksa saatten hesapla
                val isDayVal = raw.isDay.getOrNull(i) ?: run {
                    val hour = timeStr.substringAfter("T").substringBefore(":").toIntOrNull() ?: 12
                    if (hour in 6..19) 1 else 0
                }

                add(
                    HourlyWeather(
                        time = timeStr,
                        temperature = raw.temperature[i],
                        weatherCode = raw.weatherCode[i],
                        precipitationProbability = raw.precipitationProbability.getOrElse(i) { 0 },
                        isDay = isDayVal
                    )
                )
            }
        }

        val daily = buildList {
            val raw = response.daily ?: return@buildList
            val n = minOf(raw.time.size, raw.temperatureMax.size, raw.temperatureMin.size)
            repeat(n) { i ->
                add(
                    DailyWeather(
                        time = raw.time[i],
                        temperatureMax = raw.temperatureMax[i],
                        temperatureMin = raw.temperatureMin[i],
                        weatherCode = raw.weatherCode.getOrElse(i) { 0 },
                        precipitationProbabilityMax = raw.precipitationProbabilityMax.getOrElse(i) { 0 }
                    )
                )
            }
        }

        return FullWeather(current = current, hourly = hourly, daily = daily)
    }
}
