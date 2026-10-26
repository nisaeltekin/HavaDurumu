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
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class Precipitation { NONE, RAIN, SNOW }

// ── Zamana Göre Arka Plan Degrade Temaları ───────────────────────────────────

// 1. Sabah (06:00 - 12:00): Güneşli, aydınlık, ferah gökyüzü mavisi
val MorningGradient = listOf(
    Color(0xFF1E528E),
    Color(0xFF2980B9),
    Color(0xFF5DADE2)
)

// 2. İkindi / Gün Batımı (12:00 - 18:00): Sıcak altın sarısı, kehribar ve gün batımı tonları
val AfternoonGradient = listOf(
    Color(0xFF2C3E50),
    Color(0xFF6C3483),
    Color(0xFFBA4A00)
)

// 3. Akşam (18:00 - 24:00): Alacakaranlık, koyu asil akşam laciverti
val EveningGradient = listOf(
    Color(0xFF111827),
    Color(0xFF1B2838),
    Color(0xFF2E4057)
)

// 4. Gece (24:00 - 06:00): Derin, dingin dumanlı gece mavisi ve gri
val NightGradient = listOf(
    Color(0xFF0F172A),
    Color(0xFF141E30),
    Color(0xFF243B55)
)

data class WeatherLook(
    val gradient: List<Color> = NightGradient,
    val precipitation: Precipitation,
    val showSun: Boolean,
    val showStars: Boolean,
    val dayPart: String = "",
    val hour: Int = 12
)

val DefaultLook = WeatherLook(
    gradient = NightGradient,
    precipitation = Precipitation.NONE,
    showSun = false,
    showStars = true,
    dayPart = "Gece",
    hour = 0
)

private fun hourOf(time: String?): Int {
    return time?.substringAfter("T")?.substringBefore(":")?.toIntOrNull()
        ?: LocalTime.now().hour
}

fun weatherLook(w: CurrentWeather): WeatherLook {
    val hour = hourOf(w.time)
    val code = w.weatherCode
    val isDay = w.isDay == 1

    // Kullanıcının belirttiği 4 zaman dilimi:
    // 06:00 - 12:00 -> Sabah
    // 12:00 - 18:00 -> İkindi / Gün Batımı
    // 18:00 - 24:00 -> Akşam
    // 24:00 - 06:00 -> Gece
    val (gradient, part) = when (hour) {
        in 6..11  -> MorningGradient to "Sabah"
        in 12..17 -> AfternoonGradient to "İkindi"
        in 18..23 -> EveningGradient to "Akşam"
        else      -> NightGradient to "Gece"
    }

    val precipitation = when (code) {
        in 51..67, in 80..82, in 95..99 -> Precipitation.RAIN
        in 71..77, 85, 86               -> Precipitation.SNOW
        else                             -> Precipitation.NONE
    }

    val isClear = code <= 2 && precipitation == Precipitation.NONE

    return WeatherLook(
        gradient = gradient,
        precipitation = precipitation,
        showSun = isClear && (hour in 6..17),
        showStars = isClear && (hour !in 6..17),
        dayPart = part,
        hour = hour
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
        initialValue = 0.88f,
        targetValue = 1.12f,
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

    // Dinamik Zamana Göre Degrade Arka Plan
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(look.gradient))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── 1. GÜNEŞ: Sağ üst butonların ("➕ Listeye Ekle" / "Kaldır") altına ASLA denk gelmeyecek konumda ──
            if (look.showSun) {
                val isAfternoon = look.hour in 12..17
                // Butonlar y = 0.08..0.14 aralığındadır.
                // Güneş ise butonların tamamen altında, büyük derece yazısının hemen yanında y = 0.22..0.25'te durur.
                val sunCenter = if (isAfternoon) {
                    Offset(w * 0.76f, h * 0.24f) // İkindi: Batışa doğru daha alçakta
                } else {
                    Offset(w * 0.78f, h * 0.21f) // Sabah: Gökyüzünde ferah güneş
                }
                val baseRadius = w * 0.085f

                val sunCoreColor = if (isAfternoon) Color(0xFFFF9800) else Color(0xFFFFEB3B)
                val sunHaloColor = if (isAfternoon) Color(0xFFFFB74D) else Color(0xFFFFEE58)

                // Güneş Halesi
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(sunHaloColor.copy(alpha = 0.32f), Color.Transparent),
                        center = sunCenter,
                        radius = baseRadius * 3.0f * sunPulse
                    ),
                    radius = baseRadius * 3.0f * sunPulse,
                    center = sunCenter
                )

                // Güneş Gövdesi
                drawCircle(
                    color = sunCoreColor,
                    radius = baseRadius,
                    center = sunCenter
                )
            }

            // ── 2. GECE / AKŞAM İSE: Sakin Yıldızlar ─────────────────────────
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
