package com.empresa.caru

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.empresa.caru.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class OnboardingState(
    val profileImageUri: String? = null,
    val selectedInterests: List<String> = emptyList(),
    val priceRange: String = "",
    val distance: String = "",
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)

sealed class OnboardingEvent {
    data class ProfileImageSelected(val uri: String) : OnboardingEvent()
    data class InterestToggled(val interest: String) : OnboardingEvent()
    data class PriceRangeSelected(val range: String) : OnboardingEvent()
    data class DistanceSelected(val distance: String) : OnboardingEvent()
    data object Completed : OnboardingEvent()
}

class OnboardingViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.ProfileImageSelected -> {
                uploadProfileImage(event.uri)
            }
            is OnboardingEvent.InterestToggled -> {
                _state.update { currentState ->
                    val currentInterests = currentState.selectedInterests.toMutableList()
                    if (currentInterests.contains(event.interest)) {
                        currentInterests.remove(event.interest)
                    } else {
                        currentInterests.add(event.interest)
                    }
                    currentState.copy(selectedInterests = currentInterests)
                }
            }
            is OnboardingEvent.PriceRangeSelected -> {
                _state.update { it.copy(priceRange = event.range) }
            }
            is OnboardingEvent.DistanceSelected -> {
                _state.update { it.copy(distance = event.distance) }
            }
            is OnboardingEvent.Completed -> {
                // Navegar a home
            }
        }
    }

    private fun uploadProfileImage(uriString: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, errorMessage = null) }
            try {
                val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
                val storageRef = firebaseStorage.reference
                    .child("profile_images")
                    .child("${user.uid}.jpg")

                val uri = Uri.parse(uriString)
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update Firebase Auth profile
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(downloadUrl))
                    .build()
                user.updateProfile(profileUpdate).await()

                // Update Firestore user document
                authRepository.updateUserData(user.uid, mapOf("photoUrl" to downloadUrl))

                _state.update { it.copy(profileImageUri = downloadUrl, isUploading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isUploading = false, errorMessage = e.message) }
            }
        }
    }

    fun reset() {
        _state.value = OnboardingState()
    }
}
