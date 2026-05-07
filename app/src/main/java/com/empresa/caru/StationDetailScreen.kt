package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.SharedStationRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun StationDetailScreen(
    stationId: String?,
    viewModel: RegistrationViewModel,
    favoritesViewModel: FavoritesViewModel,
    savedStationsViewModel: SavedStationsViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onSaveClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration by viewModel.registration.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Cargar los datos del puesto específico basándose en el ID recibido
    LaunchedEffect(stationId) {
        if (stationId != null) {
            viewModel.loadStationToRegistration(stationId)
        }
    }

    val vendorDisplayName = registration.vendorName.ifBlank { "Vendedor" }
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val fieldBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)

    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val isFavoriteStation = stationId != null && stationId in favoriteIds
    
    val savedIds by SharedStationRepository.savedStationIds.collectAsState()
    val isSavedStation = stationId != null && stationId in savedIds

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar puesto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de eliminar este puesto?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteStation { onDeleteConfirm() }
                }) { Text("Confirmar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Image(
            painter = painterResource(R.drawable.background_food_pattern),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.05f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = textColor)
                }
                
                Row {
                    IconButton(onClick = { 
                        val station = SharedStationRepository.getStationById(stationId ?: "")
                        station?.let { favoritesViewModel.toggleFavorite(it) }
                    }) {
                        Icon(
                            imageVector = if (isFavoriteStation) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavoriteStation) Color.Red else textColor
                        )
                    }
                    
                    IconButton(onClick = {
                        val station = SharedStationRepository.getStationById(stationId ?: "")
                        station?.let { savedStationsViewModel.toggleSavedStation(it) }
                    }) {
                        Icon(
                            imageVector = if (isSavedStation) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardado",
                            tint = if (isSavedStation) RedButtonColor else textColor
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = registration.stationName.ifBlank { "Detalles del Puesto" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (registration.stationImageUri != null) {
                        AsyncImage(
                            model = registration.stationImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem("👤 Vendedor", vendorDisplayName)
                    DetailItem("📍 Ubicación", registration.address.ifBlank { "Sin dirección" })
                    DetailItem("📞 Teléfono", registration.contactPhone.ifBlank { "Sin teléfono" })
                    
                    val foodText = if (registration.foodTypes.isNotEmpty()) {
                        registration.foodTypes.joinToString(", ") { it.label }
                    } else registration.otherFoodType
                    
                    DetailItem("🍔 Especialidad", foodText.ifBlank { "No especificado" })
                    
                    if (registration.averagePriceMin.isNotBlank()) {
                        DetailItem("💰 Precios", "$${registration.averagePriceMin} - $${registration.averagePriceMax}")
                    }
                }
            }
        }

        // Botones de acción
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp)) {
            AppButton(text = "Volver al Home", onClick = onSaveClick)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.05f)).padding(10.dp)
        ) {
            Text(text = value, fontWeight = FontWeight.SemiBold)
        }
    }
}
