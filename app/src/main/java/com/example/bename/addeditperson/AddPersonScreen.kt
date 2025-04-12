@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bename.addeditperson

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.R
import com.example.bename.ui.theme.BenameTheme
import com.example.bename.util.AddEditPersonTopAppBar

@Composable
fun AddPersonScreen(
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
            onFirstNameChanged = { viewModel.onFirstNameChanged(it) },
            onLastNameChanged = { viewModel.onLastNameChanged(it) },
            onDescriptionChanged = { viewModel.onDescriptionChanged(it) }
        )
    }
}

@Composable
fun AddEditPersonContent(
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Add a new person")
        OutlinedTextField(
            value = "",
            onValueChange = onFirstNameChanged,
            label = { Text("First Name") },
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = "",
            onValueChange = onLastNameChanged,
            label = { Text("Last Name") },
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = "",
            onValueChange = onDescriptionChanged,
            label = { Text("Description") },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonMenuTopBar(
    modifier: Modifier = Modifier,
    savePerson: () -> Unit,
    context: android.content.Context
) {
    //The top bar has a cancel button on the left, text in the middle and a save button on the right
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "Add Person") },
        actions = {
            Button(onClick = { savePerson()}) {
                Text(text = "Save")
            }
        }
    )
}

@Preview
@Composable
fun AddPersonMenuPreview() {
    BenameTheme {
    }
}