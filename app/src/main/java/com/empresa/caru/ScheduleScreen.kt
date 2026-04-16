package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration = viewModel.registration
    var schedule by remember(registration.value) { mutableStateOf<StationSchedule>(registration.value.schedule) }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val cardBorder      = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)
    val fieldBg         = if (isDarkTheme) Color(0xFF2C2C2C)  else Color(0xFFDEDEDE)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    // Configuración rápida
    fun applyQuickSchedule(preset: SchedulePreset) {
        val newSchedule = when (preset) {
            SchedulePreset.ALL_DAYS -> schedule.copy(
                monday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                tuesday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                wednesday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                thursday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                friday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                saturday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00"),
                sunday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "20:00")
            )
            SchedulePreset.WEEKDAYS -> schedule.copy(
                monday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "18:00"),
                tuesday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "18:00"),
                wednesday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "18:00"),
                thursday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "18:00"),
                friday = DaySchedule(isOpen = true, startTime = "08:00", endTime = "18:00"),
                saturday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                sunday = DaySchedule(isOpen = false, startTime = "", endTime = "")
            )
            SchedulePreset.WEEKENDS -> schedule.copy(
                monday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                tuesday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                wednesday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                thursday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                friday = DaySchedule(isOpen = false, startTime = "", endTime = ""),
                saturday = DaySchedule(isOpen = true, startTime = "09:00", endTime = "22:00"),
                sunday = DaySchedule(isOpen = true, startTime = "09:00", endTime = "22:00")
            )
        }
        schedule = newSchedule
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.schedule_screen_title),
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
            // Fondo decorativo
            Image(
                painter = painterResource(id = R.drawable.background_food_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isDarkTheme) 0.06f else 0.06f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Subtítulo
                Text(
                    text = stringResource(R.string.schedule_subtitle),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Botones rápidos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickScheduleButton(
                        text = stringResource(R.string.all_days_button),
                        onClick = { applyQuickSchedule(SchedulePreset.ALL_DAYS) },
                        modifier = Modifier.weight(1f),
                        textColor = textColor,
                        buttonBg = cardBg,
                        borderColor = cardBorder
                    )
                    QuickScheduleButton(
                        text = stringResource(R.string.weekdays_button),
                        onClick = { applyQuickSchedule(SchedulePreset.WEEKDAYS) },
                        modifier = Modifier.weight(1f),
                        textColor = textColor,
                        buttonBg = cardBg,
                        borderColor = cardBorder
                    )
                    QuickScheduleButton(
                        text = stringResource(R.string.weekends_button),
                        onClick = { applyQuickSchedule(SchedulePreset.WEEKENDS) },
                        modifier = Modifier.weight(1f),
                        textColor = textColor,
                        buttonBg = cardBg,
                        borderColor = cardBorder
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Lista de días
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.monday_label),
                            daySchedule = schedule.monday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    monday = schedule.monday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    monday = schedule.monday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    monday = schedule.monday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.tuesday_label),
                            daySchedule = schedule.tuesday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    tuesday = schedule.tuesday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    tuesday = schedule.tuesday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    tuesday = schedule.tuesday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.wednesday_label),
                            daySchedule = schedule.wednesday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    wednesday = schedule.wednesday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    wednesday = schedule.wednesday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    wednesday = schedule.wednesday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.thursday_label),
                            daySchedule = schedule.thursday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    thursday = schedule.thursday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    thursday = schedule.thursday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    thursday = schedule.thursday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.friday_label),
                            daySchedule = schedule.friday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    friday = schedule.friday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    friday = schedule.friday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    friday = schedule.friday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.saturday_label),
                            daySchedule = schedule.saturday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    saturday = schedule.saturday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    saturday = schedule.saturday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    saturday = schedule.saturday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        DayScheduleItem(
                            dayName = stringResource(R.string.sunday_label),
                            daySchedule = schedule.sunday,
                            fieldBg = fieldBg,
                            labelColor = labelColor,
                            textColor = textColor,
                            iconTint = iconTint,
                            onToggle = { isOpen ->
                                schedule = schedule.copy(
                                    sunday = schedule.sunday.copy(isOpen = isOpen)
                                )
                            },
                            onStartTimeChange = { time ->
                                schedule = schedule.copy(
                                    sunday = schedule.sunday.copy(startTime = time)
                                )
                            },
                            onEndTimeChange = { time ->
                                schedule = schedule.copy(
                                    sunday = schedule.sunday.copy(endTime = time)
                                )
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Botón Continuar
                Button(
                    onClick = {
                        viewModel.updateSchedule(schedule)
                        onContinueClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedButtonColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.continue_button),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private enum class SchedulePreset {
    ALL_DAYS, WEEKDAYS, WEEKENDS
}

@Composable
private fun QuickScheduleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color,
    buttonBg: Color,
    borderColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(buttonBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Normal,
            color = textColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DayScheduleItem(
    dayName: String,
    daySchedule: DaySchedule,
    fieldBg: Color,
    labelColor: Color,
    textColor: Color,
    iconTint: Color,
    onToggle: (Boolean) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (daySchedule.isOpen) fieldBg else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (daySchedule.isOpen) RedButtonColor.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Fila del día con checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (daySchedule.isOpen) RedButtonColor else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (daySchedule.isOpen) RedButtonColor else labelColor,
                        shape = CircleShape
                    )
                    .clickable {
                        onToggle(!daySchedule.isOpen)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (daySchedule.isOpen) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = dayName,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Horario (solo si está abierto)
        if (daySchedule.isOpen) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hora inicio
                TimePickerField(
                    value = daySchedule.startTime.ifEmpty { stringResource(R.string.start_time_placeholder) },
                    onValueChange = onStartTimeChange,
                    label = stringResource(R.string.start_time_label),
                    fieldBg = fieldBg,
                    labelColor = labelColor,
                    iconTint = iconTint,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "-",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 20.sp
                )

                // Hora fin
                TimePickerField(
                    value = daySchedule.endTime.ifEmpty { stringResource(R.string.end_time_placeholder) },
                    onValueChange = onEndTimeChange,
                    label = stringResource(R.string.end_time_label),
                    fieldBg = fieldBg,
                    labelColor = labelColor,
                    iconTint = iconTint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    fieldBg: Color,
    labelColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val timeOptions = generateTimeOptions()

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = {
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = iconTint
                )
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = fieldBg,
                unfocusedContainerColor = fieldBg,
                disabledContainerColor = fieldBg,
                focusedBorderColor = RedButtonColor,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = labelColor,
                unfocusedTextColor = labelColor
            ),
            singleLine = true
        )

        // Invisible clickable overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true }
        )

        if (showPicker) {
            TimePickerDialog(
                options = timeOptions,
                onSelect = { time ->
                    onValueChange(time)
                    showPicker = false
                },
                onDismiss = { showPicker = false },
                labelColor = labelColor,
                textColor = labelColor,
                dialogBg = fieldBg
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    options: List<String>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    labelColor: Color,
    textColor: Color,
    dialogBg: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        title = {
            Text(
                text = stringResource(R.string.select_time_title),
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(options) { time ->
                    Text(
                        text = time,
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = labelColor,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(time) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel_button),
                    color = RedButtonColor
                )
            }
        }
    )
}

private fun generateTimeOptions(): List<String> {
    val times = mutableListOf<String>()
    for (hour in 0..23) {
        for (minute in listOf("00", "30")) {
            val h = hour.toString().padStart(2, '0')
            times.add("$h:$minute")
        }
    }
    return times
}
