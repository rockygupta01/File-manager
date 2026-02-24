package com.privacyfilemanager.feature.devtools.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevToolsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DevToolsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dev Tools & AI") },
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
            TabRow(selectedTabIndex = uiState.activeTab) {
                Tab(selected = uiState.activeTab == 0, onClick = { viewModel.setTab(0) },
                    text = { Text("Terminal") }, icon = { Icon(Icons.Default.Terminal, null) })
                Tab(selected = uiState.activeTab == 1, onClick = { viewModel.setTab(1) },
                    text = { Text("OCR / AI") }, icon = { Icon(Icons.Default.DocumentScanner, null) })
            }

            when (uiState.activeTab) {
                0 -> TerminalTab(uiState = uiState, viewModel = viewModel)
                1 -> OcrTab(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun ColumnScope.TerminalTab(
    uiState: com.privacyfilemanager.feature.devtools.viewmodel.DevToolsUiState,
    viewModel: DevToolsViewModel
) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.terminalHistory.size) {
        if (uiState.terminalHistory.isNotEmpty()) {
            listState.animateScrollToItem(uiState.terminalHistory.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(Color(0xFF0D0D0D))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text("$ Shell ready (non-root user shell)", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color(0xFF29B6F6))
        }
        items(uiState.terminalHistory) { (cmd, output) ->
            Column {
                Text("$ $cmd", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color(0xFF29B6F6))
                Text(output, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFFE0E0E0))
            }
        }
        if (uiState.isTerminalLoading) {
            item { Text("…", fontFamily = FontFamily.Monospace, color = Color(0xFF29B6F6)) }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$ ", fontFamily = FontFamily.Monospace, color = Color(0xFF29B6F6))
        OutlinedTextField(
            value = uiState.currentCommand,
            onValueChange = viewModel::onCommandChange,
            placeholder = { Text("Enter command…", fontFamily = FontFamily.Monospace) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { viewModel.runCommand() })
        )
        IconButton(onClick = viewModel::runCommand, enabled = !uiState.isTerminalLoading) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Run", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun OcrTab(
    uiState: com.privacyfilemanager.feature.devtools.viewmodel.DevToolsUiState,
    viewModel: DevToolsViewModel
) {
    var pathInput by remember { mutableStateOf(uiState.imagePath) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "On-Device OCR",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Extract text from images using Google ML Kit, entirely on-device. No data is sent to the cloud.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = pathInput,
            onValueChange = { pathInput = it; viewModel.setImagePath(it) },
            label = { Text("Image file path") },
            placeholder = { Text("/storage/emulated/0/Pictures/photo.jpg") },
            leadingIcon = { Icon(Icons.Default.Image, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = viewModel::runOcr,
                enabled = uiState.imagePath.isNotBlank() && !uiState.isOcrLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isOcrLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.TextFields, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Extract Text")
                }
            }
            OutlinedButton(onClick = { viewModel.clearOcr(); pathInput = "" }) {
                Icon(Icons.Default.Clear, null)
                Spacer(Modifier.width(6.dp))
                Text("Clear")
            }
        }

        uiState.ocrError?.let { err ->
            Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        if (uiState.ocrText.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Extracted Text", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.ocrText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
