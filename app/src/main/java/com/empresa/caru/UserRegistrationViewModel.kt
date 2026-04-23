package com.empresa.caru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UserRegistrationState {
    data object Idle : UserRegistrationState()
    data object Loading : UserRegistrationState()
    data object Success : UserRegistrationState()
    data class Error(val message: String) : UserRegistrationState()
}

class UserRegistrationViewModel : ViewModel() {

    private val _registrationState = MutableStateFlow<UserRegistrationState>(UserRegistrationState.Idle)
    val registrationState: StateFlow<UserRegistrationState> = _registrationState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    fun registerUser(nombre: String, nombrePuesto: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || nombrePuesto.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _registrationState.value = UserRegistrationState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _registrationState.value = UserRegistrationState.Loading
            try {
                val auth = FirebaseAuth.getInstance()
                val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
                val user = result.user

                if (user != null) {
                    _userId.value = user.uid

                    val db = FirebaseFirestore.getInstance()
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "nombrePuesto" to nombrePuesto,
                        "correo" to correo
                    )

                    db.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                    _registrationState.value = UserRegistrationState.Success
                } else {
                    _registrationState.value = UserRegistrationState.Error("Error al crear el usuario. Intenta de nuevo.")
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                        "Este correo ya está registrado"
                    e.message?.contains("weak password", ignoreCase = true) == true ->
                        "La contraseña es muy corta"
                    e.message?.contains("invalid email", ignoreCase = true) == true ->
                        "Correo electrónico inválido"
                    else -> e.message ?: "Error al registrar"
                }
                _registrationState.value = UserRegistrationState.Error(errorMessage)
            }
        }
    }

    fun resetState() {
        _registrationState.value = UserRegistrationState.Idle
        _userId.value = null
    }
}