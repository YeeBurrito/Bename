// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.addeditperson

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.ui.util.AddEditPersonTopAppBar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.bename.data.local.database.Person
import androidx.compose.ui.unit.dp
import com.bename.R
import com.bename.ui.tag.TagUiState
import com.bename.ui.tag.TagViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEditPersonScreen(
    personId: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: AddEditPersonViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    // Load person data when entering the screen
    LaunchedEffect(personId) {
        viewModel.loadPerson(personId?.toLong())
    }

    // Collect tags from the database
    val tagUiState by tagViewModel.uiState.collectAsStateWithLifecycle()
    val tags = when (tagUiState) {
        is TagUiState.Success -> (tagUiState as TagUiState.Success).data
        else -> emptyList()
    }

    // Observe person state
    val personState by viewModel.person.collectAsStateWithLifecycle()

    // State for text fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State to track initial values for dirty check
    var initialFirstName by remember { mutableStateOf("") }
    var initialLastName by remember { mutableStateOf("") }
    var initialDescription by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    // Check if any field has changed
    val isDirty = firstName != initialFirstName ||
            lastName != initialLastName ||
            description != initialDescription

    // Intercept back action if there are unsaved changes
    BackHandler(enabled = isDirty) {
        showDialog = true
    }

    // Show confirmation dialog if user tries to go back with unsaved changes
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to go back?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onBack()
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("No") }
            }
        )
    }

    // Populate fields when editing an existing person
    LaunchedEffect(personState) {
        personState?.let {
            if (firstName.isBlank() && lastName.isBlank() && description.isBlank()) {
                firstName = it.firstName
                lastName = it.lastName.orEmpty()
                description = it.description.orEmpty()
                initialFirstName = it.firstName
                initialLastName = it.lastName.orEmpty()
                initialDescription = it.description.orEmpty()
            }
        }
    }
    // For add mode, set initial values to empty
    LaunchedEffect(personId) {
        if (personId == null) {
            initialFirstName = ""
            initialLastName = ""
            initialDescription = ""
        }
    }

    // State for tag search and dropdown
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Filter tags by search query
    val filteredTags = if (searchQuery.isBlank()) {
        tags
    } else {
        tags.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddEditPersonTopAppBar(
                title = if (personId == null) "Add Person" else "Edit Person",
                onBack = {
                    if (isDirty) {
                        showDialog = true
                    } else {
                        onBack()
                    }
                },
                actions = {
                    // Save or add person
                    Button(
                        onClick = {
                            val person = Person(
                                firstName = firstName,
                                lastName = lastName.ifBlank { null },
                                description = description.ifBlank { null },
                                hidden = personState?.hidden ?: false,
                                tagIds = personState?.tagIds ?: emptyList()
                            ).also {
                                // If editing, preserve uid
                                if (personId != null && personState != null) {
                                    it.uid = personState!!.uid
                                }
                            }
                            viewModel.addOrUpdatePerson(person)
                            onBack()
                        },
                        enabled = firstName.isNotBlank()
                    ) {
                        Text(if (personId == null) "Add" else "Save")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // First name input
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Last name input
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Description input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Tags section
            Text(
                text = "Tags",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tag search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search tags") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                val tagExists = tags.any { it.name.equals(searchQuery.trim(), ignoreCase = true) }
                var lastAddedTagName by remember { mutableStateOf<String?>(null) }
                // Add new tag button
                IconButton(
                    onClick = {
                        if (searchQuery.isNotBlank() && !tagExists) {
                            val newTag = com.bename.data.local.database.Tag(
                                name = searchQuery.trim()
                            )
                            tagViewModel.addTag(newTag)
                            lastAddedTagName = newTag.name // Store the last added tag name
                            // Optionally clear the search box after adding
                            searchQuery = ""
                        }
                    },
                    enabled = searchQuery.isNotBlank() && !tagExists
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add tag"
                    )
                }
                // Automatically select the tag after adding
                LaunchedEffect(tags, lastAddedTagName) {
                    if (lastAddedTagName != null) {
                        val newTag = tags.find { it.name.equals(lastAddedTagName, ignoreCase = true) }
                        if (newTag != null && personState != null && newTag.uid !in personState!!.tagIds) {
                            viewModel.addTagToPerson(newTag.uid)
                            lastAddedTagName = null // Reset after selection
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Get selected tag IDs
            val selectedTagIds = personState?.tagIds ?: emptyList<Long>()

            // Split tags into selected and unselected
            val selectedTags = filteredTags.filter { it.uid in selectedTagIds }
            val unselectedTags = filteredTags.filterNot { it.uid in selectedTagIds }

            // Display all tags in alphabetical order, selected tags in blue
            val allTagsSorted = (selectedTags + unselectedTags).sortedBy { it.name }
            allTagsSorted.forEach { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (tag.uid in selectedTagIds) {
                                viewModel.removeTagFromPerson(tag.uid)
                            } else {
                                viewModel.addTagToPerson(tag.uid)
                            }
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.shoppingmode_24pxfilled),
                        contentDescription = "Tag Icon",
                        tint = if (tag.uid in selectedTagIds) Color(0xFF1976D2) else Color.Gray, // Blue if selected
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (tag.uid in selectedTagIds) Color(0xFF1976D2) else Color.Unspecified // Blue if selected
                    )
                }
            }

            // Remove person button (only in edit mode)
            if (personId != null && personState != null) {
                Button(
                    onClick = {
                        viewModel.removePerson(personState!!)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Remove Person", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditPersonScreenPreview() {
    AddEditPersonScreen()
}