package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageUploadScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration = viewModel.registration
    var selectedImageUri by remember(registration.value) { mutableStateOf(registration.value.stationImageUri) }
    var showOptions by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val cardBorder      = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.image_upload_screen_title),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
                            contentDescription = stringResource(R.string.back_button_description),
                            tint = iconTint
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
                            contentDescription = stringResource(R.string.change_theme_description),
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
            // Fondo decorativo
            Image(
                painter = painterResource(id = R.drawable.background_food_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isDarkTheme) 0.06f else 0.06f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Subtítulo
                Text(
                    text = stringResource(R.string.image_upload_subtitle),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Área de preview de imagen
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(cardBg)
                        .border(
                            width = 2.dp,
                            color = if (selectedImageUri != null) RedButtonColor else cardBorder,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        // Aquí se mostraría la imagen seleccionada
                        // Por ahora mostramos un placeholder con icono
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = null,
                                tint = RedButtonColor,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.image_selected_text),
                                fontFamily = CaruFontFamily,
                                fontWeight = FontWeight.Normal,
                                color = labelColor,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                tint = labelColor,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.no_image_selected_text),
                                fontFamily = CaruFontFamily,
                                fontWeight = FontWeight.Normal,
                                color = labelColor,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Opciones de subida
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tomar foto
                    ImageUploadOption(
                        icon = Icons.Filled.CameraAlt,
                        label = stringResource(R.string.take_photo_option),
                        description = stringResource(R.string.take_photo_description),
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textColor = textColor,
                        labelColor = labelColor,
                        iconBg = iconBg,
                        iconTint = iconTint,
                        onClick = {
                            // En una implementación real, esto lanzaría la cámara
                            // selectedImageUri = "camera_captured"
                            showOptions = true
                        }
                    )

                    // Subir imagen
                    ImageUploadOption(
                        icon = Icons.Filled.Upload,
                        label = stringResource(R.string.upload_image_option),
                        description = stringResource(R.string.upload_image_description),
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textColor = textColor,
                        labelColor = labelColor,
                        iconBg = iconBg,
                        iconTint = iconTint,
                        onClick = {
                            // En una implementación real, esto abriría un selector de archivos
                            // selectedImageUri = "uploaded"
                            showOptions = true
                        }
                    )

                    // Subir desde galería
                    ImageUploadOption(
                        icon = Icons.Filled.PhotoLibrary,
                        label = stringResource(R.string.gallery_option),
                        description = stringResource(R.string.gallery_description),
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textColor = textColor,
                        labelColor = labelColor,
                        iconBg = iconBg,
                        iconTint = iconTint,
                        onClick = {
                            // En una implementación real, esto abriría la galería
                            // selectedImageUri = "gallery_selected"
                            showOptions = true
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Botón Guardar
                Button(
                    onClick = {
                        viewModel.updateImage(selectedImageUri)
                        onSaveClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedButtonColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save_button),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogo de confirmación de opción (placeholder para implementación real)
    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            containerColor = cardBg,
            title = {
                Text(
                    text = stringResource(R.string.image_options_title),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.image_options_message),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showOptions = false }) {
                    Text(
                        text = "OK",
                        color = RedButtonColor
                    )
                }
            }
        )
    }
}

@Composable
private fun ImageUploadOption(
    icon: ImageVector,
    label: String,
    description: String,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    labelColor: Color,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                width = 1.dp,
                color = cardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp
            )
            Text(
                text = description,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Normal,
                color = labelColor,
                fontSize = 12.sp
            )
        }
    }
}
