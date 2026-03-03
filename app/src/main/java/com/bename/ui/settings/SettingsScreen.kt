// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat.startActivity
import com.bename.R

// Main settings screen composable
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    billingViewModel: BillingViewModel = hiltViewModel()
) {
    // Collect state from ViewModels
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }
    val premiumEnabled by viewModel.premiumEnabled.collectAsState()
    val premiumPurchased by billingViewModel.premiumPurchased.collectAsState()
    val context = LocalContext.current
    val isPremiumProductReady by billingViewModel.isPremiumProductReady.collectAsState()

    val darkModeOptions = listOf("System Default", "Light", "Dark")

    var showLocationDialog by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    // Check if location permission is granted
    val locationPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Box(modifier = Modifier.fillMaxSize()) {
        // Main settings content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // Premium purchase row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Premium (unlimited locations)", modifier = Modifier.weight(1f))
                if (premiumPurchased) {
                    Text("Purchased", color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = {
                            val activity = context as? Activity
                            if (activity != null) {
                                billingViewModel.launchPurchaseFlow(activity)
                            }
                        },
                        enabled = isPremiumProductReady
                    ) {
                        Text("Buy for \$1.99")
                    }
                }
            }
            // Notifications toggle row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Notifications", modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.setNotificationsEnabled(enabled)
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Show location access button if permission not granted
            if (!locationPermissionGranted) {
                Button(
                    onClick = { showLocationDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Location Access")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // Dark mode selection
            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
            darkModeOptions.forEachIndexed { idx, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setDarkMode(idx) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = darkMode == idx,
                        onClick = { viewModel.setDarkMode(idx) }
                    )
                    Text(label)
                }
            }
            // Dialog for notification permission
            if (showPermissionDialog) {
                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },
                    title = { Text("Permission Required") },
                    text = { Text("Please grant notification permission in system settings.") },
                    confirmButton = {
                        Button(onClick = { showPermissionDialog = false }) {
                            Text("OK")
                        }
                    }
                )
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
                        val activity = context as? Activity
                        activity?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                it.requestPermissions(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ),
                                    1001
                                )
                            } else {
                                it.requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    1001
                                )
                            }
                        }
                        // Open app settings for manual permission grant
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
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
}

// Popup explaining why location access is needed
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
                painter = painterResource(id = R.drawable.benamelocationbannertransparent),
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

// Preview for the settings screen
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}