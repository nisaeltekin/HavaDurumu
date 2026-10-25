package com.example.hava

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class Precipitation { NONE, RAIN, SNOW }

// Gemlik'te görünen o dumanlı gri - koyu lacivert arası asil arka plan rengi
val FixedAppGradient = listOf(Color(0xFF141E30), Color(0xFF243B55))

data class WeatherLook(
    val gradient: List<Color> = FixedAppGradient,
    val precipitation: Precipitation,
    val showSun: Boolean,
    val showStars: Boolean,
    val dayPart: String = ""
)

val DefaultLook = WeatherLook(
    gradient = FixedAppGradient,
    precipitation = Precipitation.NONE,
    showSun = false,
    showStars = true,
    dayPart = "Gece"
)

private fun hourOf(time: String?): Int =
    time?.substringAfter("T")?.substringBefore(":")?.toIntOrNull() ?: 12

fun weatherLook(w: CurrentWeather): WeatherLook {
    val hour = hourOf(w.time)
    val code = w.weatherCode
    val isDay = w.isDay == 1

    val part = when {
        isDay && hour in 6..10   -> "Sabah"
        isDay && hour in 11..16  -> "Öğle"
        isDay && hour in 17..19  -> "Akşam"
        !isDay && hour in 18..21 -> "Akşam"
        else                     -> "Gece"
    }

    val precipitation = when (code) {
        in 51..67, in 80..82, in 95..99 -> Precipitation.RAIN
        in 71..77, 85, 86               -> Precipitation.SNOW
        else                             -> Precipitation.NONE
    }

    val isClear = code <= 1 && precipitation == Precipitation.NONE

    return WeatherLook(
        gradient = FixedAppGradient,
        precipitation = precipitation,
        showSun = isClear && isDay,
        showStars = isClear && !isDay,
        dayPart = part
    )
}

// ── Parçacık Modelleri ───────────────────────────────────────────────────────

private data class RainDrop(
    val x: Float,
    val phase: Float,
    val speed: Float,
    val length: Float
)

private data class Snowflake(
    val x: Float,
    val phase: Float,
    val speed: Float,
    val radius: Float
)

private data class Star(
    val x: Float,
    val y: Float,
    val phase: Float,
    val radius: Float
)

@Composable
fun WeatherBackground(
    look: WeatherLook,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnim")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "time"
    )

    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "sunPulse"
    )

    // Yağmur damlaları
    val rainDrops = remember {
        val rnd = Random(101)
        List(60) {
            RainDrop(
                x = rnd.nextFloat(),
                phase = rnd.nextFloat(),
                speed = 2.0f + rnd.nextFloat() * 1.5f,
                length = 25f + rnd.nextFloat() * 15f
            )
        }
    }

    // Kar taneleri
    val snowflakes = remember {
        val rnd = Random(202)
        List(45) {
            Snowflake(
                x = rnd.nextFloat(),
                phase = rnd.nextFloat(),
                speed = 0.6f + rnd.nextFloat() * 0.8f,
                radius = 2.5f + rnd.nextFloat() * 2.5f
            )
        }
    }

    // Gece sakin parıldayan küçük yıldızlar
    val stars = remember {
        val rnd = Random(303)
        List(35) {
            Star(
                x = rnd.nextFloat(),
                y = rnd.nextFloat() * 0.55f,
                phase = rnd.nextFloat(),
                radius = 1.0f + rnd.nextFloat() * 1.2f
            )
        }
    }

    // Sabit Gri - Koyu Lacivert Degradeli Arka Plan
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(FixedAppGradient))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── 1. GÜNEŞLİ İSE: Sade Güneş ──────────────────────────────────
            if (look.showSun) {
                val sunCenter = Offset(w * 0.82f, h * 0.12f)
                val baseRadius = w * 0.09f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEE58).copy(alpha = 0.35f), Color.Transparent),
                        center = sunCenter,
                        radius = baseRadius * 3.5f * sunPulse
                    ),
                    radius = baseRadius * 3.5f * sunPulse,
                    center = sunCenter
                )

                drawCircle(
                    color = Color(0xFFFFEB3B),
                    radius = baseRadius,
                    center = sunCenter
                )
            }

            // ── 2. GECE İSE: Sadece Sakin Yıldızlar ─────────────────────────
            if (look.showStars) {
                stars.forEach { s ->
                    val twinkle = 0.25f + 0.65f * ((sin((time * 2f + s.phase) * (2 * PI.toFloat())) + 1f) / 2f)
                    drawCircle(
                        color = Color.White.copy(alpha = twinkle.coerceIn(0.15f, 0.85f)),
                        radius = s.radius,
                        center = Offset(s.x * w, s.y * h)
                    )
                }
            }

            // ── 3. YAĞMURLU İSE: İnce Yağmur Çizgileri ──────────────────────
            if (look.precipitation == Precipitation.RAIN) {
                rainDrops.forEach { drop ->
                    val yProgress = (time * drop.speed + drop.phase) % 1f
                    val y = yProgress * (h + 80f) - 40f
                    val x = drop.x * w - (y * 0.12f)

                    drawLine(
                        color = Color.White.copy(alpha = 0.45f),
                        start = Offset(x, y),
                        end = Offset(x - 4f, y + drop.length),
                        strokeWidth = 2.2f
                    )
                }
            }

            // ── 4. KARLI İSE: Süzülen Kar Taneleri ───────────────────────────
            if (look.precipitation == Precipitation.SNOW) {
                snowflakes.forEach { flake ->
                    val yProgress = (time * flake.speed + flake.phase) % 1f
                    val y = yProgress * (h + 40f) - 20f
                    val sway = sin((yProgress * 4f + flake.phase) * (2 * PI.toFloat())) * 16f
                    val x = flake.x * w + sway

                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = flake.radius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
