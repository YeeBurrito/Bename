package com.example.bename.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AddPersonUiState(
    val firstName: String = "",
    val lastName: String = "",
    val description: String = "",
)

class AddPersonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddPersonUiState())
    val uiState: StateFlow<AddPersonUiState> = _uiState

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
        // TODO: Save the person
        // For now, just display the person to the screen
        println("Person saved: ${uiState.value}")
    }
}