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

@HiltViewModel
class AddEditTagViewModel @Inject constructor(
    val tagRepository: TagRepository,
    val locationRepository: LocationRepository,
    private val personRepository: PersonRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _tag = MutableStateFlow<Tag?>(null)
    val tag: StateFlow<Tag?> = _tag.asStateFlow()
    private val _people = MutableStateFlow<List<Person>>(emptyList())
    val people: StateFlow<List<Person>> = _people.asStateFlow()

    val premiumEnabled = settingsDataStore.premiumEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        viewModelScope.launch {
            _people.value = personRepository.getAllPersons()
        }
    }

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

    fun togglePersonForTag(person: Person) {
        val currentTag = _tag.value ?: return
        val updatedPersonIds = if (person.uid in currentTag.personIds) {
            currentTag.personIds - person.uid
        } else {
            currentTag.personIds + person.uid
        }
        val updatedTag = currentTag.copy(personIds = updatedPersonIds)
        _tag.value = updatedTag

        // Update tag in DB
        viewModelScope.launch {
            tagRepository.update(updatedTag)
            // Also update the person to reflect the tag association
            val updatedPerson = if (person.uid in currentTag.personIds) {
                person.copy(tagIds = person.tagIds - currentTag.uid)
            } else {
                person.copy(tagIds = person.tagIds + currentTag.uid)
            }
            personRepository.update(updatedPerson)
            // Optionally refresh people list
            _people.value = personRepository.getAllPersons()
        }
    }

    fun addOrUpdateTag(tag: Tag) {
        viewModelScope.launch {
            if (tag.uid == 0) {
                // Only add if it's a new tag
                tagRepository.add(tag)
            } else {
                // Make sure to update the tag with the correct uid
                tagRepository.update(tag)
            }
            // Refresh the tag from DB to update state
            val tags = tagRepository.getAllTags()
            _tag.value = tags.find { it.uid == tag.uid }
        }
    }

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

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            // Remove location from database
            locationRepository.remove(location)
            // Remove location from all tags that reference it
            val tags = tagRepository.getAllTags()
            tags.forEach { tag ->
                if (location.name in tag.locations) {
                    val updatedTag = tag.copy(locations = tag.locations - location.name)
                    tagRepository.update(updatedTag)
                }
            }
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            // Remove tag from all persons
            val persons = personRepository.persons.first()
            persons.forEach { person ->
                if (tag.uid in person.tagIds) {
                    val updatedPerson = person.copy(tagIds = person.tagIds - tag.uid)
                    personRepository.update(updatedPerson)
                }
            }
            // Delete the tag itself
            tagRepository.delete(tag)
        }
    }

    fun addLocationToTag(name: String, lat: Double, lng: Double) {
        val currentTag = _tag.value ?: return
        val newLocation = Location(name = name, latitude = lat, longitude = lng)
        viewModelScope.launch {
            locationRepository.add(newLocation)
            // Preserve the uid when copying
            _tag.value = currentTag.copy(locations = currentTag.locations + name).apply { uid = currentTag.uid }
        }
    }

    fun removeLocationFromTag(locationName: String) {
        val currentTag = _tag.value ?: return
        _tag.value = currentTag.copy(locations = currentTag.locations - locationName).apply { uid = currentTag.uid }
    }

    // In AddEditTagViewModel.kt
    fun toggleTagForPerson(tag: Tag, person: Person) {
        viewModelScope.launch {
            if (person.tagIds.contains(tag.uid)) {
                // Remove the tag from the person
                person.tagIds = person.tagIds - tag.uid
            } else {
                // Add the tag to the person
                person.tagIds = person.tagIds + tag.uid
            }
            personRepository.update(person)
        }
    }
}

sealed interface AddEditTagUiState {
    object Loading : AddEditTagUiState
    data class Error(val throwable: Throwable) : AddEditTagUiState
    data class Success(val data: List<Tag>) : AddEditTagUiState
}