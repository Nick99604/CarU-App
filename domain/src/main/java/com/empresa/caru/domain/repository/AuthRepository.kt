package com.empresa.caru.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Result wrapper for operations that can succeed or fail.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

/**
 * Simple user profile data used across domain layer.
 */
data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null
)

/**
 * Repository contract for authentication operations.
 * Defined in :domain so :app depends only on the interface, not the implementation.
 */
interface AuthRepository {

    /**
     * Registers a new user with email and password.
     */
    suspend fun register(email: String, password: String, displayName: String): Result<String>

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun login(email: String, password: String): Result<String>

    /**
     * Sends a password reset email to the given address.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Signs out the current user.
     */
    suspend fun logout()

    /**
     * Observes authentication state changes.
     */
    fun observeAuthState(): Flow<UserProfile?>

    /**
     * Gets the user's real name from Firestore users collection.
     */
    suspend fun getUserRealName(userId: String): Result<String>

    /**
     * Gets the station name from Firestore users collection.
     */
    suspend fun getStationName(userId: String): Result<String>

    /**
     * Updates user data in Firestore.
     */
    suspend fun updateUserData(userId: String, updates: Map<String, Any>): Result<Unit>

    /**
     * Uploads a profile image to Storage and returns the URL.
     */
    suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String>
}
