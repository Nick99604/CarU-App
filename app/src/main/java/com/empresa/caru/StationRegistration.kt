package com.empresa.caru

/**
 * Modelo de datos que representa el estado completo del registro de un puesto.
 * Se pasa entre las pantallas del flujo de registro.
 */
data class StationRegistration(
    // FoodTypeScreen
    val foodTypes: List<FoodType> = emptyList(),
    val otherFoodType: String = "",

    // StationInfoScreen
    val description: String = "",
    val contactPhone: String = "",
    val averagePriceMin: String = "",
    val averagePriceMax: String = "",

    // StationName
    val stationName: String = "",

    // Vendor name from Firestore
    val vendorName: String = "",

    // LocationSelectionScreen
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    // ScheduleScreen
    val schedule: StationSchedule = StationSchedule(),

    // ImageUploadScreen
    val stationImageUri: String? = null,
    
    // Ownership
    val ownerId: String = ""
)

/**
 * Tipos de comida disponibles.
 */
enum class FoodType(val label: String) {
    DESAYUNO("Desayuno"),
    ALMUERZO("Almuerzo"),
    CENA("Cena"),
    SNACKS("Snacks"),
    BEBIDAS("Bebidas"),
    POSTRES("Postres"),
    COMIDA_RAPIDA("Comida rápida"),
    COMIDA_MEXICANA("Comida mexicana"),
    COMIDA_ASIATICA("Comida asiática"),
    COMIDA_ITALIANA("Comida italiana"),
    VEGETARIANA("Vegetariana"),
    VEGANA("Vegana")
}

/**
 * Horario de operación del puesto.
 */
data class StationSchedule(
    val monday: DaySchedule = DaySchedule(),
    val tuesday: DaySchedule = DaySchedule(),
    val wednesday: DaySchedule = DaySchedule(),
    val thursday: DaySchedule = DaySchedule(),
    val friday: DaySchedule = DaySchedule(),
    val saturday: DaySchedule = DaySchedule(),
    val sunday: DaySchedule = DaySchedule()
)

/**
 * Horario de un día específico.
 */
data class DaySchedule(
    val isOpen: Boolean = false,
    val startTime: String = "",
    val endTime: String = ""
)

/**
 * Días de la semana para el selector rápido.
 */
enum class Weekday {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
}
