package com.example.hava

import com.google.gson.annotations.SerializedName

/**
 * Kullanıcının bildirdiği hava durumu verisi
 */
data class FeedbackRequest(
    @SerializedName("location")
    val location: String,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("reportedCondition")
    val reportedCondition: String,

    @SerializedName("currentTemp")
    val currentTemp: Double? = null,

    @SerializedName("userComment")
    val userComment: String? = null,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

data class FeedbackResponse(
    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("id")
    val id: String? = null
)

/**
 * Hızlı seçim için hazır hava durumu seçenekleri
 */
data class ConditionOption(
    val icon: String,
    val label: String
)

val DefaultConditionOptions = listOf(
    ConditionOption("☀️", "Açık / Güneşli"),
    ConditionOption("⛅", "Parçalı Bulutlu"),
    ConditionOption("☁️", "Kapalı / Bulutlu"),
    ConditionOption("🌧️", "Yağmurlu"),
    ConditionOption("❄️", "Karlı"),
    ConditionOption("🌫️", "Sisli"),
    ConditionOption("⛈️", "Fırtına / Şimşek")
)
