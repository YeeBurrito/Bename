// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.ui.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bename.R

// Top app bar for the Add/Edit Person screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonTopAppBar(
    title: String, // Title to display in the app bar
    onBack: () -> Unit, // Callback for back button press
    actions: @Composable RowScope.() -> Unit = {} // Optional actions in the app bar
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            // Back button icon
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = actions
    )
}