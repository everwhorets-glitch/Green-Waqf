package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BestariGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = BestariMintContainer,
    onPrimaryContainer = BestariOnMintContainer,
    secondary = BestariSageSecondary,
    onSecondary = Color.White,
    secondaryContainer = BestariSageContainer,
    onSecondaryContainer = BestariOnSurface,
    tertiary = BestariGreenLight,
    onTertiary = Color.White,
    background = BestariBackground,
    onBackground = BestariOnSurface,
    surface = BestariSurface,
    onSurface = BestariOnSurface,
    surfaceVariant = BestariSurfaceVariant,
    onSurfaceVariant = BestariOnSurfaceVariant,
    outline = BestariOutline,
    outlineVariant = BestariOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF00391A),
    primaryContainer = BestariGreenDark,
    onPrimaryContainer = Color(0xFFC7F8D3),
    secondary = Color(0xFFA1D1B0),
    onSecondary = Color(0xFF0F3821),
    secondaryContainer = Color(0xFF264E35),
    onSecondaryContainer = Color(0xFFBCE8CB),
    background = Color(0xFF101511),
    onBackground = Color(0xFFE2E7E1),
    surface = Color(0xFF151B17),
    onSurface = Color(0xFFE2E7E1),
    surfaceVariant = Color(0xFF222B24),
    onSurfaceVariant = Color(0xFFC2CBC2),
    outline = Color(0xFF414D43),
    outlineVariant = Color(0xFF2D3730)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Bank Bestari signature green & white theme intentional
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BestariTypography,
        content = content
    )
}
