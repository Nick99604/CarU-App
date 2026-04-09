package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.empresa.caru.R

@Composable
fun ResetEmailSentScreen(
    onBackToLoginClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Fondo decorativo
        Image(
            painter            = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = null,
            modifier           = Modifier.matchParentSize(),
            contentScale       = ContentScale.Crop,
            alpha              = if (isDarkTheme) 0.06f else 0.06f
        )

        // Botón retroceder
        IconButton(
            onClick  = onBackToLoginClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 52.dp, start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector        = Icons.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint               = iconTint,
                modifier           = Modifier.size(22.dp)
            )
        }

        // Botón tema
        IconButton(
            onClick  = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector        = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Cambiar tema",
                tint               = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333),
                modifier           = Modifier.size(22.dp)
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono de check
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(RedButtonColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Check,
                    contentDescription = "Enviado",
                    tint               = Color.White,
                    modifier           = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de éxito
            Text(
                text       = "Tu enlace fue enviado\ncorrectamente",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color      = textColor,
                fontSize   = 24.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text       = "Revisa tu correo electrónico y sigue\nlas instrucciones para restablecer tu\ncontraseña.",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Normal,
                color      = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF555555),
                fontSize   = 16.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 24.sp,
                modifier   = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botón Menú principal
            Button(
                onClick  = onBackToLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape    = RoundedCornerShape(50.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = RedButtonColor,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Menú principal",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp
                )
            }
        }
    }
}
