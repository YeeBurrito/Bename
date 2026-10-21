// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.locations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bename.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutocompleteScreen(
    apiKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlace by remember { mutableStateOf<String?>(null) }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isInEditMode = LocalInspectionMode.current

    // Show a simple preview in design/inspection mode
    if (isInEditMode) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Search for a location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Preview mode", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Initialize Places API if not already initialized
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, apiKey)
        }
    }
    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Fetch autocomplete predictions from Places API
    fun fetchPredictions(query: String) {
        if (query.isBlank()) {
            suggestions = emptyList()
            expanded = false
            return
        }
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .setTypeFilter(TypeFilter.ADDRESS)
            .build()
        coroutineScope.launch(Dispatchers.IO) {
            val response = placesClient.findAutocompletePredictions(request).await()
            suggestions = response.autocompletePredictions
            expanded = suggestions.isNotEmpty()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Dropdown menu for showing location suggestions
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    fetchPredictions(it)
                },
                label = { Text("Search for a location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                suggestions.forEach { prediction ->
                    DropdownMenuItem(
                        text = { Text(prediction.getFullText(null).toString()) },
                        onClick = {
                            selectedPlace = prediction.getFullText(null).toString()
                            searchQuery = selectedPlace ?: ""
                            expanded = false
                            suggestions = emptyList()
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Show the selected place
        selectedPlace?.let {
            Text("Selected place: $it", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Helper extension to use Tasks with coroutines
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it, null) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

@Preview(showBackground = true)
@Composable
fun PlacesAutocompleteScreenPreview() {
    PlacesAutocompleteScreen(apiKey = BuildConfig.PLACES_API_KEY)
}