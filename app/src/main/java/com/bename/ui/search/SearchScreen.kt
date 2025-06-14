package com.bename.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.data.local.database.Person

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onPersonClick: (Int) -> Unit = {}
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val people by viewModel.people.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateQuery(it) },
            label = { Text("Search people by name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(people) { person ->
                PersonSearchItem(person = person, onClick = { onPersonClick(person.uid) })
            }
        }
    }
}

@Composable
private fun PersonSearchItem(person: Person, onClick: () -> Unit) {
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
        if (!person.description.isNullOrBlank()) {
            Text(
                text = person.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}