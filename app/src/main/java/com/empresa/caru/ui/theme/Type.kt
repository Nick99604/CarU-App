// En ui.theme/Type.kt

package com.empresa.caru.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily // Importa FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// No necesitas importar R si no usas R.font

// --- Elimina o comenta estas definiciones si no usas fuentes personalizadas ---
// val CaruFontFamily = FontFamily(...)
// val CaruTitleFontFamily = FontFamily(...)


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        // Usa FontFamily.Default o las fuentes predeterminadas si no tienes personalizadas
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* ... otras definiciones de TextStyle ... */
)