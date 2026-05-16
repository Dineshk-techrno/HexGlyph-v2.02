package com.hexglyph.a1.data.preferences

import com.hexglyph.a1.core.ui.theme.ThemeMode

data class UserPreferences(
    val themeMode:       ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor:    Boolean   = true,
    val animations:      Boolean   = true,
    val exportQuality:   Int       = 100,
    val performanceMode: Boolean   = false,
    val encryptDefault:  Boolean   = true
)
