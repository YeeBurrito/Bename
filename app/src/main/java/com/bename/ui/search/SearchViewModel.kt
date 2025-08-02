package com.bename.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bename.data.PersonRepository
import com.bename.data.local.database.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.bename.data.TagRepository
import com.bename.data.local.database.Tag

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val people: StateFlow<List<Person>> = combine(
        personRepository.persons,
        _query
    ) { people, query ->
        if (query.isBlank()) people
        else people.filter {
            it.firstName.contains(query, ignoreCase = true) ||
                    (it.lastName?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tags: StateFlow<List<Tag>> = tagRepository
        .tags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }
}