package com.empresa.caru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CarUAppStartScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Fondo con patrón
        Image(
            painter = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.08f
        )

        // Botón de Modo Oscuro (Restaurado)
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                contentDescription = "Cambiar tema",
                tint = textColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo (Tamaño restaurado)
            Image(
                painter = painterResource(id = R.drawable.logo_caru),
                contentDescription = stringResource(R.string.logo_caru_description),
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CarU",
                fontFamily = CaruTitleFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 54.sp,
                color = RedButtonColor
            )

            Text(
                text = "“Lo mejor cerca de ti”",
                fontFamily = CaruFontFamily,
                fontSize = 20.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Botón Iniciar Sesión
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedButtonColor)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Crear Cuenta
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedButtonColor)
            ) {
                Text(
                    text = "Crear cuenta",
                    fontFamily = CaruFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}
