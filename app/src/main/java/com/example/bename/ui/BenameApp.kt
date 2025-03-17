package com.example.bename.ui

import android.annotation.SuppressLint
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.bename.ui.theme.BenameTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BenameApp(
    modifier: Modifier = Modifier
) {
    val viewModel: BenameViewModel = viewModel()
    Scaffold(
        bottomBar =  {
            BenameBottomAppBar()
        }
    ) {
        Text(
            text = "Test"
        )
    }
}

@Composable
fun BenameBottomAppBar(modifier: Modifier = Modifier) {
    BottomAppBar() {
        Row(
            modifier = Modifier.fillMaxWidth(), // Optional: Fill the available width
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Search", Modifier.background(Color.Red))
            Text("Tags", Modifier.background(Color.Green))
            Text("Add Person", Modifier.background(Color.Green))
            Text("People", Modifier.background(Color.Green))
            Text("Settings", Modifier.background(Color.Blue))
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