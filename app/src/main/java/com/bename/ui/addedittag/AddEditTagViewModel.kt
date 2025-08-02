package com.bename.ui.addedittag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.LocationRepository
import com.bename.data.PersonRepository
import com.bename.data.TagRepository
import com.bename.data.local.database.Location
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
    private val tagRepository: TagRepository,
    val locationRepository: LocationRepository,
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _tag = MutableStateFlow<Tag?>(null)
    val tag: StateFlow<Tag?> = _tag.asStateFlow()

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

    fun addOrUpdateTag(tag: Tag) {
        viewModelScope.launch {
            if (tag.uid == 0) {
                tagRepository.add(tag)
            } else {
                tagRepository.update(tag)
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.add(location)
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
}

sealed interface AddEditTagUiState {
    object Loading : AddEditTagUiState
    data class Error(val throwable: Throwable) : AddEditTagUiState
    data class Success(val data: List<Tag>) : AddEditTagUiState
}