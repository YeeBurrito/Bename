// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.addedittag

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.bename.BuildConfig
import com.bename.R
import com.bename.ui.person.PersonViewModel
import com.bename.ui.tag.TagViewModel

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
    val allLocations by viewModel.locationRepository.locations.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var selectedLocations by remember { mutableStateOf<List<String>>(emptyList()) }
    var newLocationName by remember { mutableStateOf("") }
    var newLocationLat by remember { mutableStateOf<Double?>(null) }
    var newLocationLng by remember { mutableStateOf<Double?>(null) }

    val premiumEnabled by viewModel.premiumEnabled.collectAsState()

    // Get all tags and check if another tag already has a location
    val allTags = viewModel.tagRepository.tags.collectAsState(initial = emptyList()).value
    val otherTagsWithLocation = allTags.any { it.uid != tagId && it.locations.isNotEmpty() }
    val canSelectLocation = premiumEnabled || !otherTagsWithLocation

    val people by viewModel.people.collectAsState()
    val tag = tagState
    var selectedPersonIds by remember { mutableStateOf<List<Int>>(tagState?.personIds ?: emptyList()) }

    // Track initial values for dirty check
    var initialName by remember { mutableStateOf("") }
    var initialLocations by remember { mutableStateOf<List<String>>(emptyList()) }
    var initialPersonIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Prepare a working tag object for the UI, even in add mode
    val workingTag = tagState ?: Tag(
        name = name,
        locations = selectedLocations,
        personIds = selectedPersonIds
    )

    // Check if any field has changed
    val isDirty = name != initialName ||
            selectedLocations != initialLocations ||
            selectedPersonIds != initialPersonIds

    // Intercept back action if there are unsaved changes
    BackHandler(enabled = isDirty) {
        showDialog = true
    }

    // Show confirmation dialog if user tries to go back with unsaved changes
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to go back?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onBack()
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("No") }
            }
        )
    }

    // When adding a location, check permission and show popup if needed
    val onAddLocation: (String, Double, Double) -> Unit = { name, lat, lng ->
        val locationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            showLocationDialog = true
            viewModel.addLocationToTag(name, lat, lng)
        } else {
            viewModel.addLocationToTag(name, lat, lng)
        }
    }

    // Populate fields and initial values when tagState changes (for edit)
    LaunchedEffect(tagState) {
        tagState?.let {
            if (name.isBlank() && selectedLocations.isEmpty() && selectedPersonIds.isEmpty()) {
                name = it.name
                selectedLocations = it.locations
                selectedPersonIds = it.personIds
                initialName = it.name
                initialLocations = it.locations
                initialPersonIds = it.personIds
            }
        }
    }
    // For add mode, set initial values to empty
    LaunchedEffect(tagId) {
        if (tagId == null) {
            initialName = ""
            initialLocations = emptyList()
            initialPersonIds = emptyList()
        }
    }

    // Places API initialization
    var placesInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(
                context.applicationContext,
                BuildConfig.PLACES_API_KEY
            )
        }
        placesInitialized = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddEditPersonTopAppBar(
                title = if (tagId == null) "Add Tag" else "Edit Tag",
                onBack = {
                    if (isDirty) {
                        showDialog = true
                    } else {
                        onBack()
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tag name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Inline Places Autocomplete for adding locations
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
                // Show location suggestions
                suggestions.forEach { prediction ->
                    Text(
                        text = prediction.getFullText(null).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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
                                        if (tagId == null) {
                                            // Add mode: update selectedLocations directly
                                            selectedLocations = selectedLocations + place.name!!
                                        } else {
                                            // Edit mode: update via ViewModel
                                            onAddLocation(place.name!!, latLng.latitude, latLng.longitude)
                                            selectedLocations = selectedLocations + place.name!!
                                        }
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

            // List all locations with remove buttons
            Text("Locations:")
            if (canSelectLocation) {
                val locationsToShow = if (tagId == null) selectedLocations else tagState?.locations ?: emptyList()
                locationsToShow.forEach { locationName ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(locationName, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            if (tagId == null) {
                                selectedLocations = selectedLocations - locationName
                            } else {
                                viewModel.removeLocationFromTag(locationName)
                                selectedLocations = selectedLocations - locationName
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Location")
                        }
                    }
                }
            } else {
                Text("Only one tag can have a location. Upgrade to premium for more.")
            }

            // People list section for assigning tag
            PeopleTagAssignmentList(
                people = people,
                tag = workingTag,
                selectedPersonIds = selectedPersonIds,
                onTogglePerson = { person ->
                    if (tagId == null) {
                        // Add mode: update selectedPersonIds directly
                        selectedPersonIds = if (person.uid in selectedPersonIds) {
                            selectedPersonIds - person.uid
                        } else {
                            selectedPersonIds + person.uid
                        }
                    } else {
                        // Edit mode: update via ViewModel
                        viewModel.toggleTagForPerson(tagState!!, person)
                        if (person.uid in selectedPersonIds) {
                            selectedPersonIds = selectedPersonIds - person.uid
                        } else {
                            selectedPersonIds = selectedPersonIds + person.uid
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            val coroutineScope = rememberCoroutineScope()
            // Save or add tag button
            Button(
                onClick = {
                    val tag = Tag(
                        name = name,
                        locations = selectedLocations,
                        personIds = (if (tagId == null) selectedPersonIds else tagState?.personIds ?: emptyList())
                    ).apply {
                        if (tagId != null && tagState != null) {
                            uid = tagState!!.uid // Set the existing tag's uid
                        }
                    }
                    viewModel.addOrUpdateTag(tag)
                    if (tagId == null) {
                        // New tag, assign to selected people
                        coroutineScope.launch {
                            val allTags = viewModel.tagRepository.getAllTags()
                            val newTag = allTags.find { it.name == tag.name && it.locations == tag.locations }
                            if (newTag != null) {
                                selectedPersonIds.forEach { personId ->
                                    val person = people.find { it.uid == personId }
                                    if (person != null) {
                                        viewModel.toggleTagForPerson(newTag, person)
                                    }
                                }
                            }
                        }
                    }
                    onBack()
                },
                enabled = name.isNotBlank()
            ) {
                Text(if (tagId == null) "Add" else "Save")
            }

            // Delete tag button (only in edit mode)
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

    // Full screen location access popup overlay
    if (showLocationDialog) {
        LocationAccessPopup(
            onContinue = {
                showLocationDialog = false
                showRationale = true
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    // Rationale dialog overlay for location permission
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Allow Bename to access this device's location?") },
            text = {
                Text(
                    "To enable location access, go to App Settings > Permissions > Location and select 'Allow all the time' or 'Allow only while using the app'. This is required for location-based reminders."
                )
            },
            confirmButton = {
                Button(onClick = {
                    showRationale = false
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    ContextCompat.startActivity(context, intent, null)
                }) { Text("Continue") }
            },
            dismissButton = {
                Button(onClick = { showRationale = false }) { Text("Cancel") }
            }
        )
    }
}

// List of people for assigning/removing tag
@Composable
fun PeopleTagAssignmentList(
    people: List<Person>,
    tag: Tag,
    selectedPersonIds: List<Int>,
    onTogglePerson: (Person) -> Unit
) {
    Column {
        Text("Assign to People", style = MaterialTheme.typography.titleMedium)
        people.forEach { person ->
            val isTagged = if (tag.uid == 0) {
                selectedPersonIds.contains(person.uid)
            } else {
                person.tagIds.contains(tag.uid)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTogglePerson(person) }
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.person_check_24px),
                    contentDescription = null,
                    tint = if (isTagged) Color(0xFF1976D2) else Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("${person.firstName} ${person.lastName.orEmpty()}")
            }
        }
    }
}

// Helper extension for coroutines to await a Task result
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it, null) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

@Preview(showBackground = true)
@Composable
fun AddEditTagScreenPreview() {
    AddEditTagScreen(
        tagId = null,
        onBack = {}
    )
}

// Popup for requesting location access
@Composable
fun LocationAccessPopup(
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Location Access for Name Reminders",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.benamelocationbanner),
                contentDescription = "Location",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            Text(
                text = "Bename uses your location to remind you of people you've met when you return to places you've tagged.\n\nThis requires access to your location even when the app is not in use, so reminders can work automatically.\n\nYour location data is used only for this feature and is not stored or shared.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onDismiss) {
                    Text("Not Now")
                }
                Button(onClick = onContinue) {
                    Text("Continue")
                }
            }
        }
    }
}