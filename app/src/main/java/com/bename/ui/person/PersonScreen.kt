// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.person

import androidx.compose.foundation.clickable
import com.bename.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.R
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag

// Main screen for displaying and managing people
@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    onPersonClick: (Int) -> Unit = {},
    onAddPerson: () -> Unit = {},
    tags: List<Tag> = emptyList(),
    viewModel: PersonViewModel = hiltViewModel()
) {
    // Observe UI state from the ViewModel
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        // Header row with title and add button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            Text(
                text = "People",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddPerson) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Person")
            }
        }
        // Show content based on UI state
        when (items) {
            is PersonUiState.Success -> {
                PersonScreen(
                    items = (items as PersonUiState.Success).data,
                    onSave = viewModel::addPerson,
                    onPersonClick = onPersonClick,
                    tags = tags,
                    modifier = Modifier.weight(1f)
                )
            }
            is PersonUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading...")
                }
            }
            is PersonUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading persons")
                }
            }
        }
    }
}

// Displays a list of people with search functionality
@Composable
internal fun PersonScreen(
    items: List<Person>,
    onSave: (person: Person) -> Unit,
    onPersonClick: (Int) -> Unit = {},
    tags: List<Tag> = emptyList(),
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter people by name (first or last)
    val filteredPeople = items.filter { person ->
        searchQuery.isBlank() ||
                person.firstName.contains(searchQuery, ignoreCase = true) ||
                (person.lastName?.contains(searchQuery, ignoreCase = true) == true)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )
        // List of people
        LazyColumn {
            items(filteredPeople) { person ->
                val personTags = tags.filter { it.uid in person.tagIds }
                PersonItem(
                    person = person,
                    tags = personTags,
                    onPersonClick = { onPersonClick(person.uid) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider()
            }
        }
    }
}

// Displays a single person row with their tags
@Composable
private fun PersonItem(
    person: Person,
    onPersonClick: (Person) -> Unit,
    tags: List<Tag> = emptyList(),
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable {
                onPersonClick(person)
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${person.firstName} ${person.lastName.orEmpty()}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Tags: " + tags.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = stringResource(id = R.string.cd_arrow_forward)
        )
    }
}

// Previews for Compose UI

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        PersonScreen(
            items = listOf(
                Person(
                    firstName = "John",
                    lastName = "Doe",
                    description = "A sample person with a longer description to test the layout."
                ),
                Person(
                    firstName = "Jane",
                    lastName = "Smith",
                    description = "Another sample person with a different description."
                )
            ),
            onSave = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        PersonScreen(
            items = listOf(
                Person(
                    firstName = "Alice",
                    lastName = "Johnson",
                    description = "A sample person with a longer description to test the layout."
                ),
                Person(
                    firstName = "Bob",
                    lastName = "Brown",
                    description = "Another sample person with a different description."
                )
            ),
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonItemPreview() {
    MyApplicationTheme {
        PersonItem(
            person = Person(
                firstName = "John",
                lastName = "Doe",
                description = "A sample person with a longer description to test the layout."
            ),
            onPersonClick = {}
        )
    }
}
