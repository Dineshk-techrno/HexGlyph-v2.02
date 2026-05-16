package com.hexglyph.a1.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hexglyph.a1.core.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("hexglyph_prefs")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE       = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR    = booleanPreferencesKey("dynamic_color")
        val ANIMATIONS       = booleanPreferencesKey("animations")
        val EXPORT_QUALITY   = intPreferencesKey("export_quality")
        val PERFORMANCE_MODE = booleanPreferencesKey("performance_mode")
        val ENCRYPT_DEFAULT  = booleanPreferencesKey("encrypt_default")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLOR] ?: true
    }

    val animationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ANIMATIONS] ?: true
    }

    val exportQuality: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.EXPORT_QUALITY] ?: 100
    }

    val performanceMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.PERFORMANCE_MODE] ?: false
    }

    val encryptByDefault: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ENCRYPT_DEFAULT] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setAnimations(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ANIMATIONS] = enabled }
    }

    suspend fun setExportQuality(quality: Int) {
        context.dataStore.edit { it[Keys.EXPORT_QUALITY] = quality.coerceIn(1, 100) }
    }

    suspend fun setPerformanceMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PERFORMANCE_MODE] = enabled }
    }

    suspend fun setEncryptByDefault(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENCRYPT_DEFAULT] = enabled }
    }
}
