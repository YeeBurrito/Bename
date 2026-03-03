// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.locations

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Singleton object to manage the current geofenced locations
object LocationStatusManager {
    // Holds the list of current locations as a StateFlow for observation
    private val _currentLocations = MutableStateFlow<List<String>>(emptyList())
    val currentLocations: StateFlow<List<String>> = _currentLocations

    // Updates the current locations
    fun setCurrentLocations(locations: List<String>) {
        _currentLocations.value = locations
    }
}