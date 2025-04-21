package com.example.bename.ui

import com.example.bename.data.Person
import com.example.bename.data.BenameTab

data class BenameUiState(
    val people: List<Person>? = null,
    val currentTab: BenameTab = BenameTab.People
) {
}
