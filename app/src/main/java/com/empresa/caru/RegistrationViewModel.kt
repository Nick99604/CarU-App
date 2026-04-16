package com.empresa.caru

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel compartido para el flujo de registro de puestos.
 * Mantiene el estado persistente entre pantallas.
 */
class RegistrationViewModel : ViewModel() {

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

    fun updateAddress(address: String) {
        _registration.value = _registration.value.copy(address = address)
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
        _imageCompleted.value = uri != null
    }

    fun reset() {
        _registration.value = StationRegistration()
        _foodTypeCompleted.value = false
        _infoCompleted.value = false
        _locationCompleted.value = false
        _scheduleCompleted.value = false
        _imageCompleted.value = false
    }
}
