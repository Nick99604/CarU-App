package com.empresa.caru.data.repository

import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of [AuthRepository].
 * Uses Firebase Auth for user registration, login, and password reset.
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    companion object {
        const val ERROR_EMAIL_NOT_FOUND = "EMAIL_NOT_FOUND"
        const val ERROR_WRONG_PASSWORD = "WRONG_PASSWORD"
    }

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
}
