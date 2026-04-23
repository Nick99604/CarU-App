package com.empresa.caru

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.domain.model.DayScheduleDto

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditStationScreen(
    stationId: String,
    viewModel: StationViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    val editState by viewModel.editState.collectAsState()

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val fieldBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFDEDEDE)
    val fieldTextColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)

    LaunchedEffect(stationId) {
        viewModel.loadStation(stationId)
    }

    LaunchedEffect(uiState) {
        if (uiState is StationUiState.Saved) {
            onSaved()
        }
    }

    val foodTypeOptions = listOf(
        "Desayuno", "Almuerzo", "Cena", "Snacks", "Bebidas",
        "Postres", "Comida rápida", "Comida mexicana",
        "Comida asiática", "Comida italiana", "Vegetariana", "Vegana"
    )

    val dayKeys = listOf(
        "monday" to "Lunes",
        "tuesday" to "Martes",
        "wednesday" to "Miércoles",
        "thursday" to "Jueves",
        "friday" to "Viernes",
        "saturday" to "Sábado",
        "sunday" to "Domingo"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (uiState is StationUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = RedButtonColor
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(fieldBg)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Editar puesto",
                        fontFamily = CaruTitleFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = RedButtonColor
                    )
                }

                OutlinedTextField(
                    value = editState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Nombre del puesto", fontFamily = CaruFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = RedButtonColor,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        focusedTextColor = fieldTextColor,
                        unfocusedTextColor = fieldTextColor,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editState.vendorName,
                    onValueChange = { viewModel.updateVendorName(it) },
                    label = { Text("Vendedor", fontFamily = CaruFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = RedButtonColor,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        focusedTextColor = fieldTextColor,
                        unfocusedTextColor = fieldTextColor,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editState.address,
                    onValueChange = { viewModel.updateAddress(it) },
                    label = { Text("Ubicación", fontFamily = CaruFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = RedButtonColor,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        focusedTextColor = fieldTextColor,
                        unfocusedTextColor = fieldTextColor,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editState.phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Teléfono", fontFamily = CaruFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = RedButtonColor,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        focusedTextColor = fieldTextColor,
                        unfocusedTextColor = fieldTextColor,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editState.priceMin,
                        onValueChange = { viewModel.updatePriceMin(it) },
                        label = { Text("Precio min", fontFamily = CaruFontFamily) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedButtonColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = RedButtonColor,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            focusedTextColor = fieldTextColor,
                            unfocusedTextColor = fieldTextColor,
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editState.priceMax,
                        onValueChange = { viewModel.updatePriceMax(it) },
                        label = { Text("Precio max", fontFamily = CaruFontFamily) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedButtonColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedLabelColor = RedButtonColor,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            focusedTextColor = fieldTextColor,
                            unfocusedTextColor = fieldTextColor,
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg
                        ),
                        singleLine = true
                    )
                }

                Text(
                    text = "Tipos de comida",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    foodTypeOptions.forEach { foodType ->
                        val isSelected = editState.foodTypes.contains(foodType)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newList = if (isSelected) {
                                    editState.foodTypes - foodType
                                } else {
                                    editState.foodTypes + foodType
                                }
                                viewModel.updateFoodTypes(newList)
                            },
                            label = { Text(foodType, fontFamily = CaruFontFamily) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedButtonColor,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }

                Text(
                    text = "Horario",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayKeys.forEach { (dayKey, dayName) ->
                        val daySchedule = when (dayKey) {
                            "monday" -> editState.schedule.monday
                            "tuesday" -> editState.schedule.tuesday
                            "wednesday" -> editState.schedule.wednesday
                            "thursday" -> editState.schedule.thursday
                            "friday" -> editState.schedule.friday
                            "saturday" -> editState.schedule.saturday
                            "sunday" -> editState.schedule.sunday
                            else -> DayScheduleDto()
                        }
                        DayScheduleEditRow(
                            dayName = dayName,
                            daySchedule = daySchedule,
                            onToggleOpen = { viewModel.toggleDayOpen(dayKey) },
                            onStartTimeChange = { viewModel.updateDayTime(dayKey, startTime = it) },
                            onEndTimeChange = { viewModel.updateDayTime(dayKey, endTime = it) },
                            fieldBg = fieldBg,
                            textColor = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.saveStation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedButtonColor),
                    enabled = uiState !is StationUiState.Loading
                ) {
                    Text(
                        text = "Guardar",
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                if (uiState is StationUiState.Error) {
                    Text(
                        text = (uiState as StationUiState.Error).message,
                        color = RedButtonColor,
                        fontFamily = CaruFontFamily,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayScheduleEditRow(
    dayName: String,
    daySchedule: DayScheduleDto,
    onToggleOpen: () -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    fieldBg: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fieldBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = daySchedule.isOpen,
                onCheckedChange = { onToggleOpen() },
                colors = CheckboxDefaults.colors(
                    checkedColor = RedButtonColor,
                    checkmarkColor = Color.White
                )
            )
            Text(
                text = dayName,
                fontFamily = CaruFontFamily,
                fontSize = 14.sp,
                color = textColor
            )
        }

        if (daySchedule.isOpen) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = daySchedule.startTime,
                    onValueChange = onStartTimeChange,
                    label = { Text("Inicio", fontFamily = CaruFontFamily, fontSize = 10.sp) },
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = daySchedule.endTime,
                    onValueChange = onEndTimeChange,
                    label = { Text("Fin", fontFamily = CaruFontFamily, fontSize = 10.sp) },
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg
                    ),
                    singleLine = true
                )
            }
        } else {
            Text(
                text = "Cerrado",
                fontFamily = CaruFontFamily,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.5f)
            )
        }
    }
}
