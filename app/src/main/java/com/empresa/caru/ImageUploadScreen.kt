package com.empresa.caru

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val registration by viewModel.registration.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val cardBorder      = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    LaunchedEffect(registration.stationImageUri) {
        if (selectedImageUri == null) {
            registration.stationImageUri?.let { uriString ->
                if (uriString.isNotBlank()) {
                    selectedImageUri = Uri.parse(uriString)
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                selectedImageUri = uri
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = java.io.File(context.cacheDir, "station_photo_${System.currentTimeMillis()}.jpg")
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            tempCameraUri = photoUri
            cameraLauncher.launch(photoUri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subir Foto",
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
                            contentDescription = "Volver",
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
                            contentDescription = "Tema",
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
                painter = painterResource(id = R.drawable.background_food_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.06f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Añade una foto de tu puesto",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

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
                    if (isUploading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = RedButtonColor, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Subiendo imagen...",
                                color = labelColor,
                                fontSize = 14.sp
                            )
                        }
                    } else if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                tint = labelColor,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No hay imagen seleccionada",
                                color = labelColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ImageUploadOption(
                        icon = Icons.Filled.CameraAlt,
                        label = "Tomar foto",
                        description = "Usa la cámara para capturar tu puesto",
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textColor = textColor,
                        labelColor = labelColor,
                        iconBg = iconBg,
                        iconTint = iconTint,
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                    )

                    ImageUploadOption(
                        icon = Icons.Filled.PhotoLibrary,
                        label = "Galería",
                        description = "Selecciona una foto desde tu galería",
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textColor = textColor,
                        labelColor = labelColor,
                        iconBg = iconBg,
                        iconTint = iconTint,
                        onClick = { galleryLauncher.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val uri = selectedImageUri
                        if (uri != null) {
                            if (uri.toString().startsWith("http")) {
                                viewModel.updateImage(uri.toString())
                                onSaveClick()
                            } else {
                                isUploading = true
                                uploadError = null
                                scope.launch {
                                    try {
                                        val storage = FirebaseStorage.getInstance()
                                        val storageRef = storage.reference.child("station_photos/${System.currentTimeMillis()}.jpg")
                                        storageRef.putFile(uri).await()
                                        val downloadUrl = storageRef.downloadUrl.await().toString()
                                        viewModel.updateImage(downloadUrl)
                                        isUploading = false
                                        onSaveClick()
                                    } catch (e: Exception) {
                                        Log.e("ImageUpload", "Upload failed", e)
                                        uploadError = "Error al subir: ${e.message}"
                                        isUploading = false
                                    }
                                }
                            }
                        } else {
                            viewModel.updateImage(null)
                            onSaveClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedButtonColor),
                    enabled = !isUploading
                ) {
                    Text(text = "Guardar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                uploadError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
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
            .border(width = 1.dp, color = cardBorder, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
            Text(text = description, color = labelColor, fontSize = 12.sp)
        }
    }
}
