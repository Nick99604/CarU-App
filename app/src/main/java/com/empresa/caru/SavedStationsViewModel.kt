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

data class SavedStationsUiState(
    val savedStations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SavedStationsViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedStationsUiState())
    val uiState: StateFlow<SavedStationsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            // Combinamos los puestos globales con los IDs guardados de Firebase
            combine(
                SharedStationRepository.stations,
                stationRepository.getSavedStationIdsFlow()
            ) { allStations, savedResult ->
                if (savedResult is com.empresa.caru.domain.repository.Result.Success) {
                    val savedIds = savedResult.data.toSet()
                    SharedStationRepository.setSavedStationIds(savedIds)
                    allStations.filter { it.id in savedIds }
                } else {
                    emptyList()
                }
            }.collect { filteredStations ->
                _uiState.value = SavedStationsUiState(savedStations = filteredStations, isLoading = false)
            }
        }
    }

    fun toggleSavedStation(station: FoodStation) {
        viewModelScope.launch {
            val isSaved = SharedStationRepository.isSavedStation(station.id)
            if (isSaved) {
                stationRepository.unsaveStation(station.id)
            } else {
                stationRepository.saveStation(station.id)
            }
        }
    }
}
