// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.bename.ui.theme.MyApplicationTheme
import com.bename.ui.locations.GeofenceManager
import com.google.android.gms.location.LocationServices
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import com.bename.data.LocationRepository
import com.bename.ui.settings.SettingsViewModel
import javax.inject.Inject
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bename.R

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge drawing for the activity
        setContent {
            // Get settings view model and observe dark mode preference
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val darkMode by settingsViewModel.darkMode.collectAsState()
            val darkTheme = when (darkMode) {
                1 -> false // Light mode
                2 -> true  // Dark mode
                else -> isSystemInDarkTheme() // System default
            }
            // Apply app theme
            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation() // Main navigation composable
                }
            }
            // Show location access popup if needed
            val locationPopupShownState = settingsViewModel.locationPopupShown.collectAsState(initial = null)
            val showPopup = locationPopupShownState.value == false
            var showRationale by remember { mutableStateOf(false) }

            if (locationPopupShownState.value != null && showPopup) {
                LocationAccessPopup(
                    onContinue = { showRationale = true },
                    onDismiss = { settingsViewModel.setLocationPopupShown(true) }
                )
            }

            // Show rationale dialog if user continues
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
                            settingsViewModel.setLocationPopupShown(true)
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = android.net.Uri.fromParts("package", packageName, null)
                            }
                            ContextCompat.startActivity(this@MainActivity, intent, null)
                            requestLocationPermission()
                        }) { Text("Continue") }
                    },
                    dismissButton = {
                        Button(onClick = { showRationale = false }) { Text("Cancel") }
                    }
                )
            }
        }
        // Register geofences after locations are loaded
        val geofencingClient = LocationServices.getGeofencingClient(this)
        val geofenceManager = GeofenceManager(this, geofencingClient)

        lifecycleScope.launchWhenStarted {
            locationRepository.locations.collectLatest { locations ->
                // Register geofences if location permission is granted
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    geofenceManager.registerGeofences(locations)
                } else {
                    Log.e("MainActivity", "Location permission not granted")
                }
            }
        }

        // Set up location updates to keep geofencing engine active
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            500L // 0.5 second interval
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // No-op: just keeps location engine active for geofencing
            }
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Clear all notifications when activity resumes
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    // Request location permissions from the user
    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                1001
            )
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
        // Open location settings for the user
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
}

// Composable for the location access popup
@Composable
fun LocationAccessPopup(
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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