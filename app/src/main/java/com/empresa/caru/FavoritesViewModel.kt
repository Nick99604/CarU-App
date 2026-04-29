package com.empresa.caru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.SharedStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteStations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false
)

class FavoritesViewModel : ViewModel() {

    companion object {
        private const val TAG = "FavoritesVM"
    }

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    // Exponer favoriteIds para otras pantallas
    val favoriteIds: StateFlow<Set<String>> = SharedStationRepository.favoriteIds

    init {
        viewModelScope.launch {
            // Observar cambios en SharedStationRepository
            SharedStationRepository.stations.collect { allStations ->
                val favoriteIds = SharedStationRepository.favoriteIds.value
                val favorites = allStations.filter { it.id in favoriteIds }
                Log.d(TAG, "Favorites updated: ${favorites.size}")
                _uiState.value = FavoritesUiState(favoriteStations = favorites)
            }
        }
    }

    fun isFavorite(stationId: String): Boolean {
        return SharedStationRepository.isFavorite(stationId)
    }

    fun toggleFavorite(station: FoodStation) {
        Log.d(TAG, "toggleFavorite: ${station.id}")
        SharedStationRepository.toggleFavorite(station.id)
    }
}
