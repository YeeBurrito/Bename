package com.example.bename.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bename.MainActivity
import com.example.bename.ui.theme.BenameTheme

class AddPersonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BenameTheme {
                AddPersonMenu()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddPersonMenu(

) {
    val viewModel: AddPersonViewModel = viewModel()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AddPersonMenuTopBar(
                savePerson = viewModel::savePerson,
                context = context
            )
        }
    ) {
        AddPersonMenuContent(
            onFirstNameChanged = viewModel::onFirstNameChange,
            onLastNameChanged = viewModel::onLastNameChange,
            onDescriptionChanged = viewModel::onDescriptionChange
        )
    }
}

@Composable
fun AddPersonMenuContent(
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
        AddPersonMenu()
    }
}