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
        private const val IMAGES_PATH = "station_images"
    }

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    override suspend fun getStation(stationId: String): Result<FoodStation> {
        return try {
            Log.d(TAG, "getStation: consultando documento $stationId")
            val doc = firestore.collection(STATIONS_COLLECTION)
                .document(stationId)
                .get()
                .await()

            Log.d(TAG, "getStation: documento existe=${doc.exists()}, data=${doc.data}")
            if (doc.exists()) {
                val station = doc.toObject(FoodStation::class.java)
                if (station != null) {
                    Log.d(TAG, "getStation: SUCCESS, station=$station")
                    Result.Success(station)
                } else {
                    Log.e(TAG, "getStation: toObject devolvio null")
                    Result.Error("No se pudo convertir el documento")
                }
            } else {
                Log.w(TAG, "getStation: documento no existe")
                Result.Error("Puesto no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getStation error: ${e.message}", e)
            Result.Error(e.message ?: "Error al obtener el puesto")
        }
    }

    override suspend fun getStationsByOwner(ownerId: String): Result<List<FoodStation>> {
        return try {
            Log.d(TAG, "getStationsByOwner: ownerId=$ownerId")
            val snapshot = firestore.collection(STATIONS_COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()

            Log.d(TAG, "getStationsByOwner: ${snapshot.size()} documentos")
            val stations = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "  Doc ${doc.id}: ${doc.data}")
                doc.toObject(FoodStation::class.java)
            }
            Log.d(TAG, "getStationsByOwner: ${stations.size} estaciones convertidas")
            Result.Success(stations)
        } catch (e: Exception) {
            Log.e(TAG, "getStationsByOwner error: ${e.message}", e)
            Result.Error(e.message ?: "Error al obtener los puestos")
        }
    }

    override suspend fun getAllStations(): Result<List<FoodStation>> {
        return try {
            Log.d(TAG, "========== getAllStations INICIO ==========")
            Log.d(TAG, "Firestore instance: $firestore")
            Log.d(TAG, "Collection path: $STATIONS_COLLECTION")

            val collectionRef = firestore.collection(STATIONS_COLLECTION)
            Log.d(TAG, "Collection reference: $collectionRef")

            val snapshot = collectionRef
                .get()
                .await()

            Log.d(TAG, "Snapshot received: size=${snapshot.size()}, empty=${snapshot.isEmpty}")
            Log.d(TAG, "Documents in snapshot:")
            snapshot.documents.forEach { doc ->
                Log.d(TAG, "  DOC id=${doc.id}")
                Log.d(TAG, "  DOC data=${doc.data}")
                Log.d(TAG, "  DOC exists=${doc.exists()}")
            }

            val stations = snapshot.documents.mapNotNull { doc ->
                val station = doc.toObject(FoodStation::class.java)
                Log.d(TAG, "  Mapeado: $station")
                station
            }

            Log.d(TAG, "getAllStations: $stations")
            Log.d(TAG, "========== getAllStations FIN: ${stations.size} estaciones ==========")
            Result.Success(stations)
        } catch (e: Exception) {
            Log.e(TAG, "getAllStations ERROR: ${e.message}", e)
            e.printStackTrace()
            Result.Error(e.message ?: "Error al obtener los puestos")
        }
    }

    override fun getAllStationsFlow(): Flow<Result<List<FoodStation>>> = callbackFlow {
        Log.d(TAG, "getAllStationsFlow: listener registrado")
        val listener = firestore.collection(STATIONS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "getAllStationsFlow listener error: ${error.message}", error)
                    Log.e(TAG, "getAllStationsFlow: POSIBLE CAUSA - ${getPossiblErrorReason(error)}")
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "getAllStationsFlow: ${snapshot.size()} docs recibido")
                    if (snapshot.isEmpty) {
                        Log.w(TAG, "getAllStationsFlow: colección vacía - verifica que existan documentos en Firestore")
                        Log.w(TAG, "getAllStationsFlow: collection='$STATIONS_COLLECTION'")
                    }
                    snapshot.documents.forEach { doc ->
                        Log.d(TAG, "  Listener Doc ${doc.id}: ${doc.data}")
                        Log.d(TAG, "  Listener Doc campos: ${doc.data?.keys}")
                    }
                    val stations = snapshot.documents.mapNotNull { doc ->
                        val station = doc.toObject(FoodStation::class.java)
                        Log.d(TAG, "  Mapeado: $station")
                        station
                    }
                    Log.d(TAG, "getAllStationsFlow: emitiendo ${stations.size} estaciones")
                    trySend(Result.Success(stations))
                } else {
                    Log.w(TAG, "getAllStationsFlow: snapshot null")
                    trySend(Result.Success(emptyList()))
                }
            }

        awaitClose {
            Log.d(TAG, "getAllStationsFlow: listener removido")
            listener.remove()
        }
    }

    private fun getPossiblErrorReason(error: Exception): String {
        val msg = error.message ?: ""
        return when {
            msg.contains("PERMISSION_DENIED") -> "PERMISOS: El usuario no tiene permiso para leer la colección 'stations'. Revisa las reglas de Firestore."
            msg.contains("Index") || msg.contains("index") -> "ÍNDICE: No existe un índice compuesto para la consulta. Crea el índice en Firebase Console."
            msg.contains("Unavailable") -> "RED: Firestore no está disponible. Verifica la conexión a internet."
            else -> "Desconocido: $msg"
        }
    }

    override suspend fun createStation(station: FoodStation): Result<String> {
        return try {
            Log.d(TAG, "========== createStation INICIO ==========")
            Log.d(TAG, "Station a guardar: $station")

            val docRef = firestore.collection(STATIONS_COLLECTION).document()
            Log.d(TAG, "DocRef generado: ${docRef.id}")

            val stationWithId = station.copy(id = docRef.id)
            Log.d(TAG, "Station con ID asignado: $stationWithId")

            docRef.set(stationWithId).await()
            Log.d(TAG, "Documento guardado exitosamente en: ${docRef.path}")

            // Verificar que se guardó
            val verifyDoc = docRef.get().await()
            Log.d(TAG, "Verificacion - existe=${verifyDoc.exists()}, data=${verifyDoc.data}")
            Log.d(TAG, "========== createStation FIN: ID=${docRef.id} ==========")

            Result.Success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "createStation ERROR: ${e.message}", e)
            e.printStackTrace()
            Result.Error(e.message ?: "Error al crear el puesto")
        }
    }

    override suspend fun updateStation(station: FoodStation): Result<Unit> {
        return try {
            Log.d(TAG, "updateStation: ${station.id}")
            firestore.collection(STATIONS_COLLECTION)
                .document(station.id)
                .set(station)
                .await()
            Log.d(TAG, "updateStation: SUCCESS")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateStation error: ${e.message}", e)
            Result.Error(e.message ?: "Error al actualizar el puesto")
        }
    }

    override suspend fun deleteStation(stationId: String, imageUrl: String?): Result<Unit> {
        return try {
            Log.d(TAG, "deleteStation: $stationId")
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
            Log.e(TAG, "deleteStation error: ${e.message}", e)
            Result.Error(e.message ?: "Error al eliminar el puesto")
        }
    }

    override suspend fun uploadImage(stationId: String, imageUri: String): Result<String> {
        return try {
            Log.d(TAG, "uploadImage: stationId=$stationId, uri=$imageUri")
            val imageRef = storage.reference.child("$IMAGES_PATH/$stationId/${UUID.randomUUID()}")
            val uri = Uri.parse(imageUri)
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d(TAG, "uploadImage: SUCCESS, url=$downloadUrl")
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "uploadImage error: ${e.message}", e)
            Result.Error(e.message ?: "Error al subir la imagen")
        }
    }

    override suspend fun saveStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            Log.d(TAG, "saveStation: userId=$userId, stationId=$stationId")

            val savedRef = firestore.collection(SAVED_STATIONS_COLLECTION)
                .document("${userId}_$stationId")

            savedRef.set(mapOf(
                "userId" to userId,
                "stationId" to stationId,
                "savedAt" to System.currentTimeMillis()
            )).await()

            Log.d(TAG, "saveStation: SUCCESS")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveStation error: ${e.message}", e)
            Result.Error(e.message ?: "Error al guardar el puesto")
        }
    }

    override suspend fun unsaveStation(stationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            Log.d(TAG, "unsaveStation: userId=$userId, stationId=$stationId")

            val savedRef = firestore.collection(SAVED_STATIONS_COLLECTION)
                .document("${userId}_$stationId")

            savedRef.delete().await()

            Log.d(TAG, "unsaveStation: SUCCESS")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "unsaveStation error: ${e.message}", e)
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

        Log.d(TAG, "getSavedStationIdsFlow: userId=$userId")
        val listener = firestore.collection(SAVED_STATIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "getSavedStationIdsFlow listener error: ${error.message}", error)
                    trySend(Result.Error(error.message ?: "Error desconocido"))
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val stationIds = snapshot.documents.mapNotNull { it.getString("stationId") }
                    Log.d(TAG, "getSavedStationIdsFlow: ${stationIds.size} guardados")
                    trySend(Result.Success(stationIds))
                } else {
                    trySend(Result.Success(emptyList()))
                }
            }

        awaitClose {
            Log.d(TAG, "getSavedStationIdsFlow: listener removido")
            listener.remove()
        }
    }
}
