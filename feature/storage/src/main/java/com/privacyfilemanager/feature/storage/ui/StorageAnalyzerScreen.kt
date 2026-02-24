package com.privacyfilemanager.feature.storage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.core.common.util.FileUtils
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    onNavigateBack: () -> Unit,
    viewModel: StorageAnalyzerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Large Files", "Duplicates", "Junk")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Analyzer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> OverviewTab(uiState)
                    1 -> LargeFilesTab(uiState)
                    2 -> DuplicatesTab(uiState)
                    3 -> JunkTab(uiState)
                }
            }
        }
    }
}

@Composable
fun OverviewTab(uiState: com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerUiState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            uiState.stats != null -> {
                val stats = uiState.stats
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Space: ${FileUtils.formatFileSize(stats.totalBytes)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Used Space: ${FileUtils.formatFileSize(stats.usedBytes)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Free Space: ${FileUtils.formatFileSize(stats.freeBytes)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val progress = if (stats.totalBytes > 0) {
                                stats.usedBytes.toFloat() / stats.totalBytes.toFloat()
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(16.dp),
                                color = MaterialTheme.colorScheme.error,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeFilesTab(uiState: com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isLargeFilesLoading) {
            CircularProgressIndicator()
        } else if (uiState.largeFiles.isEmpty()) {
            Text("No large files found")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.largeFiles) { file ->
                    FileListItem(file = file)
                }
            }
        }
    }
}

@Composable
fun DuplicatesTab(uiState: com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isDuplicatesLoading) {
            CircularProgressIndicator()
        } else if (uiState.duplicateFiles.isEmpty()) {
            Text("No duplicate files found")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                uiState.duplicateFiles.forEachIndexed { index, group ->
                    item {
                        Text(
                            text = "Group ${index + 1} (${FileUtils.formatFileSize(group.first().size)} each)",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(group) { file ->
                        FileListItem(file = file)
                    }
                    item { HorizontalDivider() }
                }
            }
        }
    }
}

@Composable
fun JunkTab(uiState: com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isJunkLoading) {
            CircularProgressIndicator()
        } else if (uiState.junkFiles.isEmpty()) {
            Text("No junk files found")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.junkFiles) { file ->
                    FileListItem(file = file)
                }
            }
        }
    }
}

@Composable
fun FileListItem(file: FileItem) {
    ListItem(
        headlineContent = { Text(file.name) },
        supportingContent = { Text("${FileUtils.formatFileSize(file.size)} • ${file.path}") },
        leadingContent = {
            Icon(
                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}
