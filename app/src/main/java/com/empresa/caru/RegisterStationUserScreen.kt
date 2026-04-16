package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStationUserScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    var nombre by remember { mutableStateOf("") }
    var nombrePuesto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF555555)
    val fieldBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFDEDEDE)
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)
    val iconTint = if (isDarkTheme) Color.White else Color(0xFF333333)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Fondo decorativo
        Image(
            painter = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = if (isDarkTheme) 0.06f else 0.06f
        )

        // Botón retroceder
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 52.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_description),
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        // Botón tema
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = stringResource(R.string.change_theme_description),
                tint = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333),
                modifier = Modifier.size(22.dp)
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Título
            Text(
                text = stringResource(R.string.register_station_title),
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo Nombre
            RegisterStationField(
                value = nombre,
                onValueChange = { nombre = it },
                label = stringResource(R.string.name_label),
                placeholder = stringResource(R.string.name_placeholder),
                leadingIcon = Icons.Filled.Person,
                fieldBg = fieldBg,
                labelColor = labelColor,
                iconTint = iconTint,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Nombre del puesto
            RegisterStationField(
                value = nombrePuesto,
                onValueChange = { nombrePuesto = it },
                label = stringResource(R.string.station_name_label),
                placeholder = stringResource(R.string.station_name_placeholder),
                leadingIcon = Icons.Filled.Store,
                fieldBg = fieldBg,
                labelColor = labelColor,
                iconTint = iconTint,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Correo Electrónico
            RegisterStationField(
                value = correo,
                onValueChange = { correo = it },
                label = stringResource(R.string.email_label),
                placeholder = stringResource(R.string.email_placeholder),
                leadingIcon = Icons.Filled.Email,
                fieldBg = fieldBg,
                labelColor = labelColor,
                iconTint = iconTint,
                keyboardType = KeyboardType.Email,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Contraseña
            RegisterStationPasswordField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = stringResource(R.string.password_label),
                placeholder = stringResource(R.string.password_placeholder),
                showPassword = showPassword,
                onToggleVisible = { showPassword = !showPassword },
                fieldBg = fieldBg,
                labelColor = labelColor,
                iconTint = iconTint,
                enabled = !isLoading
            )

            // Mensaje de error
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Botón Crear
            Button(
                onClick = {
                    // Validaciones
                    if (nombre.isBlank() || nombrePuesto.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                        errorMessage = "Por favor completa todos los campos"
                        return@Button
                    }
                    if (contrasena.length < 6) {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        return@Button
                    }

                    errorMessage = null
                    isLoading = true

                    scope.launch {
                        try {
                            val auth = FirebaseAuth.getInstance()
                            Log.d("RegisterStation", "Iniciando registro con correo: $correo")

                            // Registro con Firebase Auth
                            val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
                            Log.d("RegisterStation", "Resultado Firebase Auth: ${result.user?.uid}")

                            val user = result.user
                            if (user != null) {
                                Log.d("RegisterStation", "Usuario creado con UID: ${user.uid}")

                                // Guardar datos en Firestore en segundo plano (no bloquea navegación)
                                val db = Firebase.firestore
                                val userData = hashMapOf(
                                    "nombre" to nombre,
                                    "nombrePuesto" to nombrePuesto,
                                    "correo" to correo
                                )

                                // Lanzar guardado en Firestore sin esperar - la navegación es prioritaria
                                launch {
                                    try {
                                        db.collection("users")
                                            .document(user.uid)
                                            .set(userData)
                                            .await()
                                        Log.d("RegisterStation", "Datos guardados en Firestore exitosamente")
                                    } catch (e: Exception) {
                                        Log.e("RegisterStation", "Error al guardar en Firestore (no crítico)", e)
                                    }
                                }

                                Log.d("RegisterStation", "Navegando al siguiente módulo...")
                                // Éxito - navegar inmediatamente al siguiente módulo
                                onSuccess()
                            } else {
                                Log.e("RegisterStation", "result.user es null")
                                errorMessage = "Error al crear el usuario. Intenta de nuevo."
                            }
                        } catch (e: CancellationException) {
                            Log.e("RegisterStation", "Coroutine cancelada", e)
                            errorMessage = "Operación cancelada. Intenta de nuevo."
                        } catch (e: Exception) {
                            Log.e("RegisterStation", "Error en registro", e)
                            errorMessage = when {
                                e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                                    "Este correo ya está registrado"
                                e.message?.contains("weak password", ignoreCase = true) == true ->
                                    "La contraseña es muy corta"
                                e.message?.contains("invalid email", ignoreCase = true) == true ->
                                    "Correo electrónico inválido"
                                else -> e.message ?: "Error al registrar"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedButtonColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.register_station_button),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RegisterStationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    fieldBg: Color,
    labelColor: Color,
    iconTint: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = labelColor.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = fieldBg,
                unfocusedContainerColor = fieldBg,
                disabledContainerColor = fieldBg,
                focusedBorderColor = RedButtonColor,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = labelColor,
                unfocusedTextColor = labelColor,
                cursorColor = RedButtonColor
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            enabled = enabled
        )
    }
}

@Composable
private fun RegisterStationPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    showPassword: Boolean,
    onToggleVisible: () -> Unit,
    fieldBg: Color,
    labelColor: Color,
    iconTint: Color,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = labelColor.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisible, enabled = enabled) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword)
                            stringResource(R.string.hide_password_description)
                        else
                            stringResource(R.string.show_password_description),
                        tint = iconTint
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = fieldBg,
                unfocusedContainerColor = fieldBg,
                disabledContainerColor = fieldBg,
                focusedBorderColor = RedButtonColor,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = labelColor,
                unfocusedTextColor = labelColor,
                cursorColor = RedButtonColor
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = enabled
        )
    }
}
