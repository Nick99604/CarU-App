package com.empresa.caru

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.empresa.caru.domain.repository.SharedStationRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Locale

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
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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
    
    val isOwner = registration.ownerId == currentUserId

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
                    if (isOwner) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = RedButtonColor)
                        }
                    }
                    
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

                    // Estado actual (Abierto/Cerrado)
                    val statusPair = getCurrentStatus(registration.schedule)
                    val isOpenNow = statusPair.first
                    val statusMessage = statusPair.second

                    Surface(
                        color = if (isOpenNow) Color(0xFF4CAF50) else Color(0xFFFF5252),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = if (isOpenNow) "ABIERTO AHORA" else "CERRADO AHORA ($statusMessage)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    DetailItem("👤 Vendedor", vendorDisplayName)
                    
                    // Sección Ubicación con botones
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Text(text = "📍 Ubicación", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.05f)).padding(10.dp)
                        ) {
                            Text(text = registration.address.ifBlank { "Sin dirección" }, fontWeight = FontWeight.SemiBold, color = textColor)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${registration.latitude},${registration.longitude}?q=${registration.latitude},${registration.longitude}(${registration.stationName})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ver Mapa", fontSize = 12.sp)
                            }
                            
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("google.navigation:q=${registration.latitude},${registration.longitude}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = RedButtonColor),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cómo llegar", fontSize = 12.sp)
                            }
                        }
                    }

                    DetailItem("📞 Teléfono", registration.contactPhone.ifBlank { "Sin teléfono" })
                    
                    val foodText = if (registration.foodTypes.isNotEmpty()) {
                        registration.foodTypes.joinToString(", ") { it.label }
                    } else registration.otherFoodType
                    
                    DetailItem("🍔 Especialidad", foodText.ifBlank { "No especificado" })
                    
                    if (registration.averagePriceMin.isNotBlank()) {
                        DetailItem("💰 Precios", "$${registration.averagePriceMin} - $${registration.averagePriceMax}")
                    }

                    // Sección Horario
                    DetailItem("⏰ Horario", formatSchedule(registration.schedule))
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
    val textColor = LocalContentColor.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.05f)).padding(10.dp)
        ) {
            Text(text = value, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }
}

fun formatSchedule(schedule: StationSchedule): String {
    val days = listOf(
        "Lunes" to schedule.monday,
        "Martes" to schedule.tuesday,
        "Miércoles" to schedule.wednesday,
        "Jueves" to schedule.thursday,
        "Viernes" to schedule.friday,
        "Sábado" to schedule.saturday,
        "Domingo" to schedule.sunday
    )
    
    val sb = StringBuilder()
    days.forEach { (name, day) ->
        if (day.isOpen) {
            sb.append("$name: ${day.startTime} - ${day.endTime}\n")
        } else {
            sb.append("$name: Cerrado\n")
        }
    }
    return sb.toString().trim().ifBlank { "Horario no disponible" }
}

/**
 * Retorna si el puesto está abierto actualmente y un mensaje opcional.
 * Utiliza comparación de minutos para mayor robustez.
 */
fun getCurrentStatus(schedule: StationSchedule): Pair<Boolean, String> {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    
    val daySchedule = when (dayOfWeek) {
        Calendar.MONDAY -> schedule.monday
        Calendar.TUESDAY -> schedule.tuesday
        Calendar.WEDNESDAY -> schedule.wednesday
        Calendar.THURSDAY -> schedule.thursday
        Calendar.FRIDAY -> schedule.friday
        Calendar.SATURDAY -> schedule.saturday
        Calendar.SUNDAY -> schedule.sunday
        else -> DaySchedule()
    }
    
    if (!daySchedule.isOpen) return false to "Cerrado hoy"
    
    val startMinutes = parseTimeToMinutes(daySchedule.startTime)
    val endMinutes = parseTimeToMinutes(daySchedule.endTime)
    
    if (startMinutes == null || endMinutes == null) return false to "Horario no definido"
    
    // Manejo de horarios que cruzan la medianoche
    val isOpen = if (startMinutes < endMinutes) {
        currentMinutes in startMinutes..endMinutes
    } else {
        // Ejemplo: 22:00 a 02:00
        currentMinutes >= startMinutes || currentMinutes <= endMinutes
    }
    
    return isOpen to (if (isOpen) "Abierto" else "Cerrado")
}

/**
 * Parsea una cadena de tiempo (HH:mm) a minutos totales desde el inicio del día.
 * Soporta formatos con o sin cero inicial.
 */
fun parseTimeToMinutes(time: String): Int? {
    return try {
        val parts = time.split(":")
        if (parts.size != 2) return null
        val hours = parts[0].trim().toInt()
        val minutes = parts[1].trim().toInt()
        hours * 60 + minutes
    } catch (e: Exception) {
        null
    }
}
