// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to get the DataStore instance for settings
val Context.settingsDataStore by preferencesDataStore(name = "settings")

// Keys used for storing preferences in DataStore
object SettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val DARK_MODE = intPreferencesKey("dark_mode") // 0=system, 1=light, 2=dark
    val PREMIUM_ENABLED = booleanPreferencesKey("premium_enabled")
    val LOCATION_POPUP_SHOWN = booleanPreferencesKey("location_popup_shown")
}

// Class to manage app settings using DataStore
class SettingsDataStore(private val context: Context) {
    // Flow to observe notifications enabled setting
    val notificationsEnabledFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { it[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true }

    // Flow to observe dark mode setting
    val darkModeFlow: Flow<Int> =
        context.settingsDataStore.data.map { it[SettingsKeys.DARK_MODE] ?: 0 }

    // Update notifications enabled setting
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    // Update dark mode setting
    suspend fun setDarkMode(mode: Int) {
        context.settingsDataStore.edit { it[SettingsKeys.DARK_MODE] = mode }
    }

    // Flow to observe premium enabled setting
    val premiumEnabledFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { it[SettingsKeys.PREMIUM_ENABLED] ?: false }

    // Update premium enabled setting
    suspend fun setPremiumEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.PREMIUM_ENABLED] = enabled }
    }

    // Flow to observe if the location popup has been shown
    val locationPopupShownFlow: Flow<Boolean?> =
        context.settingsDataStore.data.map { it[SettingsKeys.LOCATION_POPUP_SHOWN] }

    // Update location popup shown setting
    suspend fun setLocationPopupShown(shown: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.LOCATION_POPUP_SHOWN] = shown }
    }
}