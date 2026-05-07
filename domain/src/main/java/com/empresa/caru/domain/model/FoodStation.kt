package com.empresa.caru.domain.model

import com.google.firebase.firestore.PropertyName

/**
 * Modelo de puesto de comida para Firestore.
 */
data class FoodStation(
    val id: String = "",
    val name: String = "",
    val vendorName: String = "",
    val address: String = "",
    val phone: String = "",
    val description: String = "",
    val foodTypes: List<String> = emptyList(),
    val schedule: StationScheduleDto = StationScheduleDto(),
    val imageUrl: String? = null,
    val ownerId: String = "",
    val priceMin: String = "",
    val priceMax: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
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
    @get:PropertyName("isOpen") @set:PropertyName("isOpen")
    var isOpen: Boolean = false,
    @get:PropertyName("startTime") @set:PropertyName("startTime")
    var startTime: String = "",
    @get:PropertyName("endTime") @set:PropertyName("endTime")
    var endTime: String = ""
)
