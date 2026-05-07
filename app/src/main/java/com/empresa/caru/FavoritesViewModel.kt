package com.empresa.caru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.SharedStationRepository
import com.empresa.caru.domain.repository.StationRepository
import com.empresa.caru.data.repository.StationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteStations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false
)

class FavoritesViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = SharedStationRepository.favoriteIds

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            combine(
                SharedStationRepository.stations,
                stationRepository.getFavoriteStationIdsFlow()
            ) { stations, favResult ->
                if (favResult is com.empresa.caru.domain.repository.Result.Success) {
                    val favIds = favResult.data.toSet()
                    SharedStationRepository.setFavoriteIds(favIds)
                    stations.filter { it.id in favIds }
                } else {
                    emptyList()
                }
            }.collect { filteredFavorites ->
                _uiState.value = FavoritesUiState(favoriteStations = filteredFavorites)
            }
        }
    }

    fun toggleFavorite(station: FoodStation) {
        viewModelScope.launch {
            val isFavorite = SharedStationRepository.isFavorite(station.id)
            if (isFavorite) {
                stationRepository.unfavoriteStation(station.id)
            } else {
                stationRepository.favoriteStation(station.id)
            }
        }
    }
}
