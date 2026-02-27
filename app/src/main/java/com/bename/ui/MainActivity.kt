/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val darkMode by settingsViewModel.darkMode.collectAsState()
            val darkTheme = when (darkMode) {
                1 -> false // Light
                2 -> true  // Dark
                else -> isSystemInDarkTheme() // System default
            }
            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
            val locationPopupShownState = settingsViewModel.locationPopupShown.collectAsState(initial = null)
            val showPopup = locationPopupShownState.value == false
            var showRationale by remember { mutableStateOf(false) }

            if (locationPopupShownState.value != null && showPopup) {
            //if (true) { // Always show for testing
                LocationAccessPopup(
                    onContinue = { showRationale = true },
                    onDismiss = { settingsViewModel.setLocationPopupShown(true) }
                )
            }

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
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    geofenceManager.registerGeofences(locations)
                } else {
                    Log.e("MainActivity", "Location permission not granted")
                }
            }
        }


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            500L // 0.5 second
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
        // Clear all notifications
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

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
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
}


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
                //painter = painterResource(id = R.drawable.ic_location), // Replace with your image
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