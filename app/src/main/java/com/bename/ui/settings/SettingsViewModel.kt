// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel for managing app settings state
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // StateFlow for notification toggle
    val notificationsEnabled: StateFlow<Boolean> =
        settingsDataStore.notificationsEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)

    // StateFlow for dark mode selection (0 = system, 1 = light, 2 = dark)
    val darkMode: StateFlow<Int> =
        settingsDataStore.darkModeFlow.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Update notification setting
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotificationsEnabled(enabled) }
    }

    // Update dark mode setting
    fun setDarkMode(mode: Int) {
        viewModelScope.launch { settingsDataStore.setDarkMode(mode) }
    }

    // StateFlow for premium feature status
    val premiumEnabled: StateFlow<Boolean> =
        settingsDataStore.premiumEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Update premium feature status
    fun setPremiumEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setPremiumEnabled(enabled) }
    }

    // StateFlow for whether the location popup has been shown
    val locationPopupShown: StateFlow<Boolean?> =
        settingsDataStore.locationPopupShownFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Update location popup shown state
    fun setLocationPopupShown(shown: Boolean) {
        viewModelScope.launch { settingsDataStore.setLocationPopupShown(shown) }
    }
}