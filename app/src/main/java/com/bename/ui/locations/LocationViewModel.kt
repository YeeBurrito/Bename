package com.bename.ui.locations

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {
    private val _currentLocations = MutableStateFlow<List<String>>(emptyList())
    val currentLocations: StateFlow<List<String>> = _currentLocations

    fun setCurrentLocations(locations: List<String>) {
        _currentLocations.value = locations
    }
}