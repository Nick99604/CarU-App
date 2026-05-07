package com.empresa.caru

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    savedStationsViewModel: SavedStationsViewModel,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSavedStationsClick: () -> Unit,
    onMyStationsClick: () -> Unit,
    onCreateStationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onStationClick: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasLocationPermission = isGranted }

    val uiState by viewModel.uiState.collectAsState()
    val stations = uiState.stations
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var showSidebar by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.7110, -74.0721), 14f)
    }

    val mapProperties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission, mapType = MapType.NORMAL)
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
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
                }
            }
        } catch (_: Exception) { }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Capa 1: Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false, compassEnabled = false)
        ) {
            stations.forEach { station ->
                val lat = station.latitude
                val lng = station.longitude
                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        title = station.name,
                        snippet = station.address,
                        onClick = { onStationClick(station.id); true }
                    )
                }
            }
        }

        // Capa 2: UI Overlay (Barra superior y botones inferiores)
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = RedButtonColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "CarU", fontFamily = CaruTitleFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = RedButtonColor)
                }

                // Botón Usuario
                IconButton(
                    onClick = { showSidebar = true },
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).border(2.dp, RedButtonColor, CircleShape)
                ) {
                    Icon(Icons.Default.Person, "Menú", tint = RedButtonColor, modifier = Modifier.size(28.dp))
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.navigationBarsPadding().padding(bottom = 32.dp, start = 32.dp, end = 32.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(onClick = { centerOnMyLocation() }, modifier = Modifier.height(48.dp).width(100.dp), shape = RoundedCornerShape(24.dp), color = RedButtonColor, shadowElevation = 4.dp) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.MyLocation, "Mi ubicación", tint = Color.White, modifier = Modifier.size(24.dp)) }
                }
                Surface(onClick = onSavedStationsClick, modifier = Modifier.height(48.dp).width(100.dp), shape = RoundedCornerShape(24.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, RedButtonColor), shadowElevation = 4.dp) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.BookmarkBorder, "Guardados", tint = RedButtonColor, modifier = Modifier.size(24.dp)) }
                }
            }
        }

        // Capa 3: Fondo Oscuro (Detrás del menú)
        AnimatedVisibility(visible = showSidebar, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showSidebar = false }
            )
        }

        // Capa 4: Menú Lateral (Sidebar)
        AnimatedVisibility(
            visible = showSidebar,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.75f).background(RedButtonColor)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { } // Detiene clics al fondo
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(onClick = { showSidebar = false }, modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White)) {
                        Icon(Icons.Default.Close, "Cerrar", tint = RedButtonColor, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SidebarItem("Mi perfil") { showSidebar = false; onProfileClick() }
                    SidebarItem("Favoritos") { showSidebar = false; onFavoritesClick() }
                    
                    if (uiState.userStations.isNotEmpty()) {
                        SidebarItem("Mis puestos") { showSidebar = false; onMyStationsClick() }
                    }
                    
                    SidebarItem("Crear nuevo puesto") { showSidebar = false; onCreateStationClick() }

                    SidebarItem("Ajustes") { showSidebar = false; onSettingsClick() }

                    Spacer(modifier = Modifier.weight(1f))

                    SidebarItem("Cerrar sesión") { showSidebar = false; onLogoutClick() }
                }
            }
        }
    }
}

@Composable
private fun SidebarItem(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
            Text(text = text, fontFamily = CaruFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }
    }
}
