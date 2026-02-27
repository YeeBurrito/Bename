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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bename.ui.addeditperson.AddEditPersonScreen
import com.bename.ui.addedittag.AddEditTagScreen
import com.bename.ui.locations.LocationStatusManager
import com.bename.ui.person.PersonScreen
import com.bename.ui.search.SearchScreen
import com.bename.ui.settings.SettingsScreen
import com.bename.ui.tag.TagScreen
import com.bename.ui.tag.TagUiState
import com.bename.ui.tag.TagViewModel
import com.bename.ui.util.BenameBottomBar
import com.bename.ui.util.BenameDestination
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bename.ui.settings.SettingsViewModel
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            AdView(context).apply {
                adUnitId = "ca-app-pub-3940256099942544/9214589741"
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360))
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("BannerAd", "Ad loaded successfully")
                    }
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("BannerAd", "Ad failed to load: ${adError.code} - ${adError.message}")
                    }
                    override fun onAdOpened() {
                        Log.d("BannerAd", "Ad opened")
                    }
                    override fun onAdClosed() {
                        Log.d("BannerAd", "Ad closed")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Get premiumEnabled from SettingsViewModel
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val premiumEnabled by settingsViewModel.premiumEnabled.collectAsState()

    // List of routes where the bottom bar should be shown
    val bottomBarRoutes = setOf(
        BenameDestination.People.name,
        BenameDestination.Search.name,
        BenameDestination.Tags.name,
        BenameDestination.Settings.name
    )

//    Scaffold(
//        bottomBar = {
//            if (currentRoute in bottomBarRoutes) {
//                BenameBottomBar(
//                    currentDestination = getCurrentDestination(currentRoute),
//                    onDestinationSelected = { destination ->
//                        navController.navigate(destination.name) {
//                            popUpTo(navController.graph.startDestinationId) { saveState = true }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                )
//            }
//        }
//    ) { innerPadding ->
    Column {
        Scaffold(
            bottomBar = {
                if (currentRoute in bottomBarRoutes) {
                    Box(
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        Column {
                            BenameBottomBar(
                                currentDestination = getCurrentDestination(currentRoute),
                                onDestinationSelected = { destination ->
                                    navController.navigate(destination.name) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            // Ad banner
                            //if (!premiumEnabled) {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(48.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = "Your Ad Here! Upgrade to Premium to remove ads.",
//                                        color = Color.White,
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(48.dp)
//                                            .background(Color(0xFF1976D2))
//                                            .padding(horizontal = 16.dp)
//                                    )
//                                }
                                //BannerAd(modifier = Modifier.fillMaxWidth()) TODO: Enable ads when approved by admob
                            //}
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BenameDestination.People.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                //composable(BenameDestination.People.name) { PersonScreen(modifier = Modifier.padding(16.dp)) }
                composable(BenameDestination.People.name) {
                    val tagViewModel: TagViewModel = hiltViewModel()
                    val tagUiState by tagViewModel.uiState.collectAsStateWithLifecycle()
                    val tags = when (tagUiState) {
                        is TagUiState.Success -> (tagUiState as TagUiState.Success).data
                        else -> emptyList()
                    }
                    PersonScreen(
                        modifier = Modifier.padding(16.dp),
                        onPersonClick = { personId ->
                            navController.navigate("${BenameDestination.AddEdit.name}/$personId")
                        },
                        onAddPerson = { navController.navigate(BenameDestination.AddEdit.name) },
                        tags = tags,
                    )
                }

                composable(
                    route = "AddEditTag/{tagId}",
                    arguments = listOf(navArgument("tagId") {
                        type = NavType.IntType; nullable = false
                    })
                ) { backStackEntry ->
                    val tagId = backStackEntry.arguments?.getInt("tagId")
                    AddEditTagScreen(
                        tagId = tagId,
                        onBack = { navController.navigateUp() }
                    )
                }

                composable(BenameDestination.Tags.name) {
                    val tagViewModel: TagViewModel = hiltViewModel()
                    val tagUiState by tagViewModel.uiState.collectAsStateWithLifecycle()
                    val tags = when (tagUiState) {
                        is TagUiState.Success -> (tagUiState as TagUiState.Success).data
                        else -> emptyList()
                    }
                    TagScreen(
                        modifier = Modifier.padding(16.dp),
                        onAddTag = { navController.navigate("AddEditTag") },
                        onTagClick = { tag -> navController.navigate("AddEditTag/${tag.uid}") }
                    )
                }

                //composable(BenameDestination.Search.name) { /* TODO: Add SearchScreen */ }
                composable(BenameDestination.Search.name) {
                    val currentLocations by LocationStatusManager.currentLocations.collectAsState()
                    SearchScreen(
                        currentLocations = currentLocations,
                        onPersonClick = { personId ->
                            navController.navigate("${BenameDestination.AddEdit.name}/$personId")
                        }
                    )
                }
                //composable(BenameDestination.Tags.name) { TagScreen(modifier = Modifier.padding(16.dp), onAddTag = { navController.navigate("AddEditTag") }) }
                composable(BenameDestination.Settings.name) {
                    SettingsScreen(
                        modifier = Modifier.padding(
                            16.dp
                        )
                    )
                }
                composable("AddEditTag") {
                    AddEditTagScreen(
                        modifier = Modifier,
                        onBack = { navController.navigateUp() })
                }

                //composable(BenameDestination.AddEdit.name) { AddEditPersonScreen(modifier = Modifier, onBack = { navController.navigateUp() }) }
                composable(
                    route = "${BenameDestination.AddEdit.name}/{personId}",
                    arguments = listOf(navArgument("personId") {
                        type = NavType.IntType; nullable = false
                    })
                ) { backStackEntry ->
                    val personId = backStackEntry.arguments?.getInt("personId")
                    AddEditPersonScreen(
                        personId = personId,
                        modifier = Modifier,
                        onBack = { navController.navigateUp() }
                    )
                }
                composable(BenameDestination.AddEdit.name) {
                    AddEditPersonScreen(
                        personId = null,
                        modifier = Modifier,
                        onBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

private fun getCurrentDestination(route: String?): BenameDestination {
    return BenameDestination.values().find { it.name == route } ?: BenameDestination.People
}

//preview bottom bars, including ad banner
@Preview(showBackground = true)
@Composable
fun BenameBottomBarPreview() {
    Column {
        BenameBottomBar(
            currentDestination = BenameDestination.People,
            onDestinationSelected = {}
        )
        // Fake ad banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your Ad Here! Upgrade to Premium to remove ads.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFF1976D2))
                    .padding(horizontal = 16.dp)
            )
        }
    }
}