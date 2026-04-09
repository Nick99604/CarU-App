package com.empresa.caru

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// Colores compartidos entre todas las pantallas
val RedButtonColor = Color(0xFFFF0000)
val TextColorSecondary = Color(0xFF181717)

// Fuente para botones y slogan
val CaruFontFamily = FontFamily(
    Font(R.font.caru_font, FontWeight.Normal),
    Font(R.font.caru_font, FontWeight.Bold)
)

// Fuente exclusiva para el titulo CarU
val CaruTitleFontFamily = FontFamily(
    Font(R.font.caru_title, FontWeight.Normal),
    Font(R.font.caru_title, FontWeight.Bold)
)