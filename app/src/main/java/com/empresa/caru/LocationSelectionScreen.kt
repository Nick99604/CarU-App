package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
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
fun LocationSelectionScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val registration = viewModel.registration
    var address by remember(registration.value) { mutableStateOf(registration.value.address) }

    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val labelColor      = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val fieldBg         = if (isDarkTheme) Color(0xFF2C2C2C)  else Color(0xFFDEDEDE)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)
    val mapBorder       = if (isDarkTheme) Color(0xFF3A3A3A)  else Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.location_selection_screen_title),
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
                    text = stringResource(R.string.location_selection_subtitle),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = labelColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(
                            width = 1.dp,
                            color = mapBorder,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = RedButtonColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.map_placeholder_text),
                            fontFamily = CaruFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = labelColor,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.map_placeholder_subtext),
                            fontFamily = CaruFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = labelColor,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.exact_address_label),
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.exact_address_placeholder),
                            color = labelColor,
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        disabledContainerColor = fieldBg,
                        focusedBorderColor = RedButtonColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = RedButtonColor
                    ),
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.updateAddress(address)
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
