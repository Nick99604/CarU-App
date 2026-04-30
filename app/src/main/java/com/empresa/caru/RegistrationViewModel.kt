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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    companion object {
        private const val TAG = "RegistrationVM"
    }

    private val _registration = MutableStateFlow(StationRegistration())
    val registration: StateFlow<StationRegistration> = _registration.asStateFlow()

    private val _foodTypeCompleted = MutableStateFlow(false)
    val foodTypeCompleted: StateFlow<Boolean> = _foodTypeCompleted.asStateFlow()

    private val _infoCompleted = MutableStateFlow(false)
    val infoCompleted: StateFlow<Boolean> = _infoCompleted.asStateFlow()

    private val _locationCompleted = MutableStateFlow(false)
    val locationCompleted: StateFlow<Boolean> = _locationCompleted.asStateFlow()

    private val _scheduleCompleted = MutableStateFlow(false)
    val scheduleCompleted: StateFlow<Boolean> = _scheduleCompleted.asStateFlow()

    private val _imageCompleted = MutableStateFlow(false)
    val imageCompleted: StateFlow<Boolean> = _imageCompleted.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var savedStationId: String? = null

    val isAllCompleted: Boolean
        get() = _foodTypeCompleted.value && _infoCompleted.value && _locationCompleted.value &&
                _scheduleCompleted.value && _imageCompleted.value

    val completedCount: Int
        get() = listOf(
            _foodTypeCompleted.value,
            _infoCompleted.value,
            _locationCompleted.value,
            _scheduleCompleted.value,
            _imageCompleted.value
        ).count { it }

    val totalSections = 5

    fun updateFoodTypes(foodTypes: List<FoodType>, otherType: String) {
        _registration.value = _registration.value.copy(
            foodTypes = foodTypes,
            otherFoodType = otherType
        )
        _foodTypeCompleted.value = foodTypes.isNotEmpty() || otherType.isNotBlank()
    }

    fun updateInfo(description: String, phone: String, priceMin: String, priceMax: String) {
        _registration.value = _registration.value.copy(
            description = description,
            contactPhone = phone,
            averagePriceMin = priceMin,
            averagePriceMax = priceMax
        )
        _infoCompleted.value = description.isNotBlank() && phone.isNotBlank()
    }

    fun updateAddress(address: String, latitude: Double? = null, longitude: Double? = null) {
        _registration.value = _registration.value.copy(
            address = address,
            latitude = latitude,
            longitude = longitude
        )
        _locationCompleted.value = address.isNotBlank()
    }

    fun updateSchedule(schedule: StationSchedule) {
        _registration.value = _registration.value.copy(schedule = schedule)
        _scheduleCompleted.value = _registration.value.schedule.let { sched ->
            sched.monday.isOpen || sched.tuesday.isOpen || sched.wednesday.isOpen ||
            sched.thursday.isOpen || sched.friday.isOpen || sched.saturday.isOpen ||
            sched.sunday.isOpen
        }
    }

    fun updateImage(uri: String?) {
        _registration.value = _registration.value.copy(stationImageUri = uri)
        // Imagen es opcional - solo marca completado si hay una imagen
        // _imageCompleted.value = uri != null
        _imageCompleted.value = true // Siempre completado (imagen opcional)
    }

    fun reset() {
        _registration.value = StationRegistration()
        _foodTypeCompleted.value = false
        _infoCompleted.value = false
        _locationCompleted.value = false
        _scheduleCompleted.value = false
        _imageCompleted.value = false
        savedStationId = null
    }

    /**
     * Limpia solo el estado de completado para comenzar fresh
     * pero mantiene los datos (para edición)
     */
    fun resetCompletionFlags() {
        _foodTypeCompleted.value = false
        _infoCompleted.value = false
        _locationCompleted.value = false
        _scheduleCompleted.value = false
        _imageCompleted.value = false
    }

    /**
     * Guarda el puesto en Firestore
     */
    fun saveStation(onComplete: (Boolean) -> Unit = {}) {
        if (_isSaving.value) {
            Log.w(TAG, "saveStation: ya se está guardando, ignorando...")
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            Log.d(TAG, "saveStation: iniciando...")
            Log.d(TAG, "  registration=${_registration.value}")

            try {
                val reg = _registration.value
                val currentUser = firebaseAuth.currentUser
                val userId = currentUser?.uid ?: run {
                    Log.e(TAG, "saveStation: No hay usuario autenticado")
                    _isSaving.value = false
                    onComplete(false)
                    return@launch
                }
                val vendorName = currentUser.displayName ?: "Usuario"

                Log.d(TAG, "saveStation: userId=$userId, vendorName=$vendorName")
                Log.d(TAG, "  foodTypes=${reg.foodTypes}, count=${reg.foodTypes.size}")
                Log.d(TAG, "  address=${reg.address}")
                Log.d(TAG, "  phone=${reg.contactPhone}")
                Log.d(TAG, "  schedule es default: ${reg.schedule == StationSchedule()}")

                // Convertir StationSchedule (UI) a StationScheduleDto (domain)
                val scheduleDto = StationScheduleDto(
                    monday = convertDaySchedule(reg.schedule.monday),
                    tuesday = convertDaySchedule(reg.schedule.tuesday),
                    wednesday = convertDaySchedule(reg.schedule.wednesday),
                    thursday = convertDaySchedule(reg.schedule.thursday),
                    friday = convertDaySchedule(reg.schedule.friday),
                    saturday = convertDaySchedule(reg.schedule.saturday),
                    sunday = convertDaySchedule(reg.schedule.sunday)
                )

                val station = FoodStation(
                    id = "",
                    name = reg.address.ifBlank { "Mi Puesto" },
                    vendorName = vendorName,
                    address = reg.address,
                    phone = reg.contactPhone,
                    foodTypes = reg.foodTypes.map { it.label },
                    schedule = scheduleDto,
                    imageUrl = reg.stationImageUri,
                    priceMin = reg.averagePriceMin,
                    priceMax = reg.averagePriceMax,
                    latitude = reg.latitude ?: 0.0,
                    longitude = reg.longitude ?: 0.0,
                    ownerId = userId
                )

                Log.d(TAG, "saveStation: station=$station")
                Log.d(TAG, "  name=${station.name}, address=${station.address}")
                Log.d(TAG, "  phone=${station.phone}, foodTypes=${station.foodTypes}")
                Log.d(TAG, "  ownerId=${station.ownerId}")
                Log.d(TAG, "  schedule=${station.schedule}")
                Log.d(TAG, "  latitude=${station.latitude}, longitude=${station.longitude}")

                when (val result = stationRepository.createStation(station)) {
                    is Result.Success -> {
                        Log.d(TAG, "saveStation: ÉXITO, ID=${result.data}")
                        savedStationId = result.data
                        _isSaving.value = false
                        reset()
                        onComplete(true)
                    }
                    is Result.Error -> {
                        Log.e(TAG, "saveStation: ERROR - ${result.message}")
                        _isSaving.value = false
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveStation: EXCEPTION - ${e.message}", e)
                _isSaving.value = false
                onComplete(false)
            }
        }
    }

    private fun convertDaySchedule(day: DaySchedule): DayScheduleDto {
        return DayScheduleDto(
            isOpen = day.isOpen,
            startTime = day.startTime,
            endTime = day.endTime
        )
    }

    fun deleteStation(onComplete: (Boolean) -> Unit = {}) {
        val stationId = savedStationId
        if (stationId == null) {
            Log.w(TAG, "deleteStation: No hay stationId guardado")
            onComplete(false)
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "deleteStation: eliminando $stationId")
            when (val result = stationRepository.deleteStation(stationId, null)) {
                is Result.Success -> {
                    Log.d(TAG, "deleteStation: ÉXITO")
                    savedStationId = null
                    reset()
                    onComplete(true)
                }
                is Result.Error -> {
                    Log.e(TAG, "deleteStation: ERROR - ${result.message}")
                    onComplete(false)
                }
            }
        }
    }
}
