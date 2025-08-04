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

    val personState by viewModel.person.collectAsStateWithLifecycle()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Populate fields when personState changes (for edit)
    LaunchedEffect(personState) {
        personState?.let {
            if (firstName.isBlank() && lastName.isBlank() && description.isBlank()) {
                firstName = it.firstName
                lastName = it.lastName.orEmpty()
                description = it.description.orEmpty()
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

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
                onBack = onBack,
                actions = {
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
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Tags
            Text(
                text = "Tags",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Search bar (no dropdown)
//            OutlinedTextField(
//                value = searchQuery,
//                onValueChange = { searchQuery = it },
//                label = { Text("Search tags") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search tags") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                val tagExists = tags.any { it.name.equals(searchQuery.trim(), ignoreCase = true) }
                IconButton(
                    onClick = {
                        if (searchQuery.isNotBlank() && !tagExists) {
                            val newTag = com.bename.data.local.database.Tag(
                                name = searchQuery.trim()
                            )
                            tagViewModel.addTag(newTag)
                            // Optionally clear the search box after adding
                            // searchQuery = ""
                        }
                    },
                    enabled = searchQuery.isNotBlank() && !tagExists
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add tag"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Get selected tag IDs
            val selectedTagIds = personState?.tagIds ?: emptyList<Long>()

            // Split tags
            val selectedTags = filteredTags.filter { it.uid in selectedTagIds }
            val unselectedTags = filteredTags.filterNot { it.uid in selectedTagIds }

            // Display selected tags at the top in blue
            selectedTags.forEach { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.removeTagFromPerson(tag.uid) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.shoppingmode_24pxfilled),
                        contentDescription = "Tag Icon",
                        tint = Color(0xFF1976D2), // Blue color
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2) // Blue color
                    )
                }
            }

            // Display unselected tags below in default color
            unselectedTags.forEach { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.addTagToPerson(tag.uid) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.shoppingmode_24pxfilled),
                        contentDescription = "Tag Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

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