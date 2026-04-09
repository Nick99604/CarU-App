package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onBuscarClick: () -> Unit,
    onRegistrarClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    innerPadding: PaddingValues
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor       = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val sloganColor     = if (isDarkTheme) Color(0xFFAAAAAA)  else Color(0xFF555555)
    val cardBg          = if (isDarkTheme) Color(0xFF2A2A2A)  else Color(0xFFFFFFFF)
    val iconBg          = if (isDarkTheme) Color(0xFF333333)  else Color(0xFFE0E0E0)
    val iconTint        = if (isDarkTheme) Color.White        else Color(0xFF333333)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ── Fondo decorativo ────────────────────────────────────────────
        Image(
            painter       = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = null,
            modifier      = Modifier.matchParentSize(),
            contentScale  = ContentScale.Crop,
            alpha         = if (isDarkTheme) 0.06f else 0.08f   // sutil, no distrae
        )

        // ── Botón regresar ───────────────────────────────────────────────
        IconButton(
            onClick  = onBackClick,
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

        // ── Botón tema ───────────────────────────────────────────────────
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

        // ── Contenido principal ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            // Título
            Text(
                text       = "Elige una opción\npara continuar",
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Bold,
                color      = textColor,
                fontSize   = 30.sp,
                textAlign  = TextAlign.Start,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            // ── Tarjeta 1 ────────────────────────────────────────────────
            OptionCard(
                emoji       = "🍔",
                description = "\"Crea una cuenta y encuentra tu puesto de comida favorito\"",
                cardBg      = cardBg,
                sloganColor = sloganColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón 1
            Button(
                onClick  = onBuscarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Buscar puestos de comida",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Tarjeta 2 ────────────────────────────────────────────────
            OptionCard(
                emoji       = "🛺",
                description = "\"Comparte tu magia\"",
                cardBg      = cardBg,
                sloganColor = sloganColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón 2
            Button(
                onClick  = onRegistrarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Registra tu puesto",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp
                )
            }
        }
    }
}

// ── Componente reutilizable de tarjeta de opción ─────────────────────────────
@Composable
private fun OptionCard(
    emoji: String,
    description: String,
    cardBg: Color,
    sloganColor: Color
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = emoji,
                fontSize = 52.sp,
                modifier = Modifier.padding(end = 14.dp)
            )
            Text(
                text       = description,
                fontFamily = CaruFontFamily,
                fontWeight = FontWeight.Normal,
                color      = sloganColor,
                fontSize   = 15.sp,
                textAlign  = TextAlign.Start,
                lineHeight = 22.sp
            )
        }
    }
}