package com.bename.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val DARK_MODE = intPreferencesKey("dark_mode") // 0=system, 1=light, 2=dark
    val PREMIUM_ENABLED = booleanPreferencesKey("premium_enabled")
}

class SettingsDataStore(private val context: Context) {
    val notificationsEnabledFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { it[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true }

    val darkModeFlow: Flow<Int> =
        context.settingsDataStore.data.map { it[SettingsKeys.DARK_MODE] ?: 0 }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setDarkMode(mode: Int) {
        context.settingsDataStore.edit { it[SettingsKeys.DARK_MODE] = mode }
    }

    val premiumEnabledFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { it[SettingsKeys.PREMIUM_ENABLED] ?: false }

    suspend fun setPremiumEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.PREMIUM_ENABLED] = enabled }
    }
}