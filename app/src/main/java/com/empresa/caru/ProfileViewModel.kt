package com.empresa.caru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import com.empresa.caru.domain.repository.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordResetSent: Boolean = false,
    val showPasswordResetDialog: Boolean = false,
    val showImagePickerDialog: Boolean = false,
    val isUploadingImage: Boolean = false
)

sealed class ProfileEvent {
    data object LoadUserData : ProfileEvent()
    data object ShowPasswordResetDialog : ProfileEvent()
    data object DismissPasswordResetDialog : ProfileEvent()
    data object RequestPasswordReset : ProfileEvent()
    data object SendPasswordReset : ProfileEvent()
    data object ShowImagePickerDialog : ProfileEvent()
    data object DismissImagePickerDialog : ProfileEvent()
    data object TakePhoto : ProfileEvent()
    data object ChooseFromGallery : ProfileEvent()
    data class ProfileImageSelected(val uri: String) : ProfileEvent()
    data object DismissError : ProfileEvent()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    init {
        loadUserData()
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { userProfile ->
                if (userProfile == null) {
                    resetToDefaults()
                } else {
                    _uiState.update {
                        it.copy(
                            userName = userProfile.displayName,
                            userEmail = userProfile.email,
                            profileImageUrl = userProfile.photoUrl
                        )
                    }
                }
            }
        }
    }

    fun resetToDefaults() {
        _uiState.value = ProfileUiState()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadUserData -> loadUserData()
            is ProfileEvent.ShowPasswordResetDialog -> {
                _uiState.update { it.copy(showPasswordResetDialog = true) }
            }
            is ProfileEvent.RequestPasswordReset -> sendPasswordReset()
            is ProfileEvent.SendPasswordReset -> sendPasswordReset()
            is ProfileEvent.DismissPasswordResetDialog -> {
                _uiState.update { it.copy(showPasswordResetDialog = false, passwordResetSent = false) }
            }
            is ProfileEvent.ShowImagePickerDialog -> {
                _uiState.update { it.copy(showImagePickerDialog = true) }
            }
            is ProfileEvent.DismissImagePickerDialog -> {
                _uiState.update { it.copy(showImagePickerDialog = false) }
            }
            is ProfileEvent.TakePhoto -> {
                _uiState.update { it.copy(showImagePickerDialog = false) }
            }
            is ProfileEvent.ChooseFromGallery -> {
                _uiState.update { it.copy(showImagePickerDialog = false) }
            }
            is ProfileEvent.ProfileImageSelected -> {
                uploadProfileImage(event.uri)
            }
            is ProfileEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadUserData() {
        val user = firebaseAuth.currentUser
        _uiState.update {
            it.copy(
                userName = user?.displayName ?: "",
                userEmail = user?.email ?: "",
                profileImageUrl = user?.photoUrl?.toString()
            )
        }
    }

    private fun sendPasswordReset() {
        val email = _uiState.value.userEmail
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "No hay correo electrónico associado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = authRepository.sendPasswordReset(email)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            passwordResetSent = true,
                            showPasswordResetDialog = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun uploadProfileImage(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true) }
            try {
                val user = firebaseAuth.currentUser
                    ?: throw Exception("Usuario no autenticado")

                val storageRef = firebaseStorage.reference
                    .child("profile_images")
                    .child("${user.uid}.jpg")

                // Upload image to Firebase Storage
                val uploadTask = storageRef.putFile(android.net.Uri.parse(uri))
                uploadTask.await()

                // Get download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update user profile with new photo URL
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setPhotoUri(android.net.Uri.parse(downloadUrl))
                    .build()
                user.updateProfile(profileUpdate).await()

                // Sync with Firestore
                authRepository.updateUserData(user.uid, mapOf("photoUrl" to downloadUrl))

                _uiState.update {
                    it.copy(
                        isUploadingImage = false,
                        profileImageUrl = downloadUrl,
                        showImagePickerDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingImage = false,
                        errorMessage = "Error al subir la imagen: ${e.message}"
                    )
                }
            }
        }
    }
}
