package com.empresa.caru

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.domain.model.FoodStation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStationsScreen(
    viewModel: HomeViewModel,
    onBackClick: () -> Unit,
    onCreateStationClick: () -> Unit,
    onStationClick: (FoodStation) -> Unit,
    isDarkTheme: Boolean,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val iconBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Puestos",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateStationClick,
                containerColor = RedButtonColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear nuevo puesto")
            }
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(R.drawable.background_food_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isDarkTheme) 0.05f else 0.1f
            )

            if (uiState.userStations.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes puestos creados aún",
                        fontFamily = CaruFontFamily,
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.userStations) { station ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onStationClick(station) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(RedButtonColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = RedButtonColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = station.name,
                                        fontFamily = CaruFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )
                                    Text(
                                        text = station.address,
                                        fontFamily = CaruFontFamily,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
