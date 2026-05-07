package com.empresa.caru.data.repository

import android.net.Uri
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Firebase implementation of [AuthRepository].
 * Uses Firebase Auth for user registration, login, and password reset.
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : AuthRepository {

    companion object {
        const val ERROR_EMAIL_NOT_FOUND = "EMAIL_NOT_FOUND"
        const val ERROR_WRONG_PASSWORD = "WRONG_PASSWORD"
        private const val USERS_COLLECTION = "users"
        private const val PROFILE_IMAGES_PATH = "profile_images"
    }

    /**
     * Observes authentication state changes.
     * Emits the current user immediately on subscription, then on every auth state change.
     */
    override fun observeAuthState(): Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val profile = auth.currentUser?.let { user ->
                UserProfile(
                    userId = user.uid,
                    displayName = user.displayName ?: "",
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString()
                )
            }
            trySend(profile)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.Error("No se pudo crear el usuario")

            val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdate).await()

            // Initialize user document in Firestore
            updateUserData(user.uid, mapOf(
                "nombre" to displayName,
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            ))

            Result.Success(user.uid)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido al registrar")
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.Error("No se pudo iniciar sesión")

            Result.Success(user.uid)
        } catch (e: FirebaseAuthInvalidUserException) {
            // User not found or disabled
            Result.Error(ERROR_EMAIL_NOT_FOUND)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Wrong password or invalid email format
            when {
                e.message?.contains("wrong password", ignoreCase = true) == true ||
                e.message?.contains("password is invalid", ignoreCase = true) == true -> {
                    Result.Error(ERROR_WRONG_PASSWORD)
                }
                else -> {
                    // Could be invalid email format
                    Result.Error(ERROR_EMAIL_NOT_FOUND)
                }
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido al iniciar sesión")
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido al enviar el correo de recuperación")
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getUserRealName(userId: String): Result<String> {
        return try {
            val docSnapshot = FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val realName = docSnapshot.getString("nombre")
                ?: FirebaseAuth.getInstance().currentUser?.displayName
                ?: FirebaseAuth.getInstance().currentUser?.email?.substringBefore('@')
                ?: "Usuario"

            Result.Success(realName)
        } catch (e: Exception) {
            val fallback = FirebaseAuth.getInstance().currentUser?.displayName
                ?: FirebaseAuth.getInstance().currentUser?.email?.substringBefore('@')
                ?: "Usuario"
            Result.Success(fallback)
        }
    }

    override suspend fun getStationName(userId: String): Result<String> {
        return try {
            val docSnapshot = FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val stationName = docSnapshot.getString("nombrePuesto") ?: ""

            Result.Success(stationName)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener el nombre del puesto")
        }
    }

    override suspend fun updateUserData(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(userId)
                .set(updates, com.google.firebase.firestore.SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al actualizar datos de usuario en Firestore")
        }
    }

    override suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String> {
        return try {
            val imageRef = storage.reference.child("$PROFILE_IMAGES_PATH/$userId/${UUID.randomUUID()}")
            val uri = Uri.parse(imageUri)
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            
            // También actualizamos el perfil de Firebase Auth
            val user = firebaseAuth.currentUser
            val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(downloadUrl))
                .build()
            user?.updateProfile(profileUpdate)?.await()
            
            // Y el documento en Firestore
            updateUserData(userId, mapOf("fotoPerfilUrl" to downloadUrl))
            
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al subir la imagen de perfil")
        }
    }
}
