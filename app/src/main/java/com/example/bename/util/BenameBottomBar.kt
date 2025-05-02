package com.example.bename.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BenameBottomBar(
    currentRoute: String,
    onNavigateToSearch: () -> Unit,
    onNavigateToPeople: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val navigationItems = listOf(
        NavigationItem(
            title = "Search",
            icon = Icons.Default.Search,
            route = "search"
        ),
        NavigationItem(
            title = "People",
            icon = Icons.Default.AccountCircle,
            route = "people"
        ),
        NavigationItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            route = "settings"
        )
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = item.title)
                },
                selected = isSelected,
                onClick = {
                    selectedNavigationIndex.value = index
                    when (item.route) {
                        "search" -> onNavigateToSearch()
                        "people" -> onNavigateToPeople()
                        "settings" -> onNavigateToSettings()
                    }
                }
            )
        }
    }
}

@Composable
@Preview
fun PreviewBenameBottomBar() {
    BenameBottomBar(
        currentRoute = "home",
        onNavigateToSearch = {},
        onNavigateToPeople = {},
        onNavigateToSettings = {}
    )
}