package com.empresa.caru.domain.model

/**
 * Modelo de favorito (puesto marcado como favorito por un usuario).
 */
data class FavoriteDto(
    val id: String = "",
    val userId: String = "",
    val stationId: String = "",
    val stationName: String = "",
    val stationAddress: String = "",
    val stationImageUrl: String? = null,
    val foodTypes: List<String> = emptyList(),
    val addedAt: Long = System.currentTimeMillis()
)