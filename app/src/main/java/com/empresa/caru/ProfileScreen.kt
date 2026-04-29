package com.empresa.caru

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Reload user data whenever the screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.onEvent(ProfileEvent.LoadUserData)
    }

    // Launcher for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.ProfileImageSelected(it.toString()))
        }
    }

    // Launcher for camera with URI output
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                viewModel.onEvent(ProfileEvent.ProfileImageSelected(uri.toString()))
            }
        }
    }

    // Camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Create a temporary file URI for the camera
            val photoFile = java.io.File(context.cacheDir, "temp_profile_photo_${System.currentTimeMillis()}.jpg")
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            tempCameraUri = photoUri
            cameraLauncher.launch(photoUri)
        }
    }

    // Dialogs
    if (uiState.showImagePickerDialog) {
        ImagePickerDialog(
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissImagePickerDialog) },
            onTakePhoto = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onChooseFromGallery = {
                galleryLauncher.launch("image/*")
            }
        )
    }

    if (uiState.showPasswordResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.DismissPasswordResetDialog) },
            title = {
                Text(
                    text = if (uiState.passwordResetSent) "Correo enviado" else "Cambiar contraseña",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (uiState.passwordResetSent) {
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
                        if (!uiState.passwordResetSent) {
                            viewModel.onEvent(ProfileEvent.RequestPasswordReset)
                        } else {
                            viewModel.onEvent(ProfileEvent.DismissPasswordResetDialog)
                        }
                    }
                ) {
                    Text(
                        text = if (uiState.passwordResetSent) "Entendido" else "Enviar",
                        color = RedButtonColor,
                        fontFamily = CaruFontFamily
                    )
                }
            },
            dismissButton = {
                if (!uiState.passwordResetSent) {
                    TextButton(onClick = { viewModel.onEvent(ProfileEvent.DismissPasswordResetDialog) }) {
                        Text("Cancelar", fontFamily = CaruFontFamily)
                    }
                }
            }
        )
    }

    // Error snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Auto-dismiss after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(3.dp, RedButtonColor, CircleShape)
                        .clickable { viewModel.onEvent(ProfileEvent.ShowImagePickerDialog) },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isUploadingImage) {
                        CircularProgressIndicator(
                            color = RedButtonColor,
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (uiState.profileImageUrl != null) {
                        AsyncImage(
                            model = uiState.profileImageUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = textColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = RedButtonColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Toca para cambiar la foto",
                    fontFamily = CaruFontFamily,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // User Name
                Text(
                    text = uiState.userName.ifBlank { "Usuario" },
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // User Email
                Text(
                    text = uiState.userEmail,
                    fontFamily = CaruFontFamily,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    label: String,
    value: String,
    textColor: Color,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = RedButtonColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = CaruFontFamily,
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.7f)
            )
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    fontFamily = CaruFontFamily,
                    fontSize = 16.sp,
                    color = textColor
                )
            }
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cambiar foto de perfil",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Take Photo Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTakePhoto() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Tomar foto",
                        tint = RedButtonColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Tomar foto",
                        fontFamily = CaruFontFamily,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                HorizontalDivider()

                // Choose from Gallery Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChooseFromGallery() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Escoger de galería",
                        tint = RedButtonColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Escoger de galería",
                        fontFamily = CaruFontFamily,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = CaruFontFamily)
            }
        }
    )
}