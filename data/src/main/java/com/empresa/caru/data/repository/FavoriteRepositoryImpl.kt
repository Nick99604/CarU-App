package com.empresa.caru.data.repository

import com.empresa.caru.domain.model.FavoriteDto
import com.empresa.caru.domain.repository.FavoriteRepository
import com.empresa.caru.domain.repository.Result
import androidx.compose.runtime.mutableStateListOf

class FavoriteRepositoryImpl : FavoriteRepository {

    private val favorites = mutableListOf<FavoriteDto>()

    override fun getFavorites(): List<FavoriteDto> = favorites.toList()

    override fun addFavorite(favorite: FavoriteDto): Result<Unit> {
        return try {
            val existingFavorite = favorites.find { it.stationId == favorite.stationId }
            if (existingFavorite != null) {
                return Result.Success(Unit)
            }
            val newFavorite = favorite.copy(id = java.util.UUID.randomUUID().toString())
            favorites.add(newFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al agregar favorito")
        }
    }

    override fun removeFavorite(favoriteId: String): Result<Unit> {
        return try {
            favorites.removeIf { it.id == favoriteId }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al eliminar favorito")
        }
    }

    override fun isFavorite(stationId: String): Boolean {
        return favorites.any { it.stationId == stationId }
    }

    override fun getFavoriteByStationId(stationId: String): FavoriteDto? {
        return favorites.find { it.stationId == stationId }
    }
}