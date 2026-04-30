package com.empresa.caru

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStationScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onFoodTypeClick: () -> Unit,
    onInfoClick: () -> Unit,
    onLocationClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onImageClick: () -> Unit,
    onSuccess: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val cardBorder      = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)
    val completedColor  = Color(0xFF4CAF50)
    val pendingColor    = if (isDarkTheme) Color(0xFF555555) else Color(0xFFBDBDBD)

    // Recolectar estado del ViewModel para reaccionalidad
    val foodTypeCompleted by viewModel.foodTypeCompleted.collectAsState()
    val infoCompleted by viewModel.infoCompleted.collectAsState()
    val locationCompleted by viewModel.locationCompleted.collectAsState()
    val scheduleCompleted by viewModel.scheduleCompleted.collectAsState()
    val imageCompleted by viewModel.imageCompleted.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState() // Estado de carga
    val completedCount = viewModel.completedCount
    val totalSections = viewModel.totalSections

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.registration_station_title),
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
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.registration_menu_subtitle),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Barra de progreso
                val progress = completedCount.toFloat() / totalSections
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(RedButtonColor)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$completedCount/$totalSections ${stringResource(R.string.sections_completed)}",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SectionMenuItem(
                            icon = Icons.Filled.Restaurant,
                            title = stringResource(R.string.section_food_type),
                            subtitle = stringResource(R.string.section_food_type_desc),
                            isCompleted = foodTypeCompleted,
                            completedColor = completedColor,
                            pendingColor = pendingColor,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            onClick = onFoodTypeClick
                        )
                    }

                    item {
                        SectionMenuItem(
                            icon = Icons.Filled.Message,
                            title = stringResource(R.string.section_information),
                            subtitle = stringResource(R.string.section_information_desc),
                            isCompleted = infoCompleted,
                            completedColor = completedColor,
                            pendingColor = pendingColor,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            onClick = onInfoClick
                        )
                    }

                    item {
                        SectionMenuItem(
                            icon = Icons.Filled.LocationOn,
                            title = stringResource(R.string.section_location),
                            subtitle = stringResource(R.string.section_location_desc),
                            isCompleted = locationCompleted,
                            completedColor = completedColor,
                            pendingColor = pendingColor,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            onClick = onLocationClick
                        )
                    }

                    item {
                        SectionMenuItem(
                            icon = Icons.Filled.AccessTime,
                            title = stringResource(R.string.section_schedule),
                            subtitle = stringResource(R.string.section_schedule_desc),
                            isCompleted = scheduleCompleted,
                            completedColor = completedColor,
                            pendingColor = pendingColor,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            onClick = onScheduleClick
                        )
                    }

                    item {
                        SectionMenuItem(
                            icon = Icons.Filled.AddCircleOutline,
                            title = stringResource(R.string.section_photo),
                            subtitle = stringResource(R.string.section_photo_desc),
                            isCompleted = imageCompleted,
                            completedColor = completedColor,
                            pendingColor = pendingColor,
                            iconBg = iconBg,
                            iconTint = iconTint,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            onClick = onImageClick
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // BOTÓN CORREGIDO
                Button(
                    onClick = {
                        Log.d("RegStationScreen", "BOTÓN PRESIONADO - isAllCompleted=${viewModel.isAllCompleted}")
                        if (viewModel.isAllCompleted) {
                            Log.d("RegStationScreen", "Llamando viewModel.saveStation...")
                            viewModel.saveStation { success ->
                                Log.d("RegStationScreen", "saveStation callback: success=$success")
                                if (success) {
                                    onSuccess()
                                }
                            }
                        } else {
                            Log.d("RegStationScreen", "isAllCompleted es false, no se puede guardar")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isAllCompleted) RedButtonColor
                        else if (isDarkTheme) Color(0xFF444444) else Color(0xFFBDBDBD),
                        contentColor = Color.White,
                        disabledContainerColor = if (isDarkTheme) Color(0xFF444444) else Color(0xFFBDBDBD),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (viewModel.isAllCompleted) 4.dp else 0.dp
                    ),
                    enabled = viewModel.isAllCompleted && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        if (viewModel.isAllCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (viewModel.isAllCompleted)
                                stringResource(R.string.finish_registration)
                            else
                                stringResource(R.string.complete_all_sections),
                            fontFamily = CaruFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    completedColor: Color,
    pendingColor: Color,
    iconBg: Color,
    iconTint: Color,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                width = if (isCompleted) 2.dp else 1.dp,
                color = if (isCompleted) completedColor else cardBorder,
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
                text = title,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Normal,
                color = if (isCompleted) completedColor else pendingColor,
                fontSize = 12.sp
            )
        }

        if (isCompleted) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(completedColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.section_completed),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = pendingColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}