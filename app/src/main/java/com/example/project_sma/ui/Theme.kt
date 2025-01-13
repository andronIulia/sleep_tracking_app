package com.example.project_sma.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MidnightBlue = Color(0xFF262B40) // Deep, calming blue
val MintGreen = Color(0xFF06457F)    //  blue
val Lavender = Color(0xFFC5CAE9)     // Subtle lavender for contrast
val SoftWhite = Color(0xFFA8C4EC)    // Light text color(light blue)
val DimGrey = Color(0xFF5379AE)      //light blue


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