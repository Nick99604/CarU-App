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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.domain.repository.Result
import kotlinx.coroutines.launch

@Composable
fun RegisterStationScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    authRepository: AuthRepository,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    var nombre       by remember { mutableStateOf("") }
    var nombrePuesto by remember { mutableStateOf("") }
    var correo       by remember { mutableStateOf("") }
    var contrasena   by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading    by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val fieldBg         = if (isDarkTheme) Color(0xFF2C2C2C)  else Color(0xFFDEDEDE)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Fondo decorativo
        Image(
            painter            = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = null,
            modifier           = Modifier.matchParentSize(),
            contentScale       = ContentScale.Crop,
            alpha              = if (isDarkTheme) 0.06f else 0.06f
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
                        text       = "Estamos preparando\ntodo para ti",
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Normal,
                        color      = Color.White,
                        fontSize   = 20.sp,
                        textAlign  = TextAlign.Center
                    )
                }
            }
        }

        // Botón retroceder
        IconButton(
            onClick  = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 52.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector        = Icons.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint               = iconTint,
                modifier           = Modifier.size(22.dp)
            )
        }

        // Botón tema
        IconButton(
            onClick  = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector        = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Cambiar tema",
                tint               = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333),
                modifier           = Modifier.size(22.dp)
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text       = "Registra tu puesto",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color      = textColor,
                fontSize   = 30.sp,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            )

            // Campo Nombre
            StationFieldLabel(text = "Nombre", color = textColor)
            StationTextField(
                value         = nombre,
                onValueChange = { nombre = it },
                placeholder   = "",
                fieldBg       = fieldBg,
                labelColor    = labelColor,
                keyboardType  = KeyboardType.Text,
                enabled       = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Nombre del puesto
            StationFieldLabel(text = "Nombre del puesto", color = textColor)
            StationTextField(
                value         = nombrePuesto,
                onValueChange = { nombrePuesto = it },
                placeholder   = "",
                fieldBg       = fieldBg,
                labelColor    = labelColor,
                keyboardType  = KeyboardType.Text,
                enabled       = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Correo Electrónico
            StationFieldLabel(text = "Correo Electrónico", color = textColor)
            StationTextField(
                value         = correo,
                onValueChange = { correo = it },
                placeholder   = "",
                fieldBg       = fieldBg,
                labelColor    = labelColor,
                keyboardType  = KeyboardType.Email,
                enabled       = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Contraseña
            StationFieldLabel(text = "Contraseña", color = textColor)
            StationPasswordField(
                value           = contrasena,
                onValueChange   = { contrasena = it },
                showPassword    = showPassword,
                onToggleVisible = { showPassword = !showPassword },
                fieldBg         = fieldBg,
                iconTint        = labelColor,
                enabled         = !isLoading
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón Crear
            Button(
                onClick  = {
                    if (nombre.isBlank() || nombrePuesto.isBlank() || correo.isBlank() || contrasena.isBlank()) return@Button
                    isLoading = true
                    scope.launch {
                        when (val result = authRepository.register(correo, contrasena, nombre)) {
                            is Result.Success -> onSuccess()
                            is Result.Error -> { isLoading = false }
                        }
                    }
                },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(50.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = RedButtonColor,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled   = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Crear",
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                }
            }
        }
    }
}

// ── Componentes reutilizables ────────────────────────────────────────────────

@Composable
private fun StationFieldLabel(text: String, color: Color) {
    Text(
        text       = text,
        fontFamily = CaruFontFamily,
        fontWeight = FontWeight.Bold,
        color      = color,
        fontSize   = 17.sp,
        modifier   = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun StationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    fieldBg: Color,
    labelColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = {
            Text(
                text      = placeholder,
                color     = labelColor,
                fontSize  = 15.sp
            )
        },
        modifier      = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = fieldBg,
            unfocusedContainerColor = fieldBg,
            disabledContainerColor = fieldBg,
            focusedBorderColor    = RedButtonColor,
            unfocusedBorderColor  = Color.Transparent,
            focusedTextColor      = Color(0xFF1A1A1A),
            unfocusedTextColor    = Color(0xFF1A1A1A),
            cursorColor           = RedButtonColor
        ),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        enabled       = enabled
    )
}

@Composable
private fun StationPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleVisible: () -> Unit,
    fieldBg: Color,
    iconTint: Color,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value               = value,
        onValueChange       = onValueChange,
        modifier            = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        visualTransformation = if (showPassword)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon        = {
            IconButton(onClick = onToggleVisible, enabled = enabled) {
                Icon(
                    imageVector        = if (showPassword)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff,
                    contentDescription = if (showPassword) "Ocultar" else "Mostrar",
                    tint               = iconTint
                )
            }
        },
        colors              = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = fieldBg,
            unfocusedContainerColor = fieldBg,
            disabledContainerColor  = fieldBg,
            focusedBorderColor      = RedButtonColor,
            unfocusedBorderColor    = Color.Transparent,
            focusedTextColor        = Color(0xFF1A1A1A),
            unfocusedTextColor      = Color(0xFF1A1A1A),
            cursorColor             = RedButtonColor
        ),
        singleLine          = true,
        keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled             = enabled
    )
}
