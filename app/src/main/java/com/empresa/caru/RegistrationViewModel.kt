package com.empresa.caru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.domain.model.DayScheduleDto
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.model.StationScheduleDto
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.StationRepository
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.SharedStationRepository
import com.empresa.caru.data.repository.StationRepositoryImpl
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val stationRepository: StationRepository = StationRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

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

    private var _currentStationId = MutableStateFlow<String?>(null)

    val isAllCompleted: Boolean
        get() = _foodTypeCompleted.value && _infoCompleted.value && _locationCompleted.value && _scheduleCompleted.value

    val completedCount: Int
        get() = listOf(_foodTypeCompleted.value, _infoCompleted.value, _locationCompleted.value, _scheduleCompleted.value, _imageCompleted.value).count { it }

    val totalSections = 5

    fun updateFoodTypes(foodTypes: List<FoodType>, otherType: String) {
        _registration.value = _registration.value.copy(foodTypes = foodTypes, otherFoodType = otherType)
        _foodTypeCompleted.value = foodTypes.isNotEmpty() || otherType.isNotBlank()
    }

    fun updateInfo(description: String, phone: String, priceMin: String, priceMax: String) {
        _registration.value = _registration.value.copy(description = description, contactPhone = phone, averagePriceMin = priceMin, averagePriceMax = priceMax)
        _infoCompleted.value = description.isNotBlank() && phone.isNotBlank()
    }

    fun updateAddress(address: String, latitude: Double? = null, longitude: Double? = null) {
        _registration.value = _registration.value.copy(address = address, latitude = latitude, longitude = longitude)
        _locationCompleted.value = address.isNotBlank()
    }

    fun loadStationToRegistration(stationId: String?) {
        if (stationId == null) {
            reset()
            return
        }
        val station = SharedStationRepository.getStationById(stationId)
        station?.let { s ->
            _currentStationId.value = stationId
            val mappedTypes = FoodType.entries.filter { it.label in s.foodTypes }
            val otherTypesList = s.foodTypes.filter { type -> FoodType.entries.none { it.label == type } }
            
            _registration.value = StationRegistration(
                foodTypes = mappedTypes,
                otherFoodType = otherTypesList.joinToString(", "),
                description = s.description,
                contactPhone = s.phone,
                stationName = s.name,
                vendorName = s.vendorName,
                address = s.address,
                latitude = s.latitude,
                longitude = s.longitude,
                averagePriceMin = s.priceMin,
                averagePriceMax = s.priceMax,
                stationImageUri = s.imageUrl
            )
            _foodTypeCompleted.value = true
            _infoCompleted.value = true
            _locationCompleted.value = true
            _scheduleCompleted.value = true
            _imageCompleted.value = s.imageUrl != null
        }
    }

    fun saveStation(onComplete: (Boolean) -> Unit = {}) {
        if (_isSaving.value) return
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val reg = _registration.value
                val userId = firebaseAuth.currentUser?.uid ?: return@launch
                val foodTypesList = reg.foodTypes.map { it.label }.toMutableList()
                if (reg.otherFoodType.isNotBlank()) foodTypesList.add(reg.otherFoodType)

                val station = FoodStation(
                    id = _currentStationId.value ?: "",
                    name = reg.stationName.ifBlank { reg.address.ifBlank { "Mi Puesto" } },
                    vendorName = reg.vendorName.ifBlank { "Vendedor" },
                    address = reg.address,
                    phone = reg.contactPhone,
                    description = reg.description,
                    foodTypes = foodTypesList,
                    imageUrl = reg.stationImageUri,
                    priceMin = reg.averagePriceMin,
                    priceMax = reg.averagePriceMax,
                    latitude = reg.latitude ?: 0.0,
                    longitude = reg.longitude ?: 0.0,
                    ownerId = userId
                )

                val result = if (station.id.isBlank()) stationRepository.createStation(station) else stationRepository.updateStation(station).let { Result.Success(station.id) }
                
                if (result is Result.Success) {
                    reset()
                    onComplete(true)
                } else onComplete(false)
            } catch (e: Exception) {
                onComplete(false)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateSchedule(schedule: StationSchedule) {
        _registration.value = _registration.value.copy(schedule = schedule)
        _scheduleCompleted.value = true
    }

    fun updateImage(uri: String?) {
        _registration.value = _registration.value.copy(stationImageUri = uri)
        _imageCompleted.value = true
    }

    fun reset() {
        _registration.value = StationRegistration()
        _foodTypeCompleted.value = false
        _infoCompleted.value = false
        _locationCompleted.value = false
        _scheduleCompleted.value = false
        _imageCompleted.value = false
        _currentStationId.value = null
    }

    fun loadVendorName() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = authRepository.getUserRealName(userId)
            if (result is Result.Success) _registration.value = _registration.value.copy(vendorName = result.data)
        }
    }

    fun loadStationName() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = authRepository.getStationName(userId)
            if (result is Result.Success && result.data.isNotBlank()) _registration.value = _registration.value.copy(stationName = result.data)
        }
    }

    fun deleteStation(onComplete: (Boolean) -> Unit = {}) {
        val id = _currentStationId.value ?: return
        viewModelScope.launch {
            if (stationRepository.deleteStation(id, null) is Result.Success) {
                reset()
                onComplete(true)
            } else onComplete(false)
        }
    }
}
