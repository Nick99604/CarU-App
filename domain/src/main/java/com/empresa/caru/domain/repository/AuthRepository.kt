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
    val email: String = ""
)

/**
 * Repository contract for authentication operations.
 * Defined in :domain so :app depends only on the interface, not the implementation.
 */
interface AuthRepository {

    /**
     * Registers a new user with email and password.
     * @param email User's email address.
     * @param password User's password.
     * @param displayName User's display name.
     * @return Result with the user's unique ID on success, or an error message on failure.
     */
    suspend fun register(email: String, password: String, displayName: String): Result<String>

    /**
     * Signs in an existing user with email and password.
     * @param email User's email address.
     * @param password User's password.
     * @return Result with the user's unique ID on success, or an error message on failure.
     */
    suspend fun login(email: String, password: String): Result<String>

    /**
     * Sends a password reset email to the given address.
     * @param email Target email address.
     * @return Result with success or an error message.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Observes authentication state changes.
     * @return Flow that emits the current user profile on subscription and on every auth state change.
     */
    fun observeAuthState(): Flow<UserProfile?>

    /**
     * Gets the user's real name from Firestore users collection.
     * @param userId The user's unique ID.
     * @return Result with the user's real name on success, or an error message on failure.
     */
    suspend fun getUserRealName(userId: String): Result<String>

    /**
     * Gets the station name from Firestore users collection.
     * @param userId The user's unique ID.
     * @return Result with the station name on success, or an error message on failure.
     */
    suspend fun getStationName(userId: String): Result<String>
}
