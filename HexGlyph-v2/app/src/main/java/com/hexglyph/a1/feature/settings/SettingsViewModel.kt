package com.hexglyph.a1.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexglyph.a1.core.ui.theme.ThemeMode
import com.hexglyph.a1.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // kotlinx.coroutines combine() accepts max 5 flows in the typed overload.
            // Split into two combine() calls and merge the results.
            val firstFive = combine(
                dataStore.themeMode,
                dataStore.dynamicColor,
                dataStore.animationsEnabled,
                dataStore.exportQuality,
                dataStore.performanceMode
            ) { theme, dynamic, anim, quality, perf ->
                SettingsUiState(
                    themeMode       = theme,
                    dynamicColor    = dynamic,
                    animations      = anim,
                    exportQuality   = quality,
                    performanceMode = perf
                )
            }

            combine(firstFive, dataStore.encryptByDefault) { partial, encrypt ->
                partial.copy(encryptDefault = encrypt)
            }.collect { _state.value = it }
        }
    }

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.ThemeModeChanged      -> dataStore.setThemeMode(event.mode)
                is SettingsEvent.DynamicColorChanged   -> dataStore.setDynamicColor(event.enabled)
                is SettingsEvent.AnimationsChanged     -> dataStore.setAnimations(event.enabled)
                is SettingsEvent.ExportQualityChanged  -> dataStore.setExportQuality(event.quality)
                is SettingsEvent.PerformanceModeChanged -> dataStore.setPerformanceMode(event.enabled)
                is SettingsEvent.EncryptDefaultChanged -> dataStore.setEncryptByDefault(event.enabled)
            }
        }
    }
}
