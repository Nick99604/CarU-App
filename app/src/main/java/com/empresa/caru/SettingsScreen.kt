package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteStationClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)

    var selectedDistanceUnit by remember { mutableStateOf("Kilómetros") }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var passwordResetSent by remember { mutableStateOf(false) }

    // Password Reset Dialog
    if (showPasswordResetDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordResetDialog = false
                passwordResetSent = false
            },
            title = {
                Text(
                    text = if (passwordResetSent) "Correo enviado" else "Cambiar contraseña",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (passwordResetSent) {
                        "Se ha enviado un enlace de recuperación a tu correo electrónico."
                    } else {
                        "¿Deseas recibir un enlace para cambiar tu contraseña?"
                    },
                    fontFamily = CaruFontFamily
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!passwordResetSent) {
                            // Call password reset via ProfileViewModel logic
                            passwordResetSent = true
                        } else {
                            showPasswordResetDialog = false
                            passwordResetSent = false
                        }
                    }
                ) {
                    Text(
                        text = if (passwordResetSent) "Entendido" else "Enviar",
                        color = RedButtonColor,
                        fontFamily = CaruFontFamily
                    )
                }
            },
            dismissButton = {
                if (!passwordResetSent) {
                    TextButton(onClick = {
                        showPasswordResetDialog = false
                        passwordResetSent = false
                    }) {
                        Text("Cancelar", fontFamily = CaruFontFamily)
                    }
                }
            }
        )
    }

    // Delete Station Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Eliminar puesto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar tu puesto? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteStationClick()
                    }
                ) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
                        fontFamily = CaruTitleFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = RedButtonColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconBg)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconBg)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Cambiar tema",
                            tint = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor
                )
            )
        },
        containerColor = backgroundColor
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Image(
                painter = painterResource(R.drawable.background_food_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isDarkTheme) 0.05f else 0.1f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Sección: Seguridad
                SettingsSection(title = "Seguridad") {
                    SettingsOptionRow(
                        icon = Icons.Default.Lock,
                        label = "Cambiar contraseña",
                        textColor = textColor,
                        cardBg = cardBg,
                        showArrow = true,
                        onClick = {
                            showPasswordResetDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sección: Preferencias
                SettingsSection(title = "Preferencias") {
                    SettingsOptionRow(
                        icon = Icons.Default.Straighten,
                        label = "Unidades de distancia",
                        textColor = textColor,
                        cardBg = cardBg,
                        showArrow = false,
                        onClick = { }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DistanceUnitChip(
                                text = "Km",
                                isSelected = selectedDistanceUnit == "Kilómetros",
                                onClick = { selectedDistanceUnit = "Kilómetros" }
                            )
                            DistanceUnitChip(
                                text = "M",
                                isSelected = selectedDistanceUnit == "Metros",
                                onClick = { selectedDistanceUnit = "Metros" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sección: Gestión de Puesto
                SettingsSection(title = "Gestión de Puesto") {
                    SettingsOptionRow(
                        icon = Icons.Default.Delete,
                        label = "Eliminar puesto",
                        textColor = textColor,
                        cardBg = cardBg,
                        isDestructive = true,
                        onClick = {
                            showDeleteConfirmDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = RedButtonColor,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        content()
    }
}

@Composable
private fun SettingsOptionRow(
    icon: ImageVector,
    label: String,
    textColor: Color,
    cardBg: Color,
    showArrow: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isDestructive) Color(0xFFFF5252) else RedButtonColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontFamily = CaruFontFamily,
            fontSize = 16.sp,
            color = if (isDestructive) Color(0xFFFF5252) else textColor,
            modifier = Modifier.weight(1f)
        )
        if (trailingContent != null) {
            trailingContent()
        } else if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun DistanceUnitChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) RedButtonColor else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) RedButtonColor else Color.Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = CaruFontFamily,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
