package com.empresa.caru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.SharedStationRepository
import com.empresa.caru.domain.repository.StationRepository
import com.empresa.caru.data.repository.StationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val stations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Observa estaciones del repositorio compartido (incluye las nuevas agregadas)
    val stations: StateFlow<List<FoodStation>> = SharedStationRepository.stations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadStationsFromFirestore()
    }

    private fun loadStationsFromFirestore() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = stationRepository.getAllStations()) {
                is Result.Success -> {
                    // Actualizar el repositorio compartido con los datos de Firestore
                    SharedStationRepository.setStations(result.data)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun refresh() {
        loadStationsFromFirestore()
    }
}
