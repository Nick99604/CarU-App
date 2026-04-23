package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.empresa.caru.domain.model.FoodStation

@Composable
fun SavedStationsScreen(
    stations: List<FoodStation>,
    isLoading: Boolean,
    onStationClick: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Mis Guardados",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = textColor,
                modifier = Modifier.padding(16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RedButtonColor)
                }
            } else if (stations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = textColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes puestos guardados",
                            fontFamily = CaruFontFamily,
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stations) { station ->
                        SavedStationCard(
                            station = station,
                            onClick = { onStationClick(station.id) },
                            cardBg = cardBg,
                            textColor = textColor
                        )
                    }
                }
            }
        }

        BottomNavigationBar(
            isDarkTheme = isDarkTheme,
            selectedIndex = 1,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SavedStationCard(
    station: FoodStation,
    onClick: () -> Unit,
    cardBg: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            AsyncImage(
                model = station.imageUrl,
                contentDescription = station.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.background_food_pattern),
                error = painterResource(R.drawable.background_food_pattern)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = station.name,
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = RedButtonColor
                )
                Text(
                    text = station.address,
                    fontFamily = CaruFontFamily,
                    fontSize = 14.sp,
                    color = textColor
                )
                Text(
                    text = station.foodTypes.take(2).joinToString(", "),
                    fontFamily = CaruFontFamily,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    isDarkTheme: Boolean,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    val selectedColor = RedButtonColor
    val unselectedColor = if (isDarkTheme) Color(0xFF666666) else Color(0xFF999999)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavBarItem(
                icon = Icons.Default.Map,
                label = "Mapa",
                isSelected = selectedIndex == 0,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
            NavBarItem(
                icon = Icons.Default.Bookmark,
                label = "Guardados",
                isSelected = selectedIndex == 1,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = CaruFontFamily,
            fontSize = 12.sp,
            color = if (isSelected) selectedColor else unselectedColor
        )
    }
}
