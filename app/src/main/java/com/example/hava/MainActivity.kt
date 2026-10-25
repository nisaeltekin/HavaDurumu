package com.example.hava

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Alt sistem çubuğunun (navigation bar) tamamen şeffaf olması ve uygulamanın arkasına düşmesi
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        // Android 10+ sistem kontrast zorlamasını kapatıyoruz (alt çubuğun beyaz/gri kalmasını önler)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate")
        setContent {
            MaterialTheme {
                CityListScreen()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANA EKRAN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CityListScreen(viewModel: CityViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val savedCitiesRepo = remember { SavedCitiesRepository(context) }
    val locationRepository = remember { LocationRepository(context) }
    val feedbackRepository = remember { FeedbackRepository(context) }

    LaunchedEffect(Unit) {
        viewModel.init(savedCitiesRepo)
    }

    // Arama açıksa, anlık bakılan şehir varsa veya geri bildirim penceresi açıksa geri tuşu kapatsın
    BackHandler(enabled = state.searching || state.previewCity != null || state.showFeedbackDialog) {
        if (state.showFeedbackDialog) {
            viewModel.closeFeedbackDialog()
        } else if (state.searching) {
            viewModel.onSearchClose()
        } else if (state.previewCity != null) {
            viewModel.setPage(0)
        }
    }

    // İzin isteyici
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.loadLocationWeather(locationRepository)
        else viewModel.onLocationPermissionDenied()
    }

    LaunchedEffect(Unit) {
        if (state.locationState is LocationState.Idle) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) viewModel.loadLocationWeather(locationRepository)
            else permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    // Aktif havanın belirlenmesi
    val isPreviewMode = state.previewCity != null
    val isLocationSelected = !isPreviewMode && state.currentPageIndex == 0
    val selectedCity = if (!isPreviewMode && !isLocationSelected) {
        state.savedCities.getOrNull(state.currentPageIndex - 1)
    } else null

    val currentFullWeather: FullWeather? = when {
        isPreviewMode -> (state.previewWeatherState as? WeatherState.Success)?.weather
        isLocationSelected -> (state.locationState as? LocationState.Success)?.weather
        else -> selectedCity?.let { (state.cityWeathers[it.id] as? WeatherState.Success)?.weather }
    }

    val look = currentFullWeather?.current?.let { weatherLook(it) } ?: DefaultLook

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Sade ve Dinamik Arka Plan (Ekranın en altına, menü tuşlarının arkasına kadar uzanır) ──
        WeatherBackground(look = look, modifier = Modifier.fillMaxSize())

        // ── 2. Ana Sayfa İçeriği ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Üst Bar: [➕ Sol Üstte] | [Şehir Sekmeleri] | [🔍 Sağ Üstte]
            TopNavBar(
                state = state,
                onAddClick = { viewModel.onOpenAddSearch() },
                onViewClick = { viewModel.onOpenViewSearch() },
                onSelectLocation = { viewModel.setPage(0) },
                onSelectCity = { idx -> viewModel.setPage(idx + 1) }
            )

            // Dikey Kaydırılabilir Hava Durumu İçeriği
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp)
            ) {
                if (isPreviewMode && state.previewCity != null) {
                    // SAĞ ÜSTTEKİ "🔍" İLE ANLIK BAKILAN ŞEHİR / İLÇE (LİSTEYE EKLENMEDİ)
                    val city = state.previewCity!!
                    val province = getProvinceOfDistrict(city.name)
                    val subtitle = if (province != null) "🇹🇷 $province, Türkiye (Anlık Bakış)" else "🇹🇷 Türkiye (Anlık Bakış)"

                    CityHeaderTitle(
                        cityName = city.name,
                        subtitle = subtitle,
                        actionText = "➕ Listeye Ekle",
                        onAction = { viewModel.saveCurrentPreviewCity() }
                    )

                    when (val pwState = state.previewWeatherState) {
                        is WeatherState.Idle, is WeatherState.Loading -> LoadingContent()
                        is WeatherState.Error -> ErrorContent(message = pwState.message, onRetry = {
                            viewModel.retryPreviewWeather()
                        })
                        is WeatherState.Success -> {
                            WeatherContent(
                                fullWeather = pwState.weather,
                                look = look,
                                onReportWeather = {
                                    viewModel.openFeedbackDialog(
                                        location = city.name,
                                        temp = pwState.weather.current.temperature,
                                        lat = city.latitude,
                                        lon = city.longitude
                                    )
                                }
                            )
                        }
                    }
                } else if (isLocationSelected) {
                    // MEVCUT KONUM EKRANI
                    when (val locState = state.locationState) {
                        is LocationState.Idle, is LocationState.Loading -> LoadingContent()
                        is LocationState.NoPermission -> PermissionContent(onRequestPermission = {
                            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        })
                        is LocationState.Error -> ErrorContent(message = locState.message, onRetry = {
                            viewModel.retryLocationWeather(locationRepository)
                        })
                        is LocationState.Success -> {
                            val city = locState.city
                            val province = getProvinceOfDistrict(city.name)
                            val subtitle = if (province != null) "🇹🇷 $province, Türkiye" else "🇹🇷 Türkiye"

                            CityHeaderTitle(
                                cityName = city.name,
                                subtitle = subtitle
                            )
                            WeatherContent(
                                fullWeather = locState.weather,
                                look = look,
                                onReportWeather = {
                                    viewModel.openFeedbackDialog(
                                        location = city.name,
                                        temp = locState.weather.current.temperature,
                                        lat = city.latitude,
                                        lon = city.longitude
                                    )
                                }
                            )
                        }
                    }
                } else if (selectedCity != null) {
                    // SOL ÜSTTEKİ "+" İLE LİSTEYE EKLENMİŞ ŞEHİR / İLÇE EKRANI
                    val province = getProvinceOfDistrict(selectedCity.name)
                    val subtitle = if (province != null) "🇹🇷 $province, Türkiye" else "🇹🇷 Türkiye"

                    CityHeaderTitle(
                        cityName = selectedCity.name,
                        subtitle = subtitle,
                        actionText = "Kaldır",
                        onAction = { viewModel.removeCity(selectedCity.id) }
                    )

                    when (val cityWeather = state.cityWeathers[selectedCity.id] ?: WeatherState.Idle) {
                        is WeatherState.Idle, is WeatherState.Loading -> LoadingContent()
                        is WeatherState.Error -> ErrorContent(message = cityWeather.message, onRetry = {
                            viewModel.retryCityWeather(selectedCity)
                        })
                        is WeatherState.Success -> {
                            WeatherContent(
                                fullWeather = cityWeather.weather,
                                look = look,
                                onReportWeather = {
                                    viewModel.openFeedbackDialog(
                                        location = selectedCity.name,
                                        temp = cityWeather.weather.current.temperature,
                                        lat = selectedCity.latitude,
                                        lon = selectedCity.longitude
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── 3. Arama Katmanı (Overlay) ──
        AnimatedVisibility(
            visible = state.searching,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            SearchOverlay(
                state = state,
                onQueryChange = { viewModel.onQueryChange(it) },
                onCitySelected = { viewModel.onCitySelectedFromSearch(it) },
                onRetry = { viewModel.retrySearch() },
                onClose = { viewModel.onSearchClose() }
            )
        }

        // ── 4. Geri Bildirim / Hata Raporlama Penceresi ──
        if (state.showFeedbackDialog) {
            FeedbackDialog(
                state = state,
                onDismiss = { viewModel.closeFeedbackDialog() },
                onSubmit = { condition, comment ->
                    viewModel.submitFeedback(feedbackRepository, condition, comment)
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ÜST BAR: [➕ Sol Üstte] | [Şehir Sekmeleri] | [🔍 Sağ Üstte]
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TopNavBar(
    state: CityUiState,
    onAddClick: () -> Unit,
    onViewClick: () -> Unit,
    onSelectLocation: () -> Unit,
    onSelectCity: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. SOL ÜSTTE: "+" Butonu (Listeye Ekleme)
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.20f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                .clickable(onClick = onAddClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Listeye Şehir/İlçe Ekle",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        // 2. ORTADA: Yatay Kaydırılabilir Şehir Sekmeleri
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Konum Sekmesi
            item {
                val isSelected = state.previewCity == null && state.currentPageIndex == 0
                val locCity = (state.locationState as? LocationState.Success)?.city
                val label = locCity?.name?.let { "📍 $it" } ?: "📍 Konum"

                CityTabPill(
                    text = label,
                    isSelected = isSelected,
                    isProvince = true,
                    onClick = onSelectLocation
                )
            }

            // Kalıcı Eklenen İller ve İlçeler
            itemsIndexed(state.savedCities) { index, city ->
                val isSelected = state.previewCity == null && state.currentPageIndex == (index + 1)
                val isDistrict = getProvinceOfDistrict(city.name) != null

                CityTabPill(
                    text = city.name,
                    isSelected = isSelected,
                    isProvince = !isDistrict,
                    onClick = { onSelectCity(index) }
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // 3. SAĞ ÜSTTE: "🔍" Butonu (Sadece Anlık Bakmak İçin)
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.20f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                .clickable(onClick = onViewClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Hava Durumuna Bak (Anlık)",
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
fun CityTabPill(
    text: String,
    isSelected: Boolean,
    isProvince: Boolean,
    onClick: () -> Unit
) {
    val baseAlpha = when {
        isSelected -> 0.38f
        isProvince -> 0.18f
        else       -> 0.10f
    }

    val borderAlpha = if (isSelected) 0.65f else if (isProvince) 0.30f else 0.20f

    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = baseAlpha), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BAŞLIK (Şehir Adı + İl, Türkiye)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CityHeaderTitle(
    cityName: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.80f)
            )
        }

        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HAVA DURUMU DETAY İÇERİĞİ
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherContent(
    fullWeather: FullWeather,
    look: WeatherLook,
    onReportWeather: () -> Unit
) {
    val w = fullWeather.current

    // Büyük Sıcaklık Bloğu
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = "${w.temperature.roundToInt()}°",
            fontSize = 92.sp,
            fontWeight = FontWeight.Thin,
            color = Color.White,
            lineHeight = 92.sp
        )

        Text(
            text = weatherDescription(w.weatherCode),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.95f),
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = buildString {
                append("Hissedilen ${w.apparentTemperature.roundToInt()}°")
                fullWeather.daily.firstOrNull()?.let {
                    append("  ·  En Yüksek ${it.temperatureMax.roundToInt()}°  En Düşük ${it.temperatureMin.roundToInt()}°")
                }
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.80f)
        )
    }

    Spacer(Modifier.height(24.dp))

    // 4'lü Şeffaf Cam Metrik Kartları
    MetricGrid(w = w)

    Spacer(Modifier.height(18.dp))

    // 24 Saatlik Tahmin
    if (fullWeather.hourly.isNotEmpty()) {
        HourlyForecastSection(hourly = fullWeather.hourly)
    }

    Spacer(Modifier.height(16.dp))

    // 5 Günlük Tahmin
    if (fullWeather.daily.isNotEmpty()) {
        DailyForecastSection(daily = fullWeather.daily)
    }

    Spacer(Modifier.height(18.dp))

    // ── Hata / Gerçek Hava Durumu Bildir Butonu ──
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(24.dp))
                .clickable { onReportWeather() }
                .padding(horizontal = 20.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📢", fontSize = 15.sp)
                Text(
                    text = "Hava durumu yanlış mı? Bildir",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// METRİK KARTLARI (2x2 Grid)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MetricGrid(w: CurrentWeather) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(modifier = Modifier.weight(1f), icon = "💧", label = "Nem", value = "%${w.humidity}")
            MetricCard(
                modifier = Modifier.weight(1f),
                icon = "💨",
                label = "Rüzgar",
                value = "${w.windSpeed.roundToInt()} km/s",
                sub = windDirectionLabel(w.windDirection)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(modifier = Modifier.weight(1f), icon = "🌡️", label = "Basınç", value = "${w.pressure.roundToInt()} hPa")
            MetricCard(modifier = Modifier.weight(1f), icon = "🌧️", label = "Yağış", value = "${w.precipitation} mm")
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, icon: String, label: String, value: String, sub: String? = null) {
    Column(
        modifier = modifier
            .glassBackground()
            .padding(14.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.70f), style = MaterialTheme.typography.labelMedium)
        Text(text = value, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (!sub.isNullOrBlank()) {
            Text(text = sub, color = Color.White.copy(alpha = 0.70f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 24 SAATLİK TAHMİN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HourlyForecastSection(hourly: List<HourlyWeather>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassBackground()
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = "24 SAATLİK TAHMİN",
            color = Color.White.copy(alpha = 0.65f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(hourly) { h ->
                val timeLabel = h.time.substringAfter("T").take(5)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = timeLabel, color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(6.dp))
                    Text(text = weatherIcon(h.weatherCode, h.isDay), fontSize = 20.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(text = "${h.temperature.roundToInt()}°", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5 GÜNLÜK TAHMİN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DailyForecastSection(daily: List<DailyWeather>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassBackground()
            .padding(16.dp)
    ) {
        Text(
            text = "${daily.size} GÜNLÜK TAHMİN",
            color = Color.White.copy(alpha = 0.65f),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(10.dp))

        daily.forEachIndexed { idx, d ->
            val dayLabel = if (idx == 0) "Bugün" else try {
                LocalDate.parse(d.time)
                    .dayOfWeek
                    .getDisplayName(TextStyle.FULL, Locale("tr"))
                    .replaceFirstChar { it.uppercase() }
            } catch (e: Exception) {
                d.time
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayLabel,
                    color = Color.White,
                    modifier = Modifier.width(90.dp),
                    fontWeight = if (idx == 0) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = weatherIcon(d.weatherCode),
                    fontSize = 18.sp,
                    modifier = Modifier.width(36.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${d.temperatureMin.roundToInt()}°",
                    color = Color.White.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Light
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "${d.temperatureMax.roundToInt()}°",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (idx < daily.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ARAMA EKRANI (OVERLAY) — KOYU LACİVERT ARKA PLAN & YENİLENMİŞ METİN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SearchOverlay(
    state: CityUiState,
    onQueryChange: (String) -> Unit,
    onCitySelected: (City) -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val isAddMode = state.searchMode == SearchMode.ADD_TO_SAVED

    // Koyu Asil Lacivert Arka Plan
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1128).copy(alpha = 0.97f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp)
        ) {
            // Arama Başlığı
            Text(
                text = if (isAddMode) "➕ Şehir / İlçe Ekle" else "🔍 Hava Durumuna Bak",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
            )

            // Arama Kutusu + Kapat Butonu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = if (isAddMode) "Eklemek için il veya ilçe ara..." else "Bakmak için il veya ilçe ara...",
                            color = Color.White.copy(alpha = 0.50f),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isAddMode) Icons.Default.Add else Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.70f)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onRetry(); keyboard?.hide() }
                    ),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.50f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                }
            }

            // Arama Durumları
            when (val loadState = state.loadState) {
                is LoadState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aramak için en az 2 harf yazın", color = Color.White.copy(alpha = 0.60f))
                    }
                }
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is LoadState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sonuç bulunamadı", color = Color.White.copy(alpha = 0.60f))
                    }
                }
                is LoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bir hata oluştu", color = Color.White)
                            TextButton(onClick = onRetry) { Text("Tekrar dene", color = Color.White) }
                        }
                    }
                }
                is LoadState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(loadState.cities, key = { it.city.id }) { item ->
                            SearchResultItem(
                                item = item,
                                onClick = { onCitySelected(item.city) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(item: CityItem, onClick: () -> Unit) {
    val city = item.city
    val province = getProvinceOfDistrict(city.name)
    val isDistrict = province != null

    val plateNumber = getPlateCodeForLocation(city.name)
    val plate = plateNumber?.toString()?.padStart(2, '0') ?: "--"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = if (isDistrict) 0.08f else 0.14f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Yuvarlak Plaka Rozeti
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color.White.copy(alpha = 0.20f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = plate,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(city.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            val subtitle = if (province != null) "🇹🇷 $province, Türkiye" else "🇹🇷 Türkiye"
            Text(subtitle, color = Color.White.copy(alpha = 0.70f), style = MaterialTheme.typography.bodySmall)
        }

        item.temperature?.let {
            Text(
                text = "${it.roundToInt()}°",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light
            )
        } ?: Text("–", color = Color.White.copy(alpha = 0.40f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// YARDIMCI BİLEŞENLER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun PermissionContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .glassBackground()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📍", fontSize = 38.sp)
        Spacer(Modifier.height(10.dp))
        Text("Konum İzni Gerekli", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Bulunduğunuz konumun havasını otomatik görmek için konum izni verin.",
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.40f), RoundedCornerShape(20.dp))
                .clickable(onClick = onRequestPermission)
                .padding(horizontal = 22.dp, vertical = 9.dp)
        ) {
            Text("Konum İzni Ver", color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .glassBackground()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚠️", fontSize = 32.sp)
        Spacer(Modifier.height(8.dp))
        Text(message, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onRetry) {
            Text("Tekrar Dene", color = Color.White)
        }
    }
}

fun Modifier.glassBackground(): Modifier = this
    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
    .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp))

// ─────────────────────────────────────────────────────────────────────────────
// GERİ BİLDİRİM / HATA RAPORLAMA DİYALOĞU
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FeedbackDialog(
    state: CityUiState,
    onDismiss: () -> Unit,
    onSubmit: (condition: String, comment: String) -> Unit
) {
    var selectedCondition by remember { mutableStateOf(DefaultConditionOptions.first().label) }
    var userComment by remember { mutableStateOf("") }
    val isSubmitting = state.feedbackStatus is FeedbackUiStatus.Submitting
    val successStatus = state.feedbackStatus as? FeedbackUiStatus.Success
    val errorStatus = state.feedbackStatus as? FeedbackUiStatus.Error

    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF131D2E).copy(alpha = 0.98f))
                .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            if (successStatus != null) {
                // Başarı Durumu
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Bildiriminiz Alındı!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = successStatus.message,
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Üst Başlık
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hava Durumunu Bildir",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            val tempStr = state.feedbackTargetTemp?.let { " (${it.roundToInt()}°C gösteriliyor)" } ?: ""
                            Text(
                                text = "📍 ${state.feedbackTargetLocation}$tempStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isSubmitting,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Burada hava şu an gerçekte nasıl?",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(10.dp))

                    // Hava durumu seçenekleri (Çipler)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DefaultConditionOptions.take(2).forEach { opt ->
                                ConditionChip(
                                    option = opt,
                                    isSelected = selectedCondition == opt.label,
                                    onClick = { selectedCondition = opt.label },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DefaultConditionOptions.drop(2).take(2).forEach { opt ->
                                ConditionChip(
                                    option = opt,
                                    isSelected = selectedCondition == opt.label,
                                    onClick = { selectedCondition = opt.label },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DefaultConditionOptions.drop(4).take(3).forEach { opt ->
                                ConditionChip(
                                    option = opt,
                                    isSelected = selectedCondition == opt.label,
                                    onClick = { selectedCondition = opt.label },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Ek açıklama (İsteğe bağlı)",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = userComment,
                        onValueChange = { userComment = it },
                        placeholder = {
                            Text(
                                "Örn: Burada yağmur yağmıyor, hava günlük güneşlik...",
                                color = Color.White.copy(alpha = 0.40f),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                            cursorColor = Color(0xFF38BDF8)
                        ),
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                    )

                    if (errorStatus != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorStatus.message,
                            color = Color(0xFFF87171),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // Butonlar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                            Text("Vazgeç", color = Color.White.copy(alpha = 0.65f))
                        }

                        Spacer(Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF0284C7))
                                .clickable(enabled = !isSubmitting) {
                                    onSubmit(selectedCondition, userComment)
                                }
                                .padding(horizontal = 22.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Gönder", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConditionChip(
    option: ConditionOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
    val border = if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.15f)
    val textColor = if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.90f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = option.icon, fontSize = 14.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text = option.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
