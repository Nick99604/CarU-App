package com.empresa.caru.domain.repository

import com.empresa.caru.domain.model.FoodStation

interface StationRepository {
    suspend fun getStation(stationId: String): Result<FoodStation>
    suspend fun getStationsByOwner(ownerId: String): Result<List<FoodStation>>
    suspend fun getAllStations(): Result<List<FoodStation>>
    suspend fun createStation(station: FoodStation): Result<String>
    suspend fun updateStation(station: FoodStation): Result<Unit>
    suspend fun deleteStation(stationId: String, imageUrl: String?): Result<Unit>
    suspend fun uploadImage(stationId: String, imageUri: String): Result<String>
}
