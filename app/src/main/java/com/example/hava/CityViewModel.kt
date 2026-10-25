package com.example.hava

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoadState {
    data object Idle : LoadState
    data object Loading : LoadState
    data class Success(val cities: List<CityItem>) : LoadState
    data object Empty : LoadState
    data class Error(val message: String) : LoadState
}

sealed interface LocationState {
    data object Idle : LocationState
    data object NoPermission : LocationState
    data object Loading : LocationState
    data class Success(val city: City, val weather: FullWeather) : LocationState
    data class Error(val message: String) : LocationState
}

// Arama penceresinin hangi amaçla açıldığı
enum class SearchMode {
    ADD_TO_SAVED,  // Sol üstteki "+" butonuyla: Kalıcı olarak listeye eklemek için
    VIEW_ONLY      // Sağ üstteki "🔍" butonuyla: Sadece anlık bakmak için (eklemeden)
}

sealed interface FeedbackUiStatus {
    data object Idle : FeedbackUiStatus
    data object Submitting : FeedbackUiStatus
    data class Success(val message: String) : FeedbackUiStatus
    data class Error(val message: String) : FeedbackUiStatus
}

data class CityUiState(
    val query: String = "",
    val loadState: LoadState = LoadState.Idle,
    val searching: Boolean = false,
    val searchMode: SearchMode = SearchMode.VIEW_ONLY,
    val searchHistory: List<String> = emptyList(),

    // Konum (Sayfa 0)
    val locationState: LocationState = LocationState.Idle,

    // Kalıcı kayıtlı şehirler (Sol üst "+" ile eklenenler)
    val savedCities: List<City> = emptyList(),
    val cityWeathers: Map<Int, WeatherState> = emptyMap(),

    // Sağ üstteki "🔍" ile sadece anlık bakılan geçici şehir (Listeye eklenmez!)
    val previewCity: City? = null,
    val previewWeatherState: WeatherState = WeatherState.Idle,

    // Hangi sekme seçili (0: Konum, 1..N: Kayıtlı Şehirler, -1: Geçici Anlık Bakılan Şehir)
    val currentPageIndex: Int = 0,

    // Geri Bildirim (Hata Raporu) Durumu
    val showFeedbackDialog: Boolean = false,
    val feedbackStatus: FeedbackUiStatus = FeedbackUiStatus.Idle,
    val feedbackTargetLocation: String = "",
    val feedbackTargetTemp: Double? = null,
    val feedbackTargetLat: Double? = null,
    val feedbackTargetLon: Double? = null
)

class CityViewModel(
    private val cityRepository: CityRepository = CityRepository(),
    private val weatherRepository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CityUiState())
    val state: StateFlow<CityUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var locationJob: Job? = null
    private var previewJob: Job? = null
    private lateinit var savedCitiesRepo: SavedCitiesRepository

    fun init(repo: SavedCitiesRepository) {
        if (::savedCitiesRepo.isInitialized) return
        savedCitiesRepo = repo
        val cities = repo.getSavedCities()
        val history = repo.getSearchHistory()
        _state.update { it.copy(savedCities = cities, searchHistory = history) }
        cities.forEach { loadCityWeather(it) }
    }

    // --- Konum ---

    fun onLocationPermissionDenied() {
        _state.update { it.copy(locationState = LocationState.NoPermission) }
    }

    fun loadLocationWeather(locationRepository: LocationRepository) {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            _state.update { it.copy(locationState = LocationState.Loading) }
            try {
                val loc = locationRepository.getCurrentLocation()
                val weather = weatherRepository.getWeather(loc.latitude, loc.longitude)
                val here = City(
                    id = -1,
                    name = loc.district ?: loc.province ?: "Konumun",
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    admin1 = if (loc.district != null) loc.province else null
                )
                _state.update { it.copy(locationState = LocationState.Success(here, weather)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(locationState = LocationState.Error(e.message ?: "Konum alınamadı"))
                }
            }
        }
    }

    // --- Arama Açma Fonksiyonları ---

    /** Sol üstteki "+" butonuna basınca: Şehri kalıcı eklemek için açar */
    fun onOpenAddSearch() {
        _state.update {
            it.copy(
                searching = true,
                searchMode = SearchMode.ADD_TO_SAVED,
                query = "",
                loadState = LoadState.Idle
            )
        }
    }

    /** Sağ üstteki "🔍" butonuna basınca: Sadece anlık bakmak için açar */
    fun onOpenViewSearch() {
        _state.update {
            it.copy(
                searching = true,
                searchMode = SearchMode.VIEW_ONLY,
                query = "",
                loadState = LoadState.Idle
            )
        }
    }

    fun onSearchClose() {
        _state.update { it.copy(searching = false) }
    }

    // --- Arama Sonucundan Şehir Seçimi ---

    fun onCitySelectedFromSearch(city: City) {
        val mode = _state.value.searchMode
        savedCitiesRepo.addToSearchHistory(city.name)

        if (mode == SearchMode.ADD_TO_SAVED) {
            // Sol üst "+" ile geldiyse: Kalıcı listeye ekle ve o sekmeye geç
            addCity(city)
            _state.update {
                it.copy(
                    searching = false,
                    previewCity = null,
                    searchHistory = savedCitiesRepo.getSearchHistory()
                )
            }
        } else {
            // Sağ üst "🔍" ile geldiyse: Listeye EKLEME, sadece geçici olarak ekranda göster
            loadPreviewCityWeather(city)
            _state.update {
                it.copy(
                    searching = false,
                    previewCity = city,
                    currentPageIndex = -1, // -1: Geçici önizleme modu
                    searchHistory = savedCitiesRepo.getSearchHistory()
                )
            }
        }
    }

    // --- Geçici Bakılan Şehri Kalıcı Listeye Ekleme (İsterse diye) ---
    fun saveCurrentPreviewCity() {
        val city = _state.value.previewCity ?: return
        addCity(city)
        _state.update { it.copy(previewCity = null) }
    }

    // --- Kalıcı Şehir Yönetimi ---

    fun addCity(city: City) {
        if (_state.value.savedCities.any { it.id == city.id }) {
            navigateToSavedCity(city.id)
            return
        }
        savedCitiesRepo.saveCity(city)
        val newCities = _state.value.savedCities + city
        _state.update { it.copy(savedCities = newCities, previewCity = null) }
        loadCityWeather(city)
        viewModelScope.launch {
            delay(50)
            _state.update { it.copy(currentPageIndex = it.savedCities.size) }
        }
    }

    fun removeCity(cityId: Int) {
        savedCitiesRepo.removeCity(cityId)
        val newCities = _state.value.savedCities.filter { it.id != cityId }
        val newWeathers = _state.value.cityWeathers - cityId
        val newPage = _state.value.currentPageIndex.coerceAtMost(newCities.size)
        _state.update {
            it.copy(savedCities = newCities, cityWeathers = newWeathers, currentPageIndex = newPage)
        }
    }

    fun setPage(index: Int) {
        _state.update { it.copy(currentPageIndex = index, previewCity = null) }
    }

    fun retryCityWeather(city: City) = loadCityWeather(city)
    fun retryLocationWeather(locationRepository: LocationRepository) = loadLocationWeather(locationRepository)
    fun retryPreviewWeather() {
        _state.value.previewCity?.let { loadPreviewCityWeather(it) }
    }

    // --- Arama Metni Değişimi ---

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        search(newQuery)
    }

    fun retrySearch() = search(_state.value.query)

    private fun search(query: String) {
        searchJob?.cancel()
        val text = query.trim()
        if (text.length < 2) {
            _state.update { it.copy(loadState = LoadState.Idle) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _state.update { it.copy(loadState = LoadState.Loading) }
            try {
                val result = cityRepository.searchCities(text)
                _state.update {
                    it.copy(loadState = if (result.isEmpty()) LoadState.Empty else LoadState.Success(result))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loadState = LoadState.Error(e.message ?: "Hata")) }
            }
        }
    }

    private fun navigateToSavedCity(cityId: Int) {
        val idx = _state.value.savedCities.indexOfFirst { it.id == cityId }
        if (idx >= 0) _state.update { it.copy(currentPageIndex = idx + 1, previewCity = null) }
    }

    private fun loadCityWeather(city: City) {
        viewModelScope.launch {
            _state.update { it.copy(cityWeathers = it.cityWeathers + (city.id to WeatherState.Loading)) }
            try {
                val weather = weatherRepository.getWeather(city.latitude, city.longitude)
                _state.update { it.copy(cityWeathers = it.cityWeathers + (city.id to WeatherState.Success(weather))) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(cityWeathers = it.cityWeathers + (city.id to WeatherState.Error(e.message ?: "Hata")))
                }
            }
        }
    }

    private fun loadPreviewCityWeather(city: City) {
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            _state.update { it.copy(previewWeatherState = WeatherState.Loading) }
            try {
                val weather = weatherRepository.getWeather(city.latitude, city.longitude)
                _state.update { it.copy(previewWeatherState = WeatherState.Success(weather)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(previewWeatherState = WeatherState.Error(e.message ?: "Hata"))
                }
            }
        }
    }

    // --- Geri Bildirim (Feedback) İşlemleri ---

    fun openFeedbackDialog(location: String, temp: Double?, lat: Double?, lon: Double?) {
        _state.update {
            it.copy(
                showFeedbackDialog = true,
                feedbackStatus = FeedbackUiStatus.Idle,
                feedbackTargetLocation = location,
                feedbackTargetTemp = temp,
                feedbackTargetLat = lat,
                feedbackTargetLon = lon
            )
        }
    }

    fun closeFeedbackDialog() {
        _state.update {
            it.copy(
                showFeedbackDialog = false,
                feedbackStatus = FeedbackUiStatus.Idle
            )
        }
    }

    fun submitFeedback(
        repo: FeedbackRepository,
        condition: String,
        comment: String?
    ) {
        val current = _state.value
        val req = FeedbackRequest(
            location = current.feedbackTargetLocation,
            latitude = current.feedbackTargetLat,
            longitude = current.feedbackTargetLon,
            reportedCondition = condition,
            currentTemp = current.feedbackTargetTemp,
            userComment = comment
        )

        viewModelScope.launch {
            _state.update { it.copy(feedbackStatus = FeedbackUiStatus.Submitting) }
            val result = repo.submitFeedback(req)
            result.onSuccess { msg ->
                _state.update { it.copy(feedbackStatus = FeedbackUiStatus.Success(msg)) }
                delay(1500)
                _state.update { it.copy(showFeedbackDialog = false, feedbackStatus = FeedbackUiStatus.Idle) }
            }.onFailure { err ->
                _state.update { it.copy(feedbackStatus = FeedbackUiStatus.Error(err.message ?: "Gönderilemedi")) }
            }
        }
    }
}
