package com.empresa.caru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.SharedStationRepository
import com.empresa.caru.domain.repository.StationRepository
import com.empresa.caru.data.repository.StationRepositoryImpl
import com.empresa.caru.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val stations: List<FoodStation> = emptyList(),
    val userStations: List<FoodStation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val userId: String? = null
)

class HomeViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "HomeViewModel creado - iniciando listener de Firestore")
        observeAuthState()
        observeStations()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { userProfile ->
                _uiState.value = _uiState.value.copy(userId = userProfile?.userId)
                if (userProfile != null) {
                    fetchUserStations(userProfile.userId)
                } else {
                    _uiState.value = _uiState.value.copy(userStations = emptyList())
                }
            }
        }
    }

    private fun fetchUserStations(userId: String) {
        viewModelScope.launch {
            when (val result = stationRepository.getStationsByOwner(userId)) {
                is com.empresa.caru.domain.repository.Result.Success -> {
                    _uiState.value = _uiState.value.copy(userStations = result.data)
                }
                is com.empresa.caru.domain.repository.Result.Error -> {
                    Log.e(TAG, "Error fetching user stations: ${result.message}")
                }
            }
        }
    }

    private fun observeStations() {
        viewModelScope.launch {
            Log.d(TAG, "observeStations: registrando addSnapshotListener")

            stationRepository.getAllStationsFlow().collect { result ->
                Log.d(TAG, "observeStations: Flow recibió datos")
                when (result) {
                    is com.empresa.caru.domain.repository.Result.Success -> {
                        val stations = result.data
                        Log.d(TAG, "observeStations: ÉXITO - ${stations.size} estaciones")
                        SharedStationRepository.setStations(stations)
                        _uiState.value = _uiState.value.copy(
                            stations = stations,
                            isLoading = false,
                            error = null
                        )
                        // Refresh user stations if we are logged in
                        _uiState.value.userId?.let { fetchUserStations(it) }
                    }
                    is com.empresa.caru.domain.repository.Result.Error -> {
                        Log.e(TAG, "observeStations: ERROR - ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            stations = emptyList(),
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun loadStations() {
        Log.d(TAG, "loadStations: llamada manual - forzando recarga...")
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        observeStations()
    }

    fun refresh() {
        Log.d(TAG, "refresh: recargando stations")
        loadStations()
        _uiState.value.userId?.let { fetchUserStations(it) }
    }
}
