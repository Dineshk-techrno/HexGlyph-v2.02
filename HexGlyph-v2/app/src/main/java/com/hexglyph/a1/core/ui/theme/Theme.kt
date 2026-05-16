package com.hexglyph.a1.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = HexPrimary,
    onPrimary        = HexOnPrimary,
    secondary        = HexSecondary,
    onSecondary      = HexOnSecondary,
    tertiary         = HexTertiary,
    background       = HexBackground,
    surface          = HexSurface,
    surfaceVariant   = HexSurfaceVar,
    onBackground     = HexOnBackground,
    onSurface        = HexOnSurface,
    error            = HexError
)

private val AmoledColorScheme = darkColorScheme(
    primary          = HexPrimary,
    onPrimary        = HexOnPrimary,
    secondary        = HexSecondary,
    onSecondary      = HexOnSecondary,
    tertiary         = HexTertiary,
    background       = AmoledBlack,
    surface          = AmoledSurface,
    surfaceVariant   = AmoledSurface,
    onBackground     = HexOnBackground,
    onSurface        = HexOnSurface,
    error            = HexError
)

private val LightColorScheme = lightColorScheme(
    primary          = HexTertiary,
    onPrimary        = HexLightSurface,
    secondary        = HexSecondary,
    onSecondary      = HexOnSecondary,
    background       = HexLightBackground,
    surface          = HexLightSurface,
    onBackground     = HexLightOnBackground,
    onSurface        = HexLightOnBackground,
    error            = HexError
)

@Composable
fun HexGlyphTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val colorScheme = when (themeMode) {
        ThemeMode.AMOLED -> AmoledColorScheme

        ThemeMode.LIGHT  -> if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(context)
        } else LightColorScheme

        ThemeMode.DARK   -> if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else DarkColorScheme

        ThemeMode.SYSTEM -> when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                if (isSystemDark) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            isSystemDark -> DarkColorScheme
            else         -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = HexGlyphTypography,
        shapes      = HexGlyphShapes,
        content     = content
    )
}
