package com.empresa.caru.domain.model

/**
 * Modelo de puesto de comida para Firestore.
 */
data class FoodStation(
    val id: String = "",
    val name: String = "",
    val vendorName: String = "",
    val address: String = "",
    val phone: String = "",
    val foodTypes: List<String> = emptyList(),
    val schedule: StationScheduleDto = StationScheduleDto(),
    val imageUrl: String? = null,
    val ownerId: String = "",
    val priceMin: String = "",
    val priceMax: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class StationScheduleDto(
    val monday: DayScheduleDto = DayScheduleDto(),
    val tuesday: DayScheduleDto = DayScheduleDto(),
    val wednesday: DayScheduleDto = DayScheduleDto(),
    val thursday: DayScheduleDto = DayScheduleDto(),
    val friday: DayScheduleDto = DayScheduleDto(),
    val saturday: DayScheduleDto = DayScheduleDto(),
    val sunday: DayScheduleDto = DayScheduleDto()
)

data class DayScheduleDto(
    val isOpen: Boolean = false,
    val startTime: String = "",
    val endTime: String = ""
)
