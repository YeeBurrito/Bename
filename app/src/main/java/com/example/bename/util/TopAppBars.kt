@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bename.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview
import com.example.bename.R

@Composable
fun AddEditPersonTopAppBar(
    @StringRes title: Int,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.cd_back))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PeopleTopAppBar(
    openDrawer: () -> Unit,
    onAddPerson: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.cd_back))
            }
        },
        actions = {
            IconButton(onClick = onAddPerson) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.cd_add_person))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun AddEditPersonTopAppBarPreview() {
    AddEditPersonTopAppBar(
        title = R.string.add_person,
        onBack = {}
    )
}