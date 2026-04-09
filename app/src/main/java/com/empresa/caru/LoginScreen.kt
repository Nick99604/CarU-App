package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.R
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    authRepository: AuthRepository,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val fieldBackground = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
    val hintColor = if (isDarkTheme) Color(0xFF888888) else Color(0xFF757575)
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Patron de fondo
        Image(
            painter = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = "Patron de fondo",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = if (isDarkTheme) 0.08f else 0.9f
        )

        // ── Overlay de carga ────────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text       = "Accediendo a tu cuenta...\nVerificando tus datos",
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Normal,
                        color      = Color.White,
                        fontSize   = 20.sp,
                        textAlign  = TextAlign.Center
                    )
                }
            }
        }

        // Boton regresar
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = if (isDarkTheme) Color.White else Color(0xFF333333),
                modifier = Modifier.size(22.dp)
            )
        }

        // Boton cambio de tema
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Cambiar tema",
                tint = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333),
                modifier = Modifier.size(22.dp)
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titulo
            Text(
                text = "Accede a tu cuenta para encontrar o registrar puestos de comida",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 26.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )

            // Etiqueta correo
            Text(
                text = "Correo Electronico",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = fieldBackground,
                    focusedContainerColor = fieldBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFE53935),
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !isLoading,
                placeholder = {
                    Text("ejemplo@correo.com", color = hintColor, fontFamily = CaruFontFamily)
                }
            )

            // Etiqueta contrasena
            Text(
                text = "Contrasena",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo contrasena
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = fieldBackground,
                    focusedContainerColor = fieldBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFE53935),
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor
                ),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Mostrar contrasena",
                            tint = hintColor
                        )
                    }
                },
                placeholder = {
                    Text("••••••••", color = hintColor, fontFamily = CaruFontFamily)
                }
            )

            // Boton Iniciar
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) return@Button
                    isLoading = true
                    scope.launch {
                        when (val result = authRepository.login(email, password)) {
                            is Result.Success -> onLoginSuccess()
                            is Result.Error -> { isLoading = false }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Iniciar",
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace olvidaste contrasena
            TextButton(onClick = onForgotPasswordClick, enabled = !isLoading) {
                Text(
                    text = "¿Olvidaste tu contrasena?",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF1A1A1A),
                    fontSize = 16.sp
                )
            }
        }
    }
}