// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.person

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.bename.data.PersonRepository
import com.bename.data.local.database.Person
import com.bename.data.local.database.PersonDao
import com.bename.ui.person.PersonUiState.Error
import com.bename.ui.person.PersonUiState.Loading
import com.bename.ui.person.PersonUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ViewModel for managing people data and UI state
@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    // Exposes the UI state as a StateFlow for observation in the UI
    val uiState: StateFlow<PersonUiState> = personRepository
        .persons.map<List<Person>, PersonUiState>(::Success)
        .catch {
            Log.e("PersonViewModel", "Error fetching persons", it)
            Error(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    // Adds a new person to the repository
    fun addPerson(person: Person) {
        viewModelScope.launch {
            personRepository.add(person)
        }
    }

    // Removes all people from the repository
    fun removeAllPeople() {
        viewModelScope.launch {
            personRepository.clearAll() // Implement this in your repository/DAO
        }
    }
}

// Represents the UI state for the people screen
sealed interface PersonUiState {
    object Loading : PersonUiState
    data class Error(val throwable: Throwable) : PersonUiState
    data class Success(val data: List<Person>) : PersonUiState
}