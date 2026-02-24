package com.privacyfilemanager.feature.search.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.feature.search.viewmodel.MimeFilter
import com.privacyfilemanager.feature.search.viewmodel.SearchViewModel

/**
 * #18 Recent searches: shown when query is empty
 * #19 Filter chips: Images / Videos / Audio / Docs below the search bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("Search files...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        trailingIcon = {
                            if (uiState.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // ── #19 Type filter chips ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = uiState.mimeFilter == MimeFilter.IMAGES, onClick = { viewModel.setMimeFilter(MimeFilter.IMAGES) }, label = { Text("🖼 Images") })
                FilterChip(selected = uiState.mimeFilter == MimeFilter.VIDEOS, onClick = { viewModel.setMimeFilter(MimeFilter.VIDEOS) }, label = { Text("🎬 Videos") })
                FilterChip(selected = uiState.mimeFilter == MimeFilter.AUDIO,  onClick = { viewModel.setMimeFilter(MimeFilter.AUDIO) },  label = { Text("🎵 Audio") })
                FilterChip(selected = uiState.mimeFilter == MimeFilter.DOCS,   onClick = { viewModel.setMimeFilter(MimeFilter.DOCS) },   label = { Text("📄 Docs") })
                FilterChip(selected = uiState.searchContent,   onClick = { viewModel.toggleSearchContent() },   label = { Text("Content") })
                FilterChip(selected = uiState.searchMetadata,  onClick = { viewModel.toggleSearchMetadata() },  label = { Text("Metadata") })
            }

            HorizontalDivider()

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    // ── #18 Recent searches (shown when query is empty) ────────
                    uiState.query.isEmpty() && uiState.recentSearches.isNotEmpty() -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    "Recent",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(uiState.recentSearches) { recent ->
                                ListItem(
                                    modifier = Modifier.clickable { viewModel.onQueryChange(recent) },
                                    headlineContent = { Text(recent) },
                                    leadingContent = {
                                        Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    },
                                    trailingContent = {
                                        IconButton(onClick = { viewModel.removeRecentSearch(recent) }) {
                                            Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    uiState.error != null -> {
                        Text(
                            "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(32.dp)
                        )
                    }

                    uiState.results.isEmpty() && uiState.query.isNotEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(72.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Nothing found for \"${uiState.query}\"", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text("Try a different name or enable Content / Metadata search", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                            item {
                                if (uiState.results.isNotEmpty()) {
                                    Text(
                                        "${uiState.results.size} result${if (uiState.results.size != 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                            items(uiState.results, key = { it.path }) { file ->
                                ListItem(
                                    modifier = Modifier.clickable { /* open file */ },
                                    headlineContent = {
                                        Text(file.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    supportingContent = {
                                        Text(file.path, style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
