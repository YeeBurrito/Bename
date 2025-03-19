package com.example.bename.ui

import com.example.bename.data.local.Person
import com.example.bename.data.local.BenameTab

data class BenameUiState(
    val people: List<Person>? = null,
    val currentTab: BenameTab = BenameTab.People
) {
}
