package com.example.hava

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SavedCitiesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("hava_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SAVED_CITIES = "saved_cities"
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val MAX_HISTORY = 8
    }

    fun getSavedCities(): List<City> {
        val json = prefs.getString(KEY_SAVED_CITIES, "[]") ?: "[]"
        return try {
            gson.fromJson(json, object : TypeToken<List<City>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCity(city: City) {
        val list = getSavedCities().toMutableList()
        if (list.none { it.id == city.id }) {
            list.add(city)
            persist(KEY_SAVED_CITIES, list)
        }
    }

    fun removeCity(cityId: Int) {
        val list = getSavedCities().filter { it.id != cityId }
        persist(KEY_SAVED_CITIES, list)
    }

    fun getSearchHistory(): List<String> {
        val json = prefs.getString(KEY_SEARCH_HISTORY, "[]") ?: "[]"
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addToSearchHistory(query: String) {
        if (query.isBlank() || query.length < 2) return
        val list = getSearchHistory().toMutableList().also {
            it.remove(query)
            it.add(0, query)
            if (it.size > MAX_HISTORY) it.subList(MAX_HISTORY, it.size).clear()
        }
        persist(KEY_SEARCH_HISTORY, list)
    }

    private fun persist(key: String, value: Any) {
        prefs.edit().putString(key, gson.toJson(value)).apply()
    }
}
