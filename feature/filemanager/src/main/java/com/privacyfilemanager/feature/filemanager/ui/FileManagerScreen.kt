package com.privacyfilemanager.feature.filemanager.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.privacyfilemanager.core.common.util.FileCategory
import com.privacyfilemanager.core.common.util.FileUtils
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.ViewMode
import com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel = hiltViewModel(),
    onNavigateToStorage: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToArchive: (List<String>, String) -> Unit = { _, _ -> },
    onNavigateToViewer: (String) -> Unit = {},
    onNavigateToAppManager: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {},
    onNavigateToLan: () -> Unit = {},
    onNavigateToRoot: () -> Unit = {},
    onNavigateToDevTools: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard by viewModel.clipboard.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (uiState.selectedFiles.isNotEmpty()) {
                // Selection mode top bar
                TopAppBar(
                    title = { Text("${uiState.selectedFiles.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            onNavigateToArchive(uiState.selectedFiles.toList(), "compress")
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Default.FolderZip, "Compress")
                        }
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(Icons.Default.SelectAll, "Select all")
                        }
                        IconButton(onClick = { viewModel.copySelected() }) {
                            Icon(Icons.Default.ContentCopy, "Copy")
                        }
                        IconButton(onClick = { viewModel.cutSelected() }) {
                            Icon(Icons.Default.ContentCut, "Cut")
                        }
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                // Normal top bar
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = java.io.File(uiState.currentPath).name.ifEmpty { "Storage" },
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = uiState.currentPath,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate up")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToStorage) {
                            Icon(Icons.Default.PieChart, "Storage Analyzer")
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Security, "Security & Vault")
                        }
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, "Search")
                        }
                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                if (uiState.viewMode == ViewMode.LIST) Icons.Default.GridView
                                else Icons.Default.ViewList,
                                "Toggle view"
                            )
                        }
                        IconButton(onClick = { showSortMenu = !showSortMenu }) {
                            Icon(Icons.Default.SortByAlpha, "Sort")
                        }
                        IconButton(onClick = { viewModel.toggleHiddenFiles() }) {
                            Icon(
                                if (uiState.showHidden) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                "Toggle hidden"
                            )
                        }
                        var showMoreMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Default.MoreVert, "More")
                            }
                            DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("App Manager") },
                                    leadingIcon = { Icon(Icons.Default.Android, null) },
                                    onClick = { showMoreMenu = false; onNavigateToAppManager() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Automation") },
                                    leadingIcon = { Icon(Icons.Default.Schedule, null) },
                                    onClick = { showMoreMenu = false; onNavigateToAutomation() }
                                )
                                DropdownMenuItem(
                                    text = { Text("LAN Transfer") },
                                    leadingIcon = { Icon(Icons.Default.Wifi, null) },
                                    onClick = { showMoreMenu = false; onNavigateToLan() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Root Terminal") },
                                    leadingIcon = { Icon(Icons.Default.Terminal, null) },
                                    onClick = { showMoreMenu = false; onNavigateToRoot() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Dev Tools & AI") },
                                    leadingIcon = { Icon(Icons.Default.Code, null) },
                                    onClick = { showMoreMenu = false; onNavigateToDevTools() }
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Paste FAB (when clipboard has content)
                AnimatedVisibility(
                    visible = clipboard.files.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = { viewModel.paste() },
                        modifier = Modifier.padding(bottom = 8.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(Icons.Default.ContentPaste, "Paste")
                    }
                }

                // Create FAB
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, "Create")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                uiState.files.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Empty folder",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    if (uiState.viewMode == ViewMode.LIST) {
                        FileListView(
                            files = uiState.files,
                            selectedFiles = uiState.selectedFiles,
                            onFileClick = { 
                                when (it.category) {
                                    FileCategory.ARCHIVE -> onNavigateToArchive(listOf(it.path), "extract")
                                    FileCategory.IMAGE, FileCategory.VIDEO, FileCategory.AUDIO, 
                                    FileCategory.PDF, FileCategory.TEXT, FileCategory.CODE -> {
                                        onNavigateToViewer(it.path)
                                    }
                                    else -> viewModel.openFile(it)
                                }
                            },
                            onFileLongClick = { viewModel.toggleSelection(it.path) }
                        )
                    } else {
                        FileGridView(
                            files = uiState.files,
                            selectedFiles = uiState.selectedFiles,
                            onFileClick = { 
                                when (it.category) {
                                    FileCategory.ARCHIVE -> onNavigateToArchive(listOf(it.path), "extract")
                                    FileCategory.IMAGE, FileCategory.VIDEO, FileCategory.AUDIO, 
                                    FileCategory.PDF, FileCategory.TEXT, FileCategory.CODE -> {
                                        onNavigateToViewer(it.path)
                                    }
                                    else -> viewModel.openFile(it)
                                }
                            },
                            onFileLongClick = { viewModel.toggleSelection(it.path) }
                        )
                    }
                }
            }

            // Operation progress
            uiState.operationProgress?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress.bytesProcessed.toFloat() / progress.totalBytes.coerceAtLeast(1).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Create file/folder dialog
    if (showCreateDialog) {
        CreateDialog(
            onDismiss = { showCreateDialog = false },
            onCreateFile = { name ->
                viewModel.createNewFile(name)
                showCreateDialog = false
            },
            onCreateFolder = { name ->
                viewModel.createNewFolder(name)
                showCreateDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileListView(
    files: List<FileItem>,
    selectedFiles: Set<String>,
    onFileClick: (FileItem) -> Unit,
    onFileLongClick: (FileItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(files, key = { it.path }) { file ->
            val isSelected = file.path in selectedFiles

            ListItem(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) {
                                onFileLongClick(file)
                            } else {
                                onFileClick(file)
                            }
                        },
                        onLongClick = { onFileLongClick(file) }
                    )
                    .then(
                        if (isSelected) Modifier else Modifier
                    ),
                headlineContent = {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (file.isDirectory) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = if (file.isDirectory) {
                            "${file.childCount} items"
                        } else {
                            "${FileUtils.formatFileSize(file.size)} · ${FileUtils.formatDate(file.lastModified)}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = getFileIcon(file.category),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                        else getIconTint(file.category),
                        modifier = Modifier.size(40.dp)
                    )
                },
                trailingContent = {
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileGridView(
    files: List<FileItem>,
    selectedFiles: Set<String>,
    onFileClick: (FileItem) -> Unit,
    onFileLongClick: (FileItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(files, key = { it.path }) { file ->
            val isSelected = file.path in selectedFiles

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) onFileLongClick(file)
                            else onFileClick(file)
                        },
                        onLongClick = { onFileLongClick(file) }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getFileIcon(file.category),
                        contentDescription = null,
                        tint = getIconTint(file.category),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateDialog(
    onDismiss: () -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isFolder by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isFolder) "New Folder" else "New File") },
        text = {
            Column {
                Row {
                    FilterChip(
                        selected = isFolder,
                        onClick = { isFolder = true },
                        label = { Text("Folder") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = !isFolder,
                        onClick = { isFolder = false },
                        label = { Text("File") }
                    )
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        if (isFolder) onCreateFolder(name) else onCreateFile(name)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun getFileIcon(category: FileCategory): ImageVector = when (category) {
    FileCategory.FOLDER -> Icons.Default.Folder
    FileCategory.IMAGE -> Icons.Default.Image
    FileCategory.VIDEO -> Icons.Default.Videocam
    FileCategory.AUDIO -> Icons.Default.MusicNote
    FileCategory.PDF -> Icons.Default.PictureAsPdf
    FileCategory.DOCUMENT -> Icons.Default.Description
    FileCategory.SPREADSHEET -> Icons.Default.TableChart
    FileCategory.PRESENTATION -> Icons.Default.Slideshow
    FileCategory.TEXT -> Icons.Default.TextSnippet
    FileCategory.CODE -> Icons.Default.Code
    FileCategory.ARCHIVE -> Icons.Default.FolderZip
    FileCategory.APK -> Icons.Default.Android
    FileCategory.OTHER -> Icons.AutoMirrored.Filled.InsertDriveFile
}

@Composable
private fun getIconTint(category: FileCategory) = when (category) {
    FileCategory.FOLDER -> MaterialTheme.colorScheme.primary
    FileCategory.IMAGE -> MaterialTheme.colorScheme.tertiary
    FileCategory.VIDEO -> MaterialTheme.colorScheme.error
    FileCategory.AUDIO -> MaterialTheme.colorScheme.secondary
    FileCategory.PDF -> MaterialTheme.colorScheme.error
    FileCategory.ARCHIVE -> MaterialTheme.colorScheme.tertiary
    FileCategory.APK -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}
