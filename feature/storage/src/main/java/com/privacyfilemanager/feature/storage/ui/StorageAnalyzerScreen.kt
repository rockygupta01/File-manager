package com.privacyfilemanager.feature.storage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.core.common.util.FileUtils
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    onNavigateBack: () -> Unit,
    viewModel: StorageAnalyzerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
                uiState.stats != null -> {
                    val stats = uiState.stats!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
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
}
