package com.bename.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.R
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    currentLocations: List<String> = emptyList(),
    onPersonClick: (Int) -> Unit = {}
) {
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val people by viewModel.people.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var searchFocused by remember { mutableStateOf(false) }
    var selectedTags by remember { mutableStateOf(listOf<Tag>()) }

    // Filter tags by search query
    //val filteredTags = if (searchQuery.text.isBlank()) tags
    //else tags.filter { it.name.contains(searchQuery.text, ignoreCase = true) }

    // Filter people by selected tags and search query
    val filteredPeople = people.filter { person ->
        val matchesTags = selectedTags.all { tag -> tag.uid in person.tagIds }
        val matchesQuery = searchQuery.text.isBlank() ||
                person.firstName.contains(searchQuery.text, ignoreCase = true) ||
                (person.lastName?.contains(searchQuery.text, ignoreCase = true) == true)
        matchesTags && matchesQuery
    }
    // Get people who have all selected tags
    val peopleWithSelectedTags = if (selectedTags.isEmpty()) {
        people
    } else {
        people.filter { person ->
            selectedTags.all { tag -> tag.uid in person.tagIds }
        }
    }

    // Collect possible tags from these people, excluding already selected tags
    val possibleTagUids = peopleWithSelectedTags
        .flatMap { it.tagIds }
        .toSet()
        .minus(selectedTags.map { it.uid }.toSet())

    val possibleTags = if (selectedTags.isEmpty()) {
        tags
    } else {
        // Get people who have all selected tags
        val peopleWithSelectedTags = people.filter { person ->
            selectedTags.all { tag -> tag.uid in person.tagIds }
        }
        // Collect possible tags from these people, excluding already selected tags
        val possibleTagUids = peopleWithSelectedTags
            .flatMap { it.tagIds }
            .toSet()
            .minus(selectedTags.map { it.uid }.toSet())
        tags.filter { it.uid in possibleTagUids }
    }

    // Filter by search query
    val filteredTags = if (searchQuery.text.isBlank()) possibleTags
    else possibleTags.filter { it.name.contains(searchQuery.text, ignoreCase = true) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search people or tags") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { searchFocused = it.isFocused },
            singleLine = true
        )

        // Selected tags below search bar
        if (selectedTags.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(selectedTags) { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.shoppingmode_24pxfilled),
                                contentDescription = "Tag Icon",
                                tint = Color(0xFF1976D2), // Blue color
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove tag",
                                modifier = Modifier
                                    .clickable {
                                        selectedTags = selectedTags - tag
                                    }
                            )
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tag suggestions (when focused or typing)
        if (searchFocused) { //|| searchQuery.text.isNotBlank()) {
            LazyColumn {
                items(filteredTags) { tag ->
                    val isAtLocation = tag.locations.any { it in currentLocations }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTags = selectedTags + tag
                                searchQuery = TextFieldValue("")
                            }
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedTags.isEmpty()) {
                            // Count how many people have this tag
                            val count = people.count { person -> tag.uid in person.tagIds }
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    if (isAtLocation) {
                        // Show the first matching location
                        val locationName = tag.locations.firstOrNull { it in currentLocations }
                        if (locationName != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 28.dp, top = 2.dp, bottom = 4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = com.google.android.libraries.places.R.drawable.location_on_icon),
                                    contentDescription = "Location Arrow",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tag at $locationName",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(filteredPeople) { person ->
                val personTags = tags.filter { it.uid in person.tagIds }
                PersonSearchItem(
                    person = person,
                    tags = personTags,
                    onClick = { onPersonClick(person.uid) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun PersonSearchItem(
    person: Person,
    tags: List<Tag>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "${person.firstName} ${person.lastName.orEmpty()}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = tags.joinToString(", ") { it.name },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}