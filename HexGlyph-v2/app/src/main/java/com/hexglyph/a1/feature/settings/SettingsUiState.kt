package com.hexglyph.a1.feature.settings

import com.hexglyph.a1.core.ui.theme.ThemeMode

data class SettingsUiState(
    val themeMode:       ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor:    Boolean   = true,
    val animations:      Boolean   = true,
    val exportQuality:   Int       = 100,
    val performanceMode: Boolean   = false,
    val encryptDefault:  Boolean   = true
)

sealed class SettingsEvent {
    data class ThemeModeChanged(val mode: ThemeMode)  : SettingsEvent()
    data class DynamicColorChanged(val enabled: Boolean) : SettingsEvent()
    data class AnimationsChanged(val enabled: Boolean)   : SettingsEvent()
    data class ExportQualityChanged(val quality: Int)    : SettingsEvent()
    data class PerformanceModeChanged(val enabled: Boolean) : SettingsEvent()
    data class EncryptDefaultChanged(val enabled: Boolean)  : SettingsEvent()
}
