package com.empresa.caru.data.repository

import android.net.Uri
import com.empresa.caru.domain.model.DayScheduleDto
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.model.StationScheduleDto
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.StationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StationRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : StationRepository {

    companion object {
        private const val STATIONS_COLLECTION = "stations"
        private const val IMAGES_PATH = "station_images"
    }

    override suspend fun getStation(stationId: String): Result<FoodStation> {
        return try {
            val doc = firestore.collection(STATIONS_COLLECTION)
                .document(stationId)
                .get()
                .await()
            val station = doc.toObject(FoodStation::class.java)
                ?: return Result.Error("Puesto no encontrado")
            Result.Success(station)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener el puesto")
        }
    }

    override suspend fun getStationsByOwner(ownerId: String): Result<List<FoodStation>> {
        return try {
            val snapshot = firestore.collection(STATIONS_COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()
            val stations = snapshot.documents.mapNotNull { it.toObject(FoodStation::class.java) }
            Result.Success(stations)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener los puestos")
        }
    }

    override suspend fun getAllStations(): Result<List<FoodStation>> {
        return try {
            val snapshot = firestore.collection(STATIONS_COLLECTION)
                .get()
                .await()
            val stations = snapshot.documents.mapNotNull { it.toObject(FoodStation::class.java) }
            Result.Success(stations)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener los puestos")
        }
    }

    override suspend fun createStation(station: FoodStation): Result<String> {
        return try {
            val docRef = firestore.collection(STATIONS_COLLECTION).document()
            val stationWithId = station.copy(id = docRef.id)
            docRef.set(stationWithId).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al crear el puesto")
        }
    }

    override suspend fun updateStation(station: FoodStation): Result<Unit> {
        return try {
            firestore.collection(STATIONS_COLLECTION)
                .document(station.id)
                .set(station)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al actualizar el puesto")
        }
    }

    override suspend fun deleteStation(stationId: String, imageUrl: String?): Result<Unit> {
        return try {
            firestore.collection(STATIONS_COLLECTION)
                .document(stationId)
                .delete()
                .await()

            imageUrl?.let { url ->
                try {
                    val imageRef = storage.getReferenceFromUrl(url)
                    imageRef.delete().await()
                } catch (_: Exception) {
                    // Ignore image deletion errors
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al eliminar el puesto")
        }
    }

    override suspend fun uploadImage(stationId: String, imageUri: String): Result<String> {
        return try {
            val imageRef = storage.reference.child("$IMAGES_PATH/$stationId/${UUID.randomUUID()}")
            val uri = Uri.parse(imageUri)
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al subir la imagen")
        }
    }
}
