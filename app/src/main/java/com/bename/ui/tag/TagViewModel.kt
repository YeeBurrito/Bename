// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.tag

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.TagRepository
import com.bename.data.local.database.Tag
import com.bename.ui.tag.TagUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel for managing tags and tag-related UI state
@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    // StateFlow representing the UI state for tags (loading, error, or success)
    val uiState: StateFlow<TagUiState> = tagRepository
        .tags.map<List<Tag>, TagUiState>(::Success)
        .catch {
            Log.e("TagViewModel", "Error fetching tags", it)
            Error(it)
            //emit(Error(it)) // Optionally emit error state
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TagUiState.Loading)

    // Adds a new tag to the repository
    fun addTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.add(tag)
        }
    }

    // Returns a StateFlow of tags filtered by the search query
    fun searchTags(query: String): StateFlow<List<Tag>> {
        return tagRepository.searchTags(query)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

// Represents the UI state for tags
sealed interface TagUiState {
    object Loading : TagUiState
    data class Error(val throwable: Throwable) : TagUiState
    data class Success(val data: List<Tag>) : TagUiState
}