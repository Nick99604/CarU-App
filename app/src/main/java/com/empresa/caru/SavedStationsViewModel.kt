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
import kotlinx.coroutines.launch

data class SavedStationsUiState(
    val savedStations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SavedStationsViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl()
) : ViewModel() {

    companion object {
        private const val TAG = "SavedStationsVM"
    }

    private val _uiState = MutableStateFlow(SavedStationsUiState())
    val uiState: StateFlow<SavedStationsUiState> = _uiState.asStateFlow()

    init {
        observeSavedStations()
    }

    private fun observeSavedStations() {
        viewModelScope.launch {
            stationRepository.getSavedStationIdsFlow().collect { result ->
                when (result) {
                    is com.empresa.caru.domain.repository.Result.Success -> {
                        val savedIds = result.data.toSet()
                        Log.d(TAG, "saved IDs: $savedIds")
                        SharedStationRepository.setSavedStationIds(savedIds)

                        val allStations = SharedStationRepository.stations.value
                        val savedStations = allStations.filter { it.id in savedIds }
                        _uiState.value = _uiState.value.copy(
                            savedStations = savedStations,
                            isLoading = false,
                            error = null
                        )
                    }
                    is com.empresa.caru.domain.repository.Result.Error -> {
                        Log.e(TAG, "observeSavedStations: ERROR - ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun toggleSavedStation(station: FoodStation) {
        viewModelScope.launch {
            val stationId = station.id
            if (SharedStationRepository.isSavedStation(stationId)) {
                stationRepository.unsaveStation(stationId)
            } else {
                stationRepository.saveStation(stationId)
            }
            SharedStationRepository.toggleSavedStation(stationId)
            refresh()
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        observeSavedStations()
    }
}
