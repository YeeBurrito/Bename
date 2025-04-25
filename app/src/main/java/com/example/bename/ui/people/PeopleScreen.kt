package com.example.bename.ui.people

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.R
import com.example.bename.data.Person
import com.example.bename.util.PeopleTopAppBar
import java.util.Date

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
fun PeopleContent(people: Any, onPersonClick: (Person) -> Unit, modifier: Any) {

}

@Composable
fun PersonItem(
    person: Person,
    onPersonClick: (Person) -> Unit
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .clickable { onPersonClick(person) }
    ) {
        Text(text = person.firstName)
        person.lastName?.let { Text(text = it) }
        person.desc?.let { Text(text = it) }
    }
}

@Preview
@Composable
fun PersonItemPreview() {
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