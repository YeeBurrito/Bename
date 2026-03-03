// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.locations

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel to manage the list of current geofenced locations
class LocationViewModel : ViewModel() {
    // Holds the current locations as a StateFlow for observation
    private val _currentLocations = MutableStateFlow<List<String>>(emptyList())
    val currentLocations: StateFlow<List<String>> = _currentLocations

    // Updates the current locations
    fun setCurrentLocations(locations: List<String>) {
        _currentLocations.value = locations
    }
}