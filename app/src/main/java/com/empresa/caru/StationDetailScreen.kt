package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AttachMoney
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

@Composable
fun StationDetailScreen(
    viewModel: RegistrationViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration by viewModel.registration.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val fieldBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)

    // Observar favoritos reactivamente
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()

    // Crear FoodStation temporal para favoritos
    val tempStation = remember(registration) {
        FoodStation(
            id = registration.address.ifBlank { "temp_station" },
            name = registration.address.ifBlank { "Mi Puesto" },
            vendorName = "Usuario",
            address = registration.address,
            phone = registration.contactPhone,
            foodTypes = registration.foodTypes.map { it.label },
            imageUrl = registration.stationImageUri,
            priceMin = registration.averagePriceMin,
            priceMax = registration.averagePriceMax
        )
    }

    val isFavoriteStation = tempStation.id in favoriteIds

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar puesto",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de eliminar tu puesto?",
                    fontFamily = CaruFontFamily
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.reset()
                    onDeleteConfirm()
                }) {
                    Text("Confirmar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
            alpha = if (isDarkTheme) 0.05f else 0.9f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                IconButton(
                    onClick = {
                        favoritesViewModel.toggleFavorite(tempStation)
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavoriteStation) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavoriteStation) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavoriteStation) Color.Red else textColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mi puesto",
                        fontFamily = CaruTitleFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val imageUri = registration.stationImageUri

                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoField("🌮 Puesto", registration.address.ifBlank { "Mi puesto" })
                    InfoField("👤 Vendedor", "Carlos Ramírez")
                    InfoField("📍 Ubicación", registration.address.ifBlank { "-" })
                    InfoField("📞 Teléfono", registration.contactPhone.ifBlank { "-" })

                    val foodText = if (registration.foodTypes.isNotEmpty()) {
                        registration.foodTypes.joinToString(", ") { it.label }
                    } else registration.otherFoodType

                    InfoField("🍔 Tipo de comida", foodText.ifBlank { "-" })
                    InfoField("⏰ Horario", "Lunes - Viernes\n8:00 AM - 6:00 PM")

                    if (registration.averagePriceMin.isNotBlank()) {
                        InfoField(
                            "💰 Precio promedio",
                            "$${registration.averagePriceMin} - $${registration.averagePriceMax}"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Edita la información",
                        fontFamily = CaruFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppButton(
                text = "Editar información",
                onClick = onEditClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppButton(
                text = "Eliminar puesto",
                onClick = { showDeleteDialog = true },
                buttonColor = Color.Red
            )
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontFamily = CaruFontFamily,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(10.dp)
        ) {
            Text(
                text = value,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}