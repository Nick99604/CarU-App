package com.empresa.caru

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.empresa.caru.domain.model.FoodStation
import com.empresa.caru.domain.repository.SharedStationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    savedStationsViewModel: SavedStationsViewModel,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSavedStationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStationClick: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current

    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    val uiState by viewModel.uiState.collectAsState()
    val savedStationsState by savedStationsViewModel.uiState.collectAsState()
    val stations = uiState.stations
    val savedIds: kotlinx.coroutines.flow.StateFlow<Set<String>> = SharedStationRepository.savedStationIds
    val savedIdsValue by savedIds.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var mapExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val defaultPosition = LatLng(4.7110, -74.0721) // Bogotá, Colombia
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 12f)
    }

    // Only enable myLocation when permission is granted
    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapType = MapType.NORMAL
        )
    }

    fun centerOnMyLocation() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        try {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        } catch (_: Exception) { }
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (mapExpanded) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false
                    )
                ) {
                    stations.forEach { station ->
                        val lat = station.latitude
                        val lng = station.longitude
                        if (lat != null && lng != null) {
                            Marker(
                                state = MarkerState(position = LatLng(lat, lng)),
                                title = station.name,
                                snippet = station.address,
                                onClick = {
                                    onStationClick(station.id)
                                    true
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { mapExpanded = false },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar mapa",
                        tint = if (isDarkTheme) Color.White else Color.Black
                    )
                }

                FloatingActionButton(
                    onClick = { centerOnMyLocation() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 32.dp),
                    containerColor = RedButtonColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación"
                    )
                }
            }
        } else {
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
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "CarU",
                            fontFamily = CaruTitleFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = RedButtonColor
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { mapExpanded = true },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Ver mapa",
                                tint = if (isDarkTheme) Color.White else Color(0xFF333333)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar",
                                tint = if (isDarkTheme) Color.White else Color(0xFF333333)
                            )
                        }

                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Cambiar tema",
                                tint = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333)
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Menú de perfil",
                                    tint = if (isDarkTheme) Color.White else Color(0xFF333333)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Mi perfil", fontFamily = CaruFontFamily) },
                                    onClick = { showMenu = false; onProfileClick() },
                                    leadingIcon = { Icon(Icons.Default.Person, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Favoritos", fontFamily = CaruFontFamily) },
                                    onClick = { showMenu = false; onFavoritesClick() },
                                    leadingIcon = { Icon(Icons.Default.Favorite, null, tint = RedButtonColor) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Guardados", fontFamily = CaruFontFamily) },
                                    onClick = { showMenu = false; onSavedStationsClick() },
                                    leadingIcon = { Icon(Icons.Default.Bookmark, null, tint = RedButtonColor) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Ajustes", fontFamily = CaruFontFamily) },
                                    onClick = { showMenu = false; onSettingsClick() },
                                    leadingIcon = { Icon(Icons.Default.Settings, null) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                        titleContentColor = textColor
                    )
                )

                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                CircularProgressIndicator(color = RedButtonColor)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Cargando puestos...", fontFamily = CaruFontFamily, fontSize = 14.sp, color = textColor.copy(alpha = 0.6f))
                            }
                        }
                    }
                    uiState.error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(60.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Error: ${uiState.error}", fontFamily = CaruFontFamily, fontSize = 14.sp, color = Color.Red, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.refresh() }) { Text("Reintentar") }
                            }
                        }
                    }
                    stations.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                Icon(Icons.Default.Store, null, tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No hay puestos disponibles", fontFamily = CaruFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Toca el botón de actualizar para recargar", fontFamily = CaruFontFamily, fontSize = 14.sp, color = textColor.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(stations, key = { it.id }) { station ->
                                StationCard(
                                    station = station,
                                    cardBg = cardBg,
                                    textColor = textColor,
                                    isFavorite = station.id in favoriteIds,
                                    isSaved = station.id in savedIdsValue,
                                    onClick = { onStationClick(station.id) },
                                    onFavoriteClick = { favoritesViewModel.toggleFavorite(station) },
                                    onSaveClick = { savedStationsViewModel.toggleSavedStation(station) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationCard(
    station: FoodStation,
    cardBg: Color,
    textColor: Color,
    isFavorite: Boolean,
    isSaved: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(textColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (station.imageUrl != null) {
                    AsyncImage(
                        model = station.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Store, null, tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    fontFamily = CaruFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = textColor.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = station.address,
                        fontFamily = CaruFontFamily,
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (station.foodTypes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = station.foodTypes.take(3).joinToString(", "),
                        fontFamily = CaruFontFamily,
                        fontSize = 11.sp,
                        color = RedButtonColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (station.priceMin.isNotBlank() && station.priceMax.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${station.priceMin} - $${station.priceMax}",
                        fontFamily = CaruFontFamily,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) RedButtonColor else textColor.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isSaved) "Quitar de guardados" else "Guardar puesto",
                        tint = if (isSaved) RedButtonColor else textColor.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
