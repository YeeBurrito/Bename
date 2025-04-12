package com.example.bename.addeditperson

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AddEditPersonUiState(
    val firstName: String = "",
    val lastName: String = "",
    val description: String = "",
)

class AddEditPersonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditPersonUiState())
    val uiState: StateFlow<AddEditPersonUiState> = _uiState

    fun onFirstNameChange(firstName: String) {
        _uiState.value = _uiState.value.copy(firstName = firstName)
    }

    fun onLastNameChange(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun savePerson() {

    }
}