package com.bename.ui.locations

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.BuildConfig
import com.bename.ui.util.BenameBottomBarPreview
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.PlaceAutocomplete
import com.google.android.libraries.places.widget.PlaceAutocompleteActivity
// com.google.android.libraries.places.widget.model.Field
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resumeWithException

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

    if (isInEditMode) {
        // Show a simple placeholder in preview
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

    // Initialize Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, apiKey)
        }
    }
    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

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
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                fetchPredictions(it)
            },
            label = { Text("Search for a location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
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
        Spacer(modifier = Modifier.height(24.dp))
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
    PlacesAutocompleteScreen(apiKey = "REDACTED_API_KEY")
}