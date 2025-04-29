package com.example.bename.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.R
import com.example.bename.data.Person
import com.example.bename.util.LoadingContent
import com.example.bename.util.AddEditPersonTopAppBar
import java.util.Date

@Composable
fun SearchScreen(
    onPersonClick: (Person) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AddEditPersonTopAppBar(title = R.string.search_title, onBack = onBack) }
    ) { paddingValues ->
        val uiState = viewModel.uiState

        SearchContent(
            people = uiState.items,
            onPersonClick = onPersonClick,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun SearchContent(
    people: List<Person>,
    onPersonClick: (Person) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContent(
        loading = false,
        empty = people.isEmpty(),
        emptyContent = {
            Text(text = stringResource(id = R.string.no_results))
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            Text(
                text = "Search screen placeholder",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 16.dp
                )
            )
            LazyColumn {
                items(people) { person ->
                    PersonItem(
                        person = person,
                        onPersonClick = onPersonClick
                    )
                    Divider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun PersonItem(person: Person, onPersonClick: (Person) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPersonClick(person) }
            .padding(dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            Text(text = "${person.firstName} ${person.lastName}")
            person.desc?.let { Text(text = it) }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun SearchContentPreview() {
    MaterialTheme {
        SearchContent(
            people = listOf(
                Person(
                    id = 1,
                    firstName = "John",
                    lastName = "Doe",
                    desc = "A sample person",
                    created = Date(),
                    updated = Date()
                ),
                Person(
                    id = 2,
                    firstName = "Jane",
                    lastName = "Doe",
                    desc = "Another sample person",
                    created = Date(),
                    updated = Date()
                )
            ),
            onPersonClick = {}
        )
    }
}

@Preview
@Composable
fun PersonItemPreview() {
    MaterialTheme {
        PersonItem(
            person = Person(
                id = 1,
                firstName = "John",
                lastName = "Doe",
                desc = "A sample person",
                created = Date(),
                updated = Date()
            ),
            onPersonClick = {}
        )
    }
}