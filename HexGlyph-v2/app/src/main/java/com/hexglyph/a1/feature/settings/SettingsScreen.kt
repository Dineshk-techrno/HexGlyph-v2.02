package com.hexglyph.a1.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexglyph.a1.core.ui.theme.Dimens
import com.hexglyph.a1.core.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack:    () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(Dimens.paddingLg)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingMd)
            ) {
                Text("Appearance", style = MaterialTheme.typography.titleLarge)

                // Theme mode selector
                Text("Theme", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSm)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = state.themeMode == mode,
                            onClick  = { viewModel.onEvent(SettingsEvent.ThemeModeChanged(mode)) },
                            label    = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                SettingsSwitch(
                    label   = "Dynamic colours",
                    checked = state.dynamicColor,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.DynamicColorChanged(it))
                    }
                )

                SettingsSwitch(
                    label   = "Animations",
                    checked = state.animations,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.AnimationsChanged(it))
                    }
                )

                Text("Security", style = MaterialTheme.typography.titleLarge)

                SettingsSwitch(
                    label   = "Encrypt by default",
                    checked = state.encryptDefault,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.EncryptDefaultChanged(it))
                    }
                )

                Text("Performance", style = MaterialTheme.typography.titleLarge)

                SettingsSwitch(
                    label   = "Performance mode (faster, less safe memory)",
                    checked = state.performanceMode,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.PerformanceModeChanged(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
