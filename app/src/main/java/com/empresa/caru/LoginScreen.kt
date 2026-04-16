package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.data.repository.AuthRepositoryImpl
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
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val fieldBackground = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
    val hintColor = if (isDarkTheme) Color(0xFF888888) else Color(0xFF757575)
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)

    // Mensajes de error capturados en contexto composable
    val emailErrorMsg = stringResource(R.string.login_error_email)
    val passwordErrorMsg = stringResource(R.string.login_error_password)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Patron de fondo
        Image(
            painter = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = stringResource(R.string.background_pattern_description),
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
                        text = stringResource(R.string.login_loading),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_description),
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
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titulo
            Text(
                text = stringResource(R.string.login_title),
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
                text = stringResource(R.string.email_label),
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo correo
            val emailBackgroundColor = if (emailError != null) {
                Color(0x33FF0000) // Rojo suave con baja opacidad
            } else {
                fieldBackground
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(emailBackgroundColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    enabled = !isLoading,
                    textStyle = TextStyle(
                        color = textColor,
                        fontFamily = CaruFontFamily,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    cursorBrush = SolidColor(RedButtonColor),
                    decorationBox = { innerTextField ->
                        if (email.isEmpty()) {
                            Text(
                                text = stringResource(R.string.email_placeholder),
                                color = hintColor,
                                fontFamily = CaruFontFamily,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Error de correo
            if (emailError != null) {
                Text(
                    text = emailError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Etiqueta contrasena
            Text(
                text = stringResource(R.string.password_label),
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo contrasena
            val passwordBackgroundColor = if (passwordError != null) {
                Color(0x33FF0000) // Rojo suave con baja opacidad
            } else {
                fieldBackground
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(passwordBackgroundColor)
                    .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    BasicTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        enabled = !isLoading,
                        textStyle = TextStyle(
                            color = textColor,
                            fontFamily = CaruFontFamily,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        cursorBrush = SolidColor(RedButtonColor),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (password.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.password_placeholder),
                                        color = hintColor,
                                        fontFamily = CaruFontFamily,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(R.string.show_password_description),
                        tint = hintColor
                    )
                }
            }

            // Error de contrasena
            if (passwordError != null) {
                Text(
                    text = passwordError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Boton Iniciar
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) return@Button
                    emailError = null
                    passwordError = null
                    isLoading = true
                    scope.launch {
                        when (val result = authRepository.login(email, password)) {
                            is Result.Success -> onLoginSuccess()
                            is Result.Error -> {
                                isLoading = false
                                when (result.message) {
                                    AuthRepositoryImpl.ERROR_EMAIL_NOT_FOUND -> {
                                        emailError = emailErrorMsg
                                    }
                                    AuthRepositoryImpl.ERROR_WRONG_PASSWORD -> {
                                        passwordError = passwordErrorMsg
                                    }
                                    else -> {
                                        emailError = result.message
                                    }
                                }
                            }
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
                        text = stringResource(R.string.login_button_text),
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
                    text = stringResource(R.string.forgot_password_link),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF1A1A1A),
                    fontSize = 16.sp
                )
            }
        }
    }
}
