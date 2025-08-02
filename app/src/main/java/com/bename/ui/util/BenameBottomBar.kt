package com.bename.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bename.R

enum class BenameDestination(val label: String, val icon: ImageVector) {
    Search("Search", Icons.Default.Search),
    Tags("Tags", Icons.Default.Info),
    AddEdit("Add/Edit", Icons.Default.Add),
    People("People", Icons.Default.Person),
    Settings("Settings", Icons.Default.Settings)
}

@Composable
fun BenameBottomBar(
    currentDestination: BenameDestination,
    onDestinationSelected: (BenameDestination) -> Unit
) {
    NavigationBar {
        BenameDestination.values().forEach { destination ->
            NavigationBarItem(
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}

@Preview
@Composable
fun BenameBottomBarPreview() {
    BenameBottomBar(
        currentDestination = BenameDestination.People,
        onDestinationSelected = {}
    )
}