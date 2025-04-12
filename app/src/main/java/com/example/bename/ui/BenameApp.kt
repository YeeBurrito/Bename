package com.example.bename.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.bename.data.local.BenameTab
import com.example.bename.ui.theme.BenameTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BenameApp(
    modifier: Modifier = Modifier
) {
    val viewModel: BenameViewModel = viewModel()
    val benameUiState = viewModel.uiState.collectAsState().value
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        bottomBar = {
            BenameBottomAppBar(viewModel = viewModel, benameUiState = benameUiState)
        }
    ) {
        when (benameUiState.currentTab) {
            BenameTab.Search -> {
                BenameSearchScreen()
            }

            BenameTab.People -> {
                BenamePeopleScreen()
            }

            BenameTab.Tags -> {
                BenameTagsScreen()
            }

            BenameTab.Settings -> {
                BenameSettingsScreen()
            }
        }
    }
}

@Composable
fun BenameBottomAppBar(
    modifier: Modifier = Modifier,
    viewModel: BenameViewModel = viewModel(),
    benameUiState: BenameUiState
) {
    BottomAppBar() {
        Row(
            //modifier = Modifier.fillMaxWidth(), // Optional: Fill the available width
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.switchTab(BenameTab.Search) }) {
                Text("Search", Modifier.background(Color.Green))
            }
            Button(onClick = { viewModel.switchTab(BenameTab.Tags) }) {
                Text("Tags", Modifier.background(Color.Green))
            }
            // The AddPerson button starts the AddPersonActivity with an Intent
            val context = LocalContext.current
            Button(onClick = { }){
                Text("Add Person", Modifier.background(Color.Green))
            }
            Button(onClick = { viewModel.switchTab(BenameTab.People) }) {
                Text("People", Modifier.background(Color.Green))
            }
            Button(onClick = { viewModel.switchTab(BenameTab.Settings) }) {
                Text("Add Person", Modifier.background(Color.Green))
            }
        }
    }
}

@Preview
@Composable
fun BenamePreview() {
    BenameTheme {
        BenameApp()
    }
}