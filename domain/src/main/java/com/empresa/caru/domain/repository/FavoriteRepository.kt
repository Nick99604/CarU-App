package com.empresa.caru.domain.repository

import com.empresa.caru.domain.model.FavoriteDto

interface FavoriteRepository {
    fun getFavorites(): List<FavoriteDto>
    fun addFavorite(favorite: FavoriteDto): Result<Unit>
    fun removeFavorite(favoriteId: String): Result<Unit>
    fun isFavorite(stationId: String): Boolean
    fun getFavoriteByStationId(stationId: String): FavoriteDto?
}