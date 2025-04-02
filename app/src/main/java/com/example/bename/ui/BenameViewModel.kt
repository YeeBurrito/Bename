package com.example.bename.ui

import androidx.lifecycle.ViewModel
import com.example.bename.data.local.BenameTab
import com.example.bename.data.local.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

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