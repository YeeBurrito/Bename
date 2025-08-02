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

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }

    val darkModeOptions = listOf("System Default", "Light", "Dark")

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
        // Notifications switch (existing)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Notifications", modifier = Modifier.weight(1f))
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    // ... (existing permission logic)
                    viewModel.setNotificationsEnabled(enabled)
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Dark mode switch
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
}

// Simple SharedPreferences helpers
private fun loadNotificationsEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getBoolean("notifications_enabled", true)
}

private fun saveNotificationsEnabled(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("notifications_enabled", enabled).apply()
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}