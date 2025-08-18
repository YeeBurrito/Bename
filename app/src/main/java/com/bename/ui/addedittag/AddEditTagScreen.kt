package com.bename.ui.addedittag

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.data.LocationRepository
import com.bename.data.local.database.Person
import com.bename.data.local.database.Tag
import com.bename.ui.addeditperson.AddEditPersonViewModel
import com.bename.ui.util.AddEditPersonTopAppBar
import com.bename.ui.locations.PlacesAutocompleteScreen
import kotlinx.coroutines.launch
import kotlin.coroutines.resumeWithException
import com.google.android.gms.location.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun AddEditTagScreen(
    tagId: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: AddEditTagViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    LaunchedEffect(tagId) {
        viewModel.loadTag(tagId?.toLong())
    }

    val tagState by viewModel.tag.collectAsStateWithLifecycle()
    val allLocations by viewModel.locationRepository.locations.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var selectedLocations by remember { mutableStateOf<List<String>>(emptyList()) }
    var newLocationName by remember { mutableStateOf("") }
    var newLocationLat by remember { mutableStateOf<Double?>(null) }
    var newLocationLng by remember { mutableStateOf<Double?>(null) }

    val premiumEnabled by viewModel.premiumEnabled.collectAsState()

    val allTags = viewModel.tagRepository.tags.collectAsState(initial = emptyList()).value
    val otherTagsWithLocation = allTags.any { it.uid != tagId && it.locations.isNotEmpty() }
    val canSelectLocation = premiumEnabled || !otherTagsWithLocation

    LaunchedEffect(tagState) {
        tagState?.let {
            name = it.name
            selectedLocations = it.locations
        }
    }

    // Places API initialization
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Inline Places Autocomplete
            if (placesInitialized) {
                var searchQuery by remember { mutableStateOf("") }
                var suggestions by remember { mutableStateOf<List<com.google.android.libraries.places.api.model.AutocompletePrediction>>(emptyList()) }
                val placesClient = remember { com.google.android.libraries.places.api.Places.createClient(context) }
                val sessionToken = remember { com.google.android.libraries.places.api.model.AutocompleteSessionToken.newInstance() }
                val coroutineScope = rememberCoroutineScope()

                fun fetchPredictions(query: String) {
                    if (query.isBlank()) {
                        suggestions = emptyList()
                        return
                    }
                    val request = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(sessionToken)
                        .setQuery(query)
                        .build()
                    coroutineScope.launch {
                        val response = placesClient.findAutocompletePredictions(request).await()
                        suggestions = response.autocompletePredictions
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        fetchPredictions(it)
                    },
                    label = { Text("Add a location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                suggestions.forEach { prediction ->
                    Text(
                        text = prediction.getFullText(null).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Fetch place details
                                val placeId = prediction.placeId
                                val placeFields = listOf(
                                    com.google.android.libraries.places.api.model.Place.Field.NAME,
                                    com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
                                )
                                val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields).build()
                                coroutineScope.launch {
                                    val response = placesClient.fetchPlace(request).await()
                                    val place = response.place
                                    val latLng = place.latLng
                                    if (place.name != null && latLng != null) {
                                        viewModel.addLocation(
                                            com.bename.data.local.database.Location(
                                                name = place.name!!,
                                                latitude = latLng.latitude,
                                                longitude = latLng.longitude
                                            )
                                        )
                                        searchQuery = ""
                                        suggestions = emptyList()
                                    }
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List all locations with checkboxes
            Text("Locations:")
            if (canSelectLocation) {
                allLocations.forEach { location ->
                    val checked = selectedLocations.contains(location.name)
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                selectedLocations = if (isChecked) {
                                    if (premiumEnabled) selectedLocations + location.name
                                    else listOf(location.name) // Only one allowed if not premium
                                } else {
                                    selectedLocations - location.name
                                }
                            }
                        )
                        Text(location.name, modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                viewModel.deleteLocation(location)
                                selectedLocations = selectedLocations - location.name
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete location")
                        }
                    }
                }
            } else {
                Text("Only one tag can have a location. Upgrade to premium for more.")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val tag = Tag(
                        name = name,
                        locations = selectedLocations,
                        personIds = emptyList()
                    ).also {
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

            if (tagId != null && tagState != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.deleteTag(tagState!!)
                        onBack()
                    }
                ) {
                    Text("Delete Tag")
                }
            }
        }
    }
}

// Helper extension for coroutines
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it, null) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

//@Composable
//fun AddEditTagScreen(
//    tagId: Int? = null,
//    modifier: Modifier = Modifier,
//    viewModel: AddEditTagViewModel = hiltViewModel(),
//    onBack: () -> Unit = {}
//) {
//    // Load tag data when entering the screen
//    LaunchedEffect(tagId) {
//        viewModel.loadTag(tagId?.toLong())
//    }
//
//    val tagState by viewModel.tag.collectAsStateWithLifecycle()
//
//    var name by remember { mutableStateOf("") }
//    // var locations by remember { mutableStateOf(emptyList<String>()) }
//    // var assciatedPersonIds by remember { mutableStateOf(emptyList<Long>()) }
//
//    // Populate fields when personState changes (for edit)
//    LaunchedEffect(tagState) {
//        tagState?.let {
//            name = it.name
//            // locations = it.locations
//            // associatedPersonIds = it.personIds
//        }
//    }
//
//    Scaffold(
//        modifier = modifier.fillMaxSize(),
//        topBar = {
//            AddEditPersonTopAppBar(
//                title = if (tagId == null) "Add Tag" else "Edit Tag",
//                onBack = onBack
//            )
//        },
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            //Places Autocomplete for location input
//            var placesInitialized by remember { mutableStateOf(false) }
//            val context = LocalContext.current
//
//            LaunchedEffect(Unit) {
//                if (!com.google.android.libraries.places.api.Places.isInitialized()) {
//                    com.google.android.libraries.places.api.Places.initialize(
//                        context.applicationContext,
//                        "REDACTED_API_KEY"
//                    )
//                }
//                placesInitialized = true
//            }
//
//            if (placesInitialized) {
//                PlacesAutocompleteScreen(
//                    apiKey = "REDACTED_API_KEY",
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                onClick = {
//                    val tag = Tag(
//                        name = name,
//                        locations = emptyList(), // Placeholder for locations
//                        personIds = emptyList() // Placeholder for associated person IDs
//                    ).also {
//                        // If editing, preserve uid
//                        if (tagId != null && tagState != null) {
//                            it.uid = tagState!!.uid
//                        }
//                    }
//                    viewModel.addOrUpdateTag(tag)
//                    onBack()
//                },
//                enabled = name.isNotBlank()
//            ) {
//                Text(if (tagId == null) "Add" else "Save")
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun AddEditTagScreenPreview() {
    AddEditTagScreen(
        tagId = null,
        onBack = {}
    )
}