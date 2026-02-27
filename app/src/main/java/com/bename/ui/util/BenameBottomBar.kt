package com.bename.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
            NavigationBarItem(
                icon = {
                    if (destination == BenameDestination.AddEdit) {
                        // Larger icon with plus overlay
                        Icon(
                            painter = painterResource(id = R.drawable.person_crop_circle_badge_plus_svgrepo_com),
                            contentDescription = "Add Person",
                            //tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(48.dp)
                                //.align(Alignment.Center)
                                //move slightly to the left
                                .padding(end = 4.dp)
                        )
                    } else if (destination == BenameDestination.Tags) {
                        Icon(
                            painter = painterResource(id = R.drawable.shoppingmode_24px),
                            contentDescription = destination.label
                        )
                    } else if (destination == BenameDestination.People) {
                        Icon(
                            painter = painterResource(id = R.drawable.group_24px),
                            contentDescription = destination.label
                        )
                    } else {
                        Icon(destination.icon, contentDescription = destination.label)
                    }
                },
                label = { Text(destination.label, fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
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