package com.example.project_sma

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MidnightBlue = Color(0xFF121A2E) // Deep, calming blue
val MintGreen = Color(0xFF80CBC4)    // Gentle teal for accents
val Lavender = Color(0xFFC5CAE9)     // Subtle lavender for contrast
val SoftWhite = Color(0xFFECEFF1)    // Light text color
val DimGrey = Color(0xFF37474F)      // Neutral grey for less important elements


private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    secondary = Lavender,
    background = MidnightBlue,
    surface = DimGrey,
    onPrimary = MidnightBlue,
    onSecondary = SoftWhite,
    onBackground = SoftWhite,
    onSurface = SoftWhite
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = SoftWhite
    ),
    bodyLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        color = SoftWhite
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        color = DimGrey
    )
)


@Composable
fun SleepTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}