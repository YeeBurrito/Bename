package com.bename.ui.addedittag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag
import com.bename.ui.addeditperson.AddEditPersonViewModel
import com.bename.ui.util.AddEditPersonTopAppBar
import com.bename.ui.locations.PlacesAutocompleteScreen

@Composable
fun AddEditTagScreen(
    tagId: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: AddEditTagViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    // Load tag data when entering the screen
    LaunchedEffect(tagId) {
        viewModel.loadTag(tagId?.toLong())
    }

    val tagState by viewModel.tag.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    // var locations by remember { mutableStateOf(emptyList<String>()) }
    // var assciatedPersonIds by remember { mutableStateOf(emptyList<Long>()) }

    // Populate fields when personState changes (for edit)
    LaunchedEffect(tagState) {
        tagState?.let {
            name = it.name
            // locations = it.locations
            // associatedPersonIds = it.personIds
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddEditPersonTopAppBar(
                title = if (tagId == null) "Add Tag" else "Edit Tag",
                onBack = onBack
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            //Places Autocomplete for location input
            var placesInitialized by remember { mutableStateOf(false) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                    com.google.android.libraries.places.api.Places.initialize(
                        context.applicationContext,
                        "REDACTED_API_KEY"
                    )
                }
                placesInitialized = true
            }

            if (placesInitialized) {
                PlacesAutocompleteScreen(
                    apiKey = "REDACTED_API_KEY",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val tag = Tag(
                        name = name,
                        locations = emptyList(), // Placeholder for locations
                        personIds = emptyList() // Placeholder for associated person IDs
                    ).also {
                        // If editing, preserve uid
                        if (tagId != null && tagState != null) {
                            it.uid = tagState!!.uid
                        }
                    }
                    viewModel.addOrUpdateTag(tag)
                    onBack()
                },
                enabled = name.isNotBlank()
            ) {
                Text(if (tagId == null) "Add" else "Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditTagScreenPreview() {
    AddEditTagScreen(
        tagId = null,
        onBack = {}
    )
}