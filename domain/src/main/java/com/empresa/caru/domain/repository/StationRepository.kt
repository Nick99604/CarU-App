package com.empresa.caru.domain.repository

import com.empresa.caru.domain.model.FoodStation
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    suspend fun getStation(stationId: String): Result<FoodStation>
    suspend fun getStationsByOwner(ownerId: String): Result<List<FoodStation>>
    suspend fun getAllStations(): Result<List<FoodStation>>
    fun getAllStationsFlow(): Flow<Result<List<FoodStation>>>
    suspend fun createStation(station: FoodStation): Result<String>
    suspend fun updateStation(station: FoodStation): Result<Unit>
    suspend fun deleteStation(stationId: String, imageUrl: String?): Result<Unit>
    suspend fun uploadImage(stationId: String, imageUri: String): Result<String>
    suspend fun saveStation(stationId: String): Result<Unit>
    suspend fun unsaveStation(stationId: String): Result<Unit>
    suspend fun saveFoodStation(station: FoodStation): Result<Unit>
    fun getSavedStationIdsFlow(): Flow<Result<List<String>>>
}
