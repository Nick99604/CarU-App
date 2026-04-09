package com.empresa.caru.data.repository

import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of [AuthRepository].
 * Uses Firebase Auth for user registration, login, and password reset.
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.Error("No se pudo crear el usuario")

            user.displayName?.let {
                val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdate).await()
            }

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
}
