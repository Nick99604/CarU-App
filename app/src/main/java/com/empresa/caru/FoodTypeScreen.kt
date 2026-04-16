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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTypeScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration = viewModel.registration
    var selectedFoodTypes by remember { mutableStateOf<Set<FoodType>>(emptySet()) }
    var otherText by remember { mutableStateOf("") }

    // Sync with viewModel when screen loads
    LaunchedEffect(registration.value) {
        selectedFoodTypes = registration.value.foodTypes.toSet()
        otherText = registration.value.otherFoodType
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val cardBorder      = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.food_type_screen_title),
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
                alpha = if (isDarkTheme) 0.06f else 0.06f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.food_type_subtitle),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(FoodType.entries) { foodType ->
                        FoodTypeItem(
                            foodType = foodType,
                            isSelected = selectedFoodTypes.contains(foodType),
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            labelColor = labelColor,
                            onToggle = {
                                selectedFoodTypes = if (selectedFoodTypes.contains(foodType)) {
                                    selectedFoodTypes - foodType
                                } else {
                                    selectedFoodTypes + foodType
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.other_type_label),
                            fontFamily = CaruFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = otherText,
                            onValueChange = { otherText = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.other_type_placeholder),
                                    color = labelColor,
                                    fontSize = 15.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = cardBg,
                                unfocusedContainerColor = cardBg,
                                disabledContainerColor = cardBg,
                                focusedBorderColor = RedButtonColor,
                                unfocusedBorderColor = cardBorder,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                cursorColor = RedButtonColor
                            ),
                            singleLine = true
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                Button(
                    onClick = {
                        viewModel.updateFoodTypes(selectedFoodTypes.toList(), otherText)
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
                        text = stringResource(R.string.save_and_continue),
                        fontFamily = CaruFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FoodTypeItem(
    foodType: FoodType,
    isSelected: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    labelColor: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RedButtonColor else cardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = foodType.label,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) RedButtonColor else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (isSelected) RedButtonColor else labelColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
