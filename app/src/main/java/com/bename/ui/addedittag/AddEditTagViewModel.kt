// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.addedittag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.LocationRepository
import com.bename.data.PersonRepository
import com.bename.data.TagRepository
import com.bename.data.local.SettingsDataStore
import com.bename.data.local.database.Location
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag
import com.bename.ui.addedittag.AddEditTagUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel for adding or editing a tag
@HiltViewModel
class AddEditTagViewModel @Inject constructor(
    val tagRepository: TagRepository,
    val locationRepository: LocationRepository,
    private val personRepository: PersonRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Holds the current tag being edited or created
    private val _tag = MutableStateFlow<Tag?>(null)
    val tag: StateFlow<Tag?> = _tag.asStateFlow()

    // List of all people for assigning tags
    private val _people = MutableStateFlow<List<Person>>(emptyList())
    val people: StateFlow<List<Person>> = _people.asStateFlow()

    // State for premium feature
    val premiumEnabled = settingsDataStore.premiumEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        // Load all people when ViewModel is created
        viewModelScope.launch {
            _people.value = personRepository.getAllPersons()
        }
    }

    // Loads a tag by ID or clears if null (add mode)
    fun loadTag(tagId: Long?) {
        if (tagId == null) {
            _tag.value = null
            return
        }
        viewModelScope.launch {
            tagRepository.tags.collect { tags ->
                _tag.value = tags.find { it.uid == tagId.toInt() }
            }
        }
    }

    // Toggle a person for the current tag (assign or remove)
    fun togglePersonForTag(person: Person) {
        val currentTag = _tag.value ?: return
        val updatedPersonIds = if (person.uid in currentTag.personIds) {
            currentTag.personIds - person.uid
        } else {
            currentTag.personIds + person.uid
        }
        val updatedTag = currentTag.copy(personIds = updatedPersonIds)
        _tag.value = updatedTag

        // Update tag and person in the database
        viewModelScope.launch {
            tagRepository.update(updatedTag)
            val updatedPerson = if (person.uid in currentTag.personIds) {
                person.copy(tagIds = person.tagIds - currentTag.uid)
            } else {
                person.copy(tagIds = person.tagIds + currentTag.uid)
            }
            personRepository.update(updatedPerson)
            _people.value = personRepository.getAllPersons()
        }
    }

    // Add a new tag or update an existing one
    fun addOrUpdateTag(tag: Tag) {
        viewModelScope.launch {
            if (tag.uid == 0) {
                tagRepository.add(tag)
            } else {
                tagRepository.update(tag)
            }
            // Refresh the tag from DB to update state
            val tags = tagRepository.getAllTags()
            _tag.value = tags.find { it.uid == tag.uid }
        }
    }

    // Add a new location if not duplicate
    fun addLocation(location: Location) {
        viewModelScope.launch {
            val existingLocations = locationRepository.locations.first()
            val duplicate = existingLocations.any {
                it.name.equals(location.name, ignoreCase = true) ||
                        (it.latitude == location.latitude && it.longitude == location.longitude)
            }
            if (!duplicate) {
                locationRepository.add(location)
            }
            // Optionally, handle UI feedback for duplicates here
        }
    }

    // Delete a location and remove it from all tags
    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.remove(location)
            val tags = tagRepository.getAllTags()
            tags.forEach { tag ->
                if (location.name in tag.locations) {
                    val updatedTag = tag.copy(locations = tag.locations - location.name)
                    tagRepository.update(updatedTag)
                }
            }
        }
    }

    // Delete a tag and remove it from all persons
    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            val persons = personRepository.persons.first()
            persons.forEach { person ->
                if (tag.uid in person.tagIds) {
                    val updatedPerson = person.copy(tagIds = person.tagIds - tag.uid)
                    personRepository.update(updatedPerson)
                }
            }
            tagRepository.delete(tag)
        }
    }

    // Add a location to the current tag
    fun addLocationToTag(name: String, lat: Double, lng: Double) {
        val currentTag = _tag.value ?: return
        val newLocation = Location(name = name, latitude = lat, longitude = lng)
        viewModelScope.launch {
            locationRepository.add(newLocation)
            _tag.value = currentTag.copy(locations = currentTag.locations + name).apply { uid = currentTag.uid }
        }
    }

    // Remove a location from the current tag
    fun removeLocationFromTag(locationName: String) {
        val currentTag = _tag.value ?: return
        _tag.value = currentTag.copy(locations = currentTag.locations - locationName).apply { uid = currentTag.uid }
    }

    // Toggle a tag for a person (assign or remove)
    fun toggleTagForPerson(tag: Tag, person: Person) {
        viewModelScope.launch {
            if (person.tagIds.contains(tag.uid)) {
                person.tagIds = person.tagIds - tag.uid
            } else {
                person.tagIds = person.tagIds + tag.uid
            }
            personRepository.update(person)
        }
    }
}

// UI state for Add/Edit Tag screen
sealed interface AddEditTagUiState {
    object Loading : AddEditTagUiState
    data class Error(val throwable: Throwable) : AddEditTagUiState
    data class Success(val data: List<Tag>) : AddEditTagUiState
}