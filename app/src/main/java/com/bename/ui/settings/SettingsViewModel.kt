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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    val notificationsEnabled: StateFlow<Boolean> =
        settingsDataStore.notificationsEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)

    val darkMode: StateFlow<Int> =
        settingsDataStore.darkModeFlow.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotificationsEnabled(enabled) }
    }

    fun setDarkMode(mode: Int) {
        viewModelScope.launch { settingsDataStore.setDarkMode(mode) }
    }

    val premiumEnabled: StateFlow<Boolean> =
        settingsDataStore.premiumEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setPremiumEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setPremiumEnabled(enabled) }
    }

    val locationPopupShown: StateFlow<Boolean?> =
        settingsDataStore.locationPopupShownFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setLocationPopupShown(shown: Boolean) {
        viewModelScope.launch { settingsDataStore.setLocationPopupShown(shown) }
    }
}