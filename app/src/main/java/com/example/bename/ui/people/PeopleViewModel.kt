package com.example.bename.ui.people

import androidx.lifecycle.ViewModel
import com.example.bename.data.Person
import java.util.Date

data class PeopleUiState(
    val items: List<Person> = emptyList(),
    val isLoading: Boolean = false,
)

class PeopleViewModel : ViewModel() {
    private var _uiState = PeopleUiState()
    val uiState: PeopleUiState
        get() = _uiState

    fun loadPeople() {
        // Logic to load people
        _uiState = _uiState.copy(isLoading = true)
        // Simulate loading data
        _uiState = _uiState.copy(
            items = listOf(
                Person(id = 1, firstName = "John", lastName = "Doe", created = Date(), updated = Date()),
                Person(id = 2, firstName = "Jane", lastName = "Smith", created = Date(), updated = Date()),
            ),
            isLoading = false
        )
    }
}