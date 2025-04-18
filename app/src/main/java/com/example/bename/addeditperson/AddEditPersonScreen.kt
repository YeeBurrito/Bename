@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bename.addeditperson

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.R
import com.example.bename.ui.theme.BenameTheme
import com.example.bename.util.AddEditPersonTopAppBar

@Composable
fun AddEditPersonScreen(
    @StringRes topBarTitle: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditPersonViewModel = viewModel(),
    ) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddEditPersonTopAppBar(topBarTitle, onBack)
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = viewModel::savePerson) {
                Icon(Icons.Filled.Done, stringResource(id = R.string.cd_save))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AddEditPersonContent(
            firstName = uiState.firstName,
            lastName = uiState.lastName,
            description = uiState.description,
            onFirstNameChanged = viewModel::updateFirstName,
            onLastNameChanged = viewModel::updateLastName,
            onDescriptionChanged = viewModel::updateDescription,
            modifier = Modifier
                .padding(paddingValues)
        )
    }
}

@Composable
fun AddEditPersonContent(
    modifier: Modifier = Modifier,
    firstName: String = "",
    lastName: String = "",
    description: String = "",
    onFirstNameChanged: (String) -> Unit = {},
    onLastNameChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    ) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChanged,
            label = { Text(stringResource(R.string.first_name)) },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            maxLines = 1,
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChanged,
            label = { Text(stringResource(R.string.last_name)) },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            maxLines = 1,
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            maxLines = 5,
        )
        Text(
            text = "Tags (optional)",
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Preview
@Composable
private fun AddPersonScreenPreview() {
    BenameTheme {
        AddEditPersonScreen(
            topBarTitle = R.string.add_person,
            onBack = {}
        )
    }
}