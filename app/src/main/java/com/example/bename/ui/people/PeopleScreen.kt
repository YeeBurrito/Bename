package com.example.bename.ui.people

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.R
import com.example.bename.data.Person
import com.example.bename.util.LoadingContent
import com.example.bename.util.PeopleTopAppBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PeopleScreen(
    onAddPerson: () -> Unit,
    onPersonClick: (Person) -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = viewModel(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { PeopleTopAppBar(openDrawer, onAddPerson) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = onAddPerson) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.cd_add_person))
            }
        }
    ) { paddingValues ->
        val uiState = viewModel.uiState

        PeopleContent(
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
fun PeopleContent(
    people: List<Person>,
    onPersonClick: (Person) -> Unit,
    modifier: Modifier = Modifier)
{
    LoadingContent(
        loading = false,
        empty = people.isEmpty(),
        emptyContent = {
            Text(text = stringResource(id = R.string.no_people))
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            Text(
                text = "People screen placeholder",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 16.dp
                    // TODO: Make paddings dimension resources
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
fun PersonItem(
    person: Person,
    onPersonClick: (Person) -> Unit
)
{
    //TODO: Make sure only one item is expanded at a time
    var isExpanded by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable {
                isExpanded = !isExpanded
                onPersonClick(person)
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${person.firstName} ${person.lastName.orEmpty()}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Tags: (tags go here)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isExpanded) {
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = person.desc.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Created: ${dateFormatter.format(person.created)} \t Updated: ${dateFormatter.format(person.updated)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = stringResource(id = R.string.cd_arrow_forward)
        )
    }
}

@Preview
@Composable
fun PeopleContentPreview() {
    MaterialTheme {
        Surface {
            PeopleContent(
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
                        created = Date(),
                        updated = Date()
                    )
                ),
                onPersonClick = {}
            )
        }
    }
}

@Preview
@Composable
fun PersonItemPreview() {
    MaterialTheme {
        Surface {
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
}