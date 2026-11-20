package com.ereader.ripcardmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Calm, warm palette: sandalwood brown with a soft saffron accent (the diya flame).
private val Sandalwood = Color(0xFF6B4226)
private val SandalwoodLight = Color(0xFFE8C9A8)
private val Saffron = Color(0xFFC77A1C)
private val Cream = Color(0xFFFBF7F1)
private val Parchment = Color(0xFFF3ECE2)
private val Ink = Color(0xFF2B2420)

private val LightColors = lightColorScheme(
    primary = Sandalwood,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF2DFCB),
    onPrimaryContainer = Color(0xFF2E1605),
    secondary = Saffron,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE2BF),
    onSecondaryContainer = Color(0xFF2C1700),
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = Parchment,
    onSurfaceVariant = Color(0xFF5A4F46),
    surfaceContainer = Parchment,
    surfaceContainerLow = Color(0xFFF7F1E9),
    surfaceContainerHigh = Color(0xFFEFE6DA),
    outline = Color(0xFF8D7F73),
    outlineVariant = Color(0xFFD8CCBF),
)

private val DarkColors = darkColorScheme(
    primary = SandalwoodLight,
    onPrimary = Color(0xFF3F2410),
    primaryContainer = Color(0xFF573620),
    onPrimaryContainer = Color(0xFFF7DEC6),
    secondary = Color(0xFFFFB866),
    onSecondary = Color(0xFF482A00),
    secondaryContainer = Color(0xFF673D00),
    onSecondaryContainer = Color(0xFFFFDDB8),
    background = Color(0xFF1A1512),
    onBackground = Color(0xFFEEE0D6),
    surface = Color(0xFF1A1512),
    onSurface = Color(0xFFEEE0D6),
    surfaceVariant = Color(0xFF2A221D),
    onSurfaceVariant = Color(0xFFD4C4B7),
    surfaceContainer = Color(0xFF261F1A),
    surfaceContainerLow = Color(0xFF211B17),
    surfaceContainerHigh = Color(0xFF312923),
    outline = Color(0xFF9D8E82),
    outlineVariant = Color(0xFF4F443C),
)

private val AppTypography = Typography().run {
    copy(
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontSize = 15.sp),
    )
}

@Composable
fun ShradhanjaliTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}

val SectionLabel: TextStyle
    @Composable get() = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
