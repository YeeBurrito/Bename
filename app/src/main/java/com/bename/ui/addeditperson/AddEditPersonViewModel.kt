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

@HiltViewModel
class AddEditPersonViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    // Pass personId as a parameter (nullable)
) : ViewModel() {

    // Backing property for the person being edited or created
    private val _person = MutableStateFlow<Person?>(null)
    val person: StateFlow<Person?> = _person.asStateFlow()

    val uiState: StateFlow<AddEditPersonUiState> = personRepository
        .persons.map<List<Person>, AddEditPersonUiState>(::Success)
        .catch { emit(AddEditPersonUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditPersonUiState.Loading)

    // Call this from the UI with a nullable personId
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

    fun addOrUpdatePerson(person: Person) {
        viewModelScope.launch {
            if (person.uid == 0) {
                personRepository.add(person)
            } else {
                personRepository.update(person)
            }
        }
    }

    fun addTagToPerson(tagId: Int) {
        val current = _person.value
        if (current != null && tagId !in current.tagIds) {
            _person.value = current.copy(tagIds = current.tagIds + tagId).also { it.uid = current.uid }
        }
    }

    fun removeTagFromPerson(tagId: Int) {
        val current = _person.value
        if (current != null && tagId in current.tagIds) {
            _person.value = current.copy(tagIds = current.tagIds - tagId).also { it.uid = current.uid }
        }
    }

    fun removePerson(person: Person) {
        viewModelScope.launch {
            personRepository.remove(person)
        }
    }
}

sealed interface AddEditPersonUiState {
    object Loading : AddEditPersonUiState
    data class Error(val throwable: Throwable) : AddEditPersonUiState
    data class Success(val data: List<Person>) : AddEditPersonUiState
}