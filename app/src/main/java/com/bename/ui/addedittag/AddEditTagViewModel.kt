package com.bename.ui.addedittag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.TagRepository
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
    // Pass tagId as a parameter (nullable)
) : ViewModel() {

    // Backing property for the tag being edited or created
    private val _tag = MutableStateFlow<Tag?>(null)
    val tag: StateFlow<Tag?> = _tag.asStateFlow()

    val uiState: StateFlow<AddEditTagUiState> = tagRepository
        .tags.map<List<Tag>, AddEditTagUiState>(::Success)
        .catch { emit(AddEditTagUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEditTagUiState.Loading)

    // Call this from the UI with a nullable tagId
    fun loadTag(tagId: Long?) {
        if (tagId == null) {
            // New tag
            _tag.value = Tag(
                name = "",
                locations = emptyList(),
                personIds = emptyList(),
            )
        } else {
            // Editing existing tag
            viewModelScope.launch {
                val tagList = tagRepository.tags.first()
                _tag.value = tagList.find { it.uid.toLong() == tagId }
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
}

sealed interface AddEditTagUiState {
    object Loading : AddEditTagUiState
    data class Error(val throwable: Throwable) : AddEditTagUiState
    data class Success(val data: List<Tag>) : AddEditTagUiState
}