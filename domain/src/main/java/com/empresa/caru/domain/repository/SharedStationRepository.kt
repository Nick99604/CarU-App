package com.empresa.caru.domain.repository

import com.empresa.caru.domain.model.FoodStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single Source of Truth para puestos de comida y favoritos.
 * Repository compartido entre todos los ViewModels.
 */
object SharedStationRepository {

    private val _stations = MutableStateFlow<List<FoodStation>>(emptyList())
    val stations: StateFlow<List<FoodStation>> = _stations.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _savedStationIds = MutableStateFlow<Set<String>>(emptySet())
    val savedStationIds: StateFlow<Set<String>> = _savedStationIds.asStateFlow()

    fun addStation(station: FoodStation) {
        _stations.value = _stations.value + station
    }

    /**
     * Reemplaza todas las estaciones (usado para cargar desde Firestore).
     */
    fun setStations(stations: List<FoodStation>) {
        _stations.value = stations
    }

    fun getStationById(id: String): FoodStation? {
        return _stations.value.find { it.id == id }
    }

    fun toggleFavorite(stationId: String) {
        _favoriteIds.value = if (stationId in _favoriteIds.value) {
            _favoriteIds.value - stationId
        } else {
            _favoriteIds.value + stationId
        }
    }

    fun isFavorite(stationId: String): Boolean {
        return stationId in _favoriteIds.value
    }

    fun getFavoriteStations(): List<FoodStation> {
        val favoriteIdsSet = _favoriteIds.value
        return _stations.value.filter { it.id in favoriteIdsSet }
    }

    fun toggleSavedStation(stationId: String) {
        _savedStationIds.value = if (stationId in _savedStationIds.value) {
            _savedStationIds.value - stationId
        } else {
            _savedStationIds.value + stationId
        }
    }

    fun isSavedStation(stationId: String): Boolean {
        return stationId in _savedStationIds.value
    }

    fun setSavedStationIds(ids: Set<String>) {
        _savedStationIds.value = ids
    }

    fun getSavedStations(): List<FoodStation> {
        val savedIds = _savedStationIds.value
        return _stations.value.filter { it.id in savedIds }
    }
}
