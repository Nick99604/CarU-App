package com.empresa.caru.data.repository

import android.net.Uri
import android.util.Log
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.StationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StationRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : StationRepository {

    companion object {
        private const val TAG = "StationRepository"
        private const val STATIONS_COLLECTION = "stations"
        private const val SAVED_STATIONS_COLLECTION = "saved_stations"
        private const val FAVORITES_COLLECTION = "favorites"
        private const val IMAGES_PATH = "station_images"
    }

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    override suspend fun getStation(stationId: String): Result<FoodStation> {
        return try {
            val doc = firestore.collection(STATIONS_COLLECTION)
                .document(stationId)
                .get()
                .await()

            if (doc.exists()) {
                val station = doc.toObject(FoodStation::class.java)
                if (station != null) {
                    Result.Success(station)
                } else {
                    Result.Error("No se pudo convertir el documento")
                }
            } else {
                Result.Error("Puesto no encontrado")
            }
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

            val stations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodStation::class.java)
            }
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

            val stations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodStation::class.java)
            }
            Result.Success(stations)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener los puestos")
        }
    }

    override fun getAllStationsFlow(): Flow<Result<List<FoodStation>>> = callbackFlow {
        val listener = firestore.collection(STATIONS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val stations = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FoodStation::class.java)
                    }
                    trySend(Result.Success(stations))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createStation(station: FoodStation): Result<String> {
        return try {
            Log.d(TAG, "createStation: Iniciando guardado de ${station.name}")
            val docRef = firestore.collection(STATIONS_COLLECTION).add(station).await()
            val newId = docRef.id
            docRef.update("id", newId).await()
            Log.d(TAG, "createStation SUCCESS: ID=$newId")
            Result.Success(newId)
        } catch (e: Exception) {
            Log.e(TAG, "createStation ERROR: ${e.message}", e)
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
                } catch (_: Exception) {}
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

    override suspend fun saveStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            val savedRef = firestore.collection(SAVED_STATIONS_COLLECTION)
                .document("${userId}_$stationId")

            savedRef.set(mapOf(
                "userId" to userId,
                "stationId" to stationId,
                "savedAt" to System.currentTimeMillis()
            )).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al guardar el puesto")
        }
    }

    override suspend fun unsaveStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            firestore.collection(SAVED_STATIONS_COLLECTION)
                .document("${userId}_$stationId")
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al quitar el puesto guardado")
        }
    }

    override fun getSavedStationIdsFlow(): Flow<Result<List<String>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Result.Success(emptyList()))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(SAVED_STATIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val stationIds = snapshot.documents.mapNotNull { it.getString("stationId") }
                    trySend(Result.Success(stationIds))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun favoriteStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            firestore.collection(FAVORITES_COLLECTION)
                .document("${userId}_$stationId")
                .set(mapOf(
                    "userId" to userId,
                    "stationId" to stationId,
                    "addedAt" to System.currentTimeMillis()
                )).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al marcar como favorito")
        }
    }

    override suspend fun unfavoriteStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            firestore.collection(FAVORITES_COLLECTION)
                .document("${userId}_$stationId")
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al quitar de favoritos")
        }
    }

    override fun getFavoriteStationIdsFlow(): Flow<Result<List<String>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Result.Success(emptyList()))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(FAVORITES_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val ids = snapshot.documents.mapNotNull { it.getString("stationId") }
                    trySend(Result.Success(ids))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveFoodStation(station: FoodStation): Result<Unit> {
        return if (station.id.isBlank()) {
            when (val res = createStation(station)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(res.message)
            }
        } else {
            updateStation(station)
        }
    }
}
