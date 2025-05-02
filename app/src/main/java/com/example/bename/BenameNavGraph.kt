package com.example.bename

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bename.BenameDestinationsArgs.USER_MESSAGE_ARG
import com.example.bename.ui.people.PeopleScreen
import com.example.bename.ui.search.SearchScreen
import com.example.bename.util.AppModalDrawer
import com.example.bename.util.BenameBottomBar
import kotlinx.coroutines.CoroutineScope

@Composable
fun BenameNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = BenameDestinations.SEARCH_ROUTE,
    navActions: BenameNavigationActions = remember(navController) {
        BenameNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(BenameDestinations.PEOPLE_ROUTE) {
            // People screen implementation
        }

        composable(BenameDestinations.SETTINGS_ROUTE) {
            // Settings screen implementation
        }
    }
}