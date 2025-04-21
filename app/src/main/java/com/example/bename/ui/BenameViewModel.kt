package com.example.bename.ui

import androidx.lifecycle.ViewModel
import com.example.bename.data.BenameTab
import com.example.bename.data.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BenameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BenameUiState())
    val uiState: StateFlow<BenameUiState> = _uiState

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        val people: List<Person>? = null
        _uiState.value =
            BenameUiState()
    }

    fun switchTab(tags: BenameTab) {
        _uiState.value = _uiState.value.copy(currentTab = tags)
    }
}