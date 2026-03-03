// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.addeditperson

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
import com.bename.ui.addeditperson.AddEditPersonUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.*

// ViewModel for adding or editing a person
@HiltViewModel
class AddEditPersonViewModel @Inject constructor(
    private val personRepository: PersonRepository,
) : ViewModel() {

    // Holds the current person being edited or created
    private val _person = MutableStateFlow<Person?>(null)
    val person: StateFlow<Person?> = _person.asStateFlow()

    // UI state for observing the list of persons
    val uiState: StateFlow<AddEditPersonUiState> = personRepository
        .persons.map<List<Person>, AddEditPersonUiState>(::Success)
        .catch { emit(AddEditPersonUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditPersonUiState.Loading)

    // Loads a person by ID or creates a new one if ID is null
    fun loadPerson(personId: Long?) {
        if (personId == null) {
            // New person
            _person.value = Person(
                firstName = "",
                lastName = null,
                description = null,
                tagIds = emptyList(),
            )
        } else {
            // Editing existing person
            viewModelScope.launch {
                val personList = personRepository.persons.first()
                _person.value = personList.find { it.uid.toLong() == personId }
            }
        }
    }

    // Adds a new person or updates an existing one
    fun addOrUpdatePerson(person: Person) {
        viewModelScope.launch {
            if (person.uid == 0) {
                personRepository.add(person)
            } else {
                personRepository.update(person)
            }
        }
    }

    // Adds a tag to the current person
    fun addTagToPerson(tagId: Int) {
        val current = _person.value
        if (current != null && tagId !in current.tagIds) {
            _person.value = current.copy(tagIds = current.tagIds + tagId).also { it.uid = current.uid }
        }
    }

    // Removes a tag from the current person
    fun removeTagFromPerson(tagId: Int) {
        val current = _person.value
        if (current != null && tagId in current.tagIds) {
            _person.value = current.copy(tagIds = current.tagIds - tagId).also { it.uid = current.uid }
        }
    }

    // Removes a person from the repository
    fun removePerson(person: Person) {
        viewModelScope.launch {
            personRepository.remove(person)
        }
    }
}

// UI state for Add/Edit Person screen
sealed interface AddEditPersonUiState {
    object Loading : AddEditPersonUiState
    data class Error(val throwable: Throwable) : AddEditPersonUiState
    data class Success(val data: List<Person>) : AddEditPersonUiState
}