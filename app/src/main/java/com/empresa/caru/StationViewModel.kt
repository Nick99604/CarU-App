package com.empresa.caru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.DayScheduleDto
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.model.StationScheduleDto
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.StationRepository
import com.empresa.caru.data.repository.StationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StationUiState {
    data object Idle : StationUiState()
    data object Loading : StationUiState()
    data class Success(val station: FoodStation) : StationUiState()
    data class Error(val message: String) : StationUiState()
    data object Deleted : StationUiState()
    data object Saved : StationUiState()
    data object SaveSuccess : StationUiState()
}

data class StationEditState(
    val name: String = "",
    val vendorName: String = "",
    val address: String = "",
    val phone: String = "",
    val foodTypes: List<String> = emptyList(),
    val schedule: StationScheduleDto = StationScheduleDto(),
    val imageUrl: String? = null,
    val priceMin: String = "",
    val priceMax: String = ""
)

class StationViewModel(
    private val repository: StationRepository = StationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<StationUiState>(StationUiState.Idle)
    val uiState: StateFlow<StationUiState> = _uiState.asStateFlow()

    private val _editState = MutableStateFlow(StationEditState())
    val editState: StateFlow<StationEditState> = _editState.asStateFlow()

    private var currentStationId: String = ""

    fun loadStation(stationId: String) {
        currentStationId = stationId
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading
            when (val result = repository.getStation(stationId)) {
                is Result.Success -> {
                    val station = result.data
                    _editState.value = StationEditState(
                        name = station.name,
                        vendorName = station.vendorName,
                        address = station.address,
                        phone = station.phone,
                        foodTypes = station.foodTypes,
                        schedule = station.schedule,
                        imageUrl = station.imageUrl,
                        priceMin = station.priceMin,
                        priceMax = station.priceMax
                    )
                    _uiState.value = StationUiState.Success(station)
                }
                is Result.Error -> {
                    _uiState.value = StationUiState.Error(result.message)
                }
            }
        }
    }

    fun updateName(name: String) {
        _editState.value = _editState.value.copy(name = name)
    }

    fun updateVendorName(vendorName: String) {
        _editState.value = _editState.value.copy(vendorName = vendorName)
    }

    fun updateAddress(address: String) {
        _editState.value = _editState.value.copy(address = address)
    }

    fun updatePhone(phone: String) {
        _editState.value = _editState.value.copy(phone = phone)
    }

    fun updateFoodTypes(foodTypes: List<String>) {
        _editState.value = _editState.value.copy(foodTypes = foodTypes)
    }

    fun updatePriceMin(priceMin: String) {
        _editState.value = _editState.value.copy(priceMin = priceMin)
    }

    fun updatePriceMax(priceMax: String) {
        _editState.value = _editState.value.copy(priceMax = priceMax)
    }

    fun updateSchedule(schedule: StationScheduleDto) {
        _editState.value = _editState.value.copy(schedule = schedule)
    }

    fun toggleDayOpen(dayKey: String) {
        val currentSchedule = _editState.value.schedule
        val updatedSchedule = when (dayKey) {
            "monday" -> currentSchedule.copy(monday = currentSchedule.monday.copy(isOpen = !currentSchedule.monday.isOpen))
            "tuesday" -> currentSchedule.copy(tuesday = currentSchedule.tuesday.copy(isOpen = !currentSchedule.tuesday.isOpen))
            "wednesday" -> currentSchedule.copy(wednesday = currentSchedule.wednesday.copy(isOpen = !currentSchedule.wednesday.isOpen))
            "thursday" -> currentSchedule.copy(thursday = currentSchedule.thursday.copy(isOpen = !currentSchedule.thursday.isOpen))
            "friday" -> currentSchedule.copy(friday = currentSchedule.friday.copy(isOpen = !currentSchedule.friday.isOpen))
            "saturday" -> currentSchedule.copy(saturday = currentSchedule.saturday.copy(isOpen = !currentSchedule.saturday.isOpen))
            "sunday" -> currentSchedule.copy(sunday = currentSchedule.sunday.copy(isOpen = !currentSchedule.sunday.isOpen))
            else -> currentSchedule
        }
        _editState.value = _editState.value.copy(schedule = updatedSchedule)
    }

    fun updateDayTime(dayKey: String, startTime: String? = null, endTime: String? = null) {
        val currentSchedule = _editState.value.schedule
        val updatedSchedule = when (dayKey) {
            "monday" -> {
                val day = currentSchedule.monday
                currentSchedule.copy(monday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "tuesday" -> {
                val day = currentSchedule.tuesday
                currentSchedule.copy(tuesday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "wednesday" -> {
                val day = currentSchedule.wednesday
                currentSchedule.copy(wednesday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "thursday" -> {
                val day = currentSchedule.thursday
                currentSchedule.copy(thursday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "friday" -> {
                val day = currentSchedule.friday
                currentSchedule.copy(friday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "saturday" -> {
                val day = currentSchedule.saturday
                currentSchedule.copy(saturday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            "sunday" -> {
                val day = currentSchedule.sunday
                currentSchedule.copy(sunday = day.copy(
                    startTime = startTime ?: day.startTime,
                    endTime = endTime ?: day.endTime
                ))
            }
            else -> currentSchedule
        }
        _editState.value = _editState.value.copy(schedule = updatedSchedule)
    }

    fun saveStation() {
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading
            val edit = _editState.value
            val currentState = _uiState.value
            if (currentState is StationUiState.Success) {
                val updatedStation = currentState.station.copy(
                    name = edit.name,
                    vendorName = edit.vendorName,
                    address = edit.address,
                    phone = edit.phone,
                    foodTypes = edit.foodTypes,
                    schedule = edit.schedule,
                    priceMin = edit.priceMin,
                    priceMax = edit.priceMax
                )
                when (repository.updateStation(updatedStation)) {
                    is Result.Success -> _uiState.value = StationUiState.Saved
                    is Result.Error -> _uiState.value = StationUiState.Error("Error al guardar")
                }
            } else {
                _uiState.value = StationUiState.Error("No hay puesto para actualizar")
            }
        }
    }

    fun deleteStation() {
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading
            val currentState = _uiState.value
            if (currentState is StationUiState.Success) {
                when (repository.deleteStation(currentStationId, currentState.station.imageUrl)) {
                    is Result.Success -> _uiState.value = StationUiState.Deleted
                    is Result.Error -> _uiState.value = StationUiState.Error("Error al eliminar")
                }
            } else {
                _uiState.value = StationUiState.Error("No hay puesto para eliminar")
            }
        }
    }

    fun onSaveStation(onNavigateHome: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = StationUiState.Loading
            val edit = _editState.value
            val currentState = _uiState.value

            // Preservar ownerId original si existe
            val originalOwnerId = if (currentState is StationUiState.Success) {
                currentState.station.ownerId
            } else {
                ""
            }

            val station = FoodStation(
                id = currentStationId,
                name = edit.name,
                vendorName = edit.vendorName,
                address = edit.address,
                phone = edit.phone,
                foodTypes = edit.foodTypes,
                schedule = edit.schedule,
                imageUrl = edit.imageUrl,
                priceMin = edit.priceMin,
                priceMax = edit.priceMax,
                latitude = 0.0,
                longitude = 0.0,
                ownerId = originalOwnerId
            )

            Log.d("StationVM", "onSaveStation: guardando station")
            Log.d("StationVM", "  id=[${station.id}], name=[${station.name}]")
            Log.d("StationVM", "  ownerId=[${station.ownerId}]")
            Log.d("StationVM", "  foodTypes=${station.foodTypes}")

            when (val result = repository.saveFoodStation(station)) {
                is Result.Success -> {
                    _uiState.value = StationUiState.SaveSuccess
                    Log.d("StationVM", "onSaveStation: SUCCESS")
                    onNavigateHome?.invoke()
                }
                is Result.Error -> {
                    _uiState.value = StationUiState.Error(result.message)
                    Log.e("StationVM", "onSaveStation: ERROR - ${result.message}")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = StationUiState.Idle
        _editState.value = StationEditState()
        currentStationId = ""
    }
}
