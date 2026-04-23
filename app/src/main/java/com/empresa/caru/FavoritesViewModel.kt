package com.empresa.caru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.FavoriteDto
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.SharedStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FavoritesUiState(
    val favorites: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false
)

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    // Estado reactivo de IDs de favoritos desde el repositorio compartido
    val favoriteIds: StateFlow<Set<String>> = SharedStationRepository.favoriteIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Estaciones favoritas calculadas reactivamente
    val favoriteStations: StateFlow<List<FoodStation>> = SharedStationRepository.stations
        .map { stations ->
            val ids = favoriteIds.value
            stations.filter { it.id in ids }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isFavorite(stationId: String): Boolean {
        return SharedStationRepository.isFavorite(stationId)
    }

    fun toggleFavorite(station: FoodStation) {
        SharedStationRepository.toggleFavorite(station.id)
    }
}
