package com.bename.ui.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bename.R
import com.bename.data.local.database.Tag
import com.bename.ui.theme.MyApplicationTheme
import androidx.compose.material3.OutlinedTextField

@Composable
fun TagScreen(
    modifier: Modifier = Modifier,
    onAddTag: () -> Unit,
    onTagClick: (Tag) -> Unit,
    viewModel: TagViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    when (items) {
        is TagUiState.Success -> {
            TagScreen(
                items = (items as TagUiState.Success).data,
                onSave = viewModel::addTag,
                onAddTag = onAddTag,
                onTagClick = onTagClick,
                modifier = modifier
            )
        }
        is TagUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }
        is TagUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading tags")
            }
        }
    }
}

@Composable
internal fun TagScreen(
    items: List<Tag>,
    onSave: (tag: Tag) -> Unit,
    onAddTag: () -> Unit,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = if (searchQuery.isBlank()) items
    else items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    Column(
        modifier = modifier
            .fillMaxSize()
            //.padding(dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddTag) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Tag"
                )
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search tags") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        LazyColumn {
            items(filteredItems) { tag ->
                TagItem(
                    tag = tag,
                    onTagClick = { onTagClick(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider()
            }
        }
    }
}

@Composable
private fun TagItem(
    tag: Tag,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable {
                isExpanded = !isExpanded
                onTagClick(tag)
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = tag.locations.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isExpanded) {
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Person IDs: ${tag.personIds.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = stringResource(id = R.string.cd_arrow_forward)
        )
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun TagScreenPreview() {
    MyApplicationTheme {
        TagScreen(
            items = listOf(
                Tag(
                    name = "Work",
                    locations = listOf("Office", "Remote"),
                    personIds = listOf(1, 2)
                ),
                Tag(
                    name = "Family",
                    locations = listOf("Home"),
                    personIds = listOf(3)
                )
            ),
            onAddTag = {},
            onTagClick = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagItemPreview() {
    MyApplicationTheme {
        TagItem(
            tag = Tag(
                name = "Friends",
                locations = listOf("Cafe", "Park"),
                personIds = listOf(4, 5)
            ),
            onTagClick = {}
        )
    }
}