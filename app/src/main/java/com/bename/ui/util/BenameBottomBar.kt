package com.bename.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bename.R

enum class BenameDestination(val label: String, val icon: ImageVector) {
    Search("Search", Icons.Default.Search),
    Tags("Tags", Icons.Default.Info),
    AddEdit("Add Person", Icons.Default.Add),
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
            if (destination == BenameDestination.AddEdit) {
                // Special styling for Add Person
                NavigationBarItem(
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                //color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 6.dp,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Add Person",
                                        //tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Plus",
                                        //tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                        }
                    },
                    label = { Text(destination.label) },
                    selected = currentDestination == destination,
                    onClick = { onDestinationSelected(destination) }
                )
            } else {
                NavigationBarItem(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = currentDestination == destination,
                    onClick = { onDestinationSelected(destination) }
                )
            }
        }
    }
}

//@Composable
//fun BenameBottomBar(
//    currentDestination: BenameDestination,
//    onDestinationSelected: (BenameDestination) -> Unit
//) {
//    NavigationBar {
//        BenameDestination.values().forEach { destination ->
//            NavigationBarItem(
//                icon = { Icon(destination.icon, contentDescription = destination.label) },
//                label = { Text(destination.label) },
//                selected = currentDestination == destination,
//                onClick = { onDestinationSelected(destination) }
//            )
//        }
//    }
//}

@Preview
@Composable
fun BenameBottomBarPreview() {
    BenameBottomBar(
        currentDestination = BenameDestination.People,
        onDestinationSelected = {}
    )
}