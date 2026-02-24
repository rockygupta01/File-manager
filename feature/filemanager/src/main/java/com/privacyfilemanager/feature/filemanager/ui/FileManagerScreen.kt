package com.privacyfilemanager.feature.filemanager.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import android.net.Uri
import java.io.File
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
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val rootPath = android.os.Environment.getExternalStorageDirectory().absolutePath
    val isAtRoot = uiState.currentPath == rootPath || uiState.currentPath.isEmpty()

    // fileToOpen: cleared immediately after first use
    val fileToOpen = uiState.fileToOpen
    androidx.compose.runtime.LaunchedEffect(fileToOpen) {
        fileToOpen?.let { viewModel.clearFileToOpen() }
    }

    // #7: System back press navigates up in file tree, not out of app
    androidx.activity.compose.BackHandler(enabled = !isAtRoot) {
        viewModel.navigateUp()
    }

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
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
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
                            // #5 Typography hierarchy: bold title, dimmed small subtitle
                            Text(
                                text = if (uiState.currentPath.isEmpty()) "Storage"
                                       else java.io.File(uiState.currentPath).name.ifEmpty { "Storage" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            if (uiState.currentPath.isNotEmpty()) {
                                Text(
                                    text = uiState.currentPath,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        // BUG 4 FIX: show home icon at root, back arrow only in subfolders
                        val rootPath = android.os.Environment.getExternalStorageDirectory().absolutePath
                        val isAtRoot = uiState.currentPath == rootPath || uiState.currentPath.isEmpty()
                        if (isAtRoot) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        } else {
                            IconButton(onClick = { viewModel.navigateUp() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate up")
                            }
                        }
                    },
                    actions = {
                        // ── Core actions (always visible, equal spacing) ──
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                imageVector = if (uiState.viewMode == ViewMode.LIST)
                                    Icons.Default.GridView else Icons.Default.ViewList,
                                contentDescription = "Toggle view"
                            )
                        }
                        IconButton(onClick = { showSortMenu = !showSortMenu }) {
                            Icon(Icons.Default.SortByAlpha, contentDescription = "Sort")
                        }

                        // ── Overflow menu ──
                        var showMoreMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                // Quick toggles
                                DropdownMenuItem(
                                    text = { Text(if (uiState.showHidden) "Hide Hidden Files" else "Show Hidden Files") },
                                    leadingIcon = {
                                        Icon(
                                            if (uiState.showHidden) Icons.Default.VisibilityOff
                                            else Icons.Default.Visibility,
                                            null
                                        )
                                    },
                                    onClick = { showMoreMenu = false; viewModel.toggleHiddenFiles() }
                                )
                                HorizontalDivider()
                                // Navigation items
                                DropdownMenuItem(
                                    text = { Text("Storage Analyzer") },
                                    leadingIcon = { Icon(Icons.Default.PieChart, null) },
                                    onClick = { showMoreMenu = false; onNavigateToStorage() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Security & Vault") },
                                    leadingIcon = { Icon(Icons.Default.Security, null) },
                                    onClick = { showMoreMenu = false; onNavigateToSettings() }
                                )
                                DropdownMenuItem(
                                    text = { Text("App Manager") },
                                    leadingIcon = { Icon(Icons.Default.Android, null) },
                                    onClick = { showMoreMenu = false; onNavigateToAppManager() }
                                )
                                HorizontalDivider()
                                // Advanced tools
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
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // #14 Paste destination banner
            if (clipboard.files.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (clipboard.operation == com.privacyfilemanager.core.domain.model.ClipboardOperation.CUT)
                                Icons.Default.ContentCut else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        val opWord = if (clipboard.operation == com.privacyfilemanager.core.domain.model.ClipboardOperation.CUT) "cut" else "copied"
                        val count = clipboard.files.size
                        Text(
                            text = "$count file${if (count > 1) "s" else ""} $opWord — navigate to destination then tap Paste",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            // #6 Breadcrumb navigation bar
            if (uiState.currentPath.isNotEmpty()) {
                BreadcrumbBar(
                    currentPath = uiState.currentPath,
                    onSegmentClick = { viewModel.navigateTo(it) }
                )
            }
            Box(modifier = Modifier.weight(1f)) {

            when {
                uiState.isLoading -> {
                    // #12 Shimmer skeleton instead of spinner
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(10) {
                            ShimmerListItem()
                        }
                    }
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
                    // #2 Custom empty state
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "This folder is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tap + to create a new file or folder",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
            } // end Box weight(1f)
        } // end Column
    } // end Scaffold

    // #11 Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete ${uiState.selectedFiles.size} item${if (uiState.selectedFiles.size > 1) "s" else ""}?") },
            text = { Text("This action cannot be undone. Selected files will be permanently deleted from your device.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteSelected(); showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

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
        contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp) // #25 FAB bottom padding
    ) {
        items(files, key = { it.path }) { file ->
            val isSelected = file.path in selectedFiles
            val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current

            ListItem(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) onFileLongClick(file)
                            else onFileClick(file)
                        },
                        onLongClick = {
                            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress) // #10
                            onFileLongClick(file)
                        }
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
                            if (file.childCount >= 0) "${file.childCount} items" else "Folder"
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
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 96.dp), // #25 FAB padding
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(files, key = { it.path }) { file ->
            val isSelected = file.path in selectedFiles
            val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
            // #3 Color-coded card background by category
            val cardColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else when (file.category) {
                FileCategory.IMAGE, FileCategory.VIDEO -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                FileCategory.AUDIO -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                FileCategory.PDF -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                FileCategory.DOCUMENT, FileCategory.SPREADSHEET, FileCategory.PRESENTATION -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                FileCategory.FOLDER -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) onFileLongClick(file)
                            else onFileClick(file)
                        },
                        onLongClick = {
                            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress) // #10
                            onFileLongClick(file)
                        }
                    ),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Show real thumbnail for images and videos
                    if (file.category == FileCategory.IMAGE || file.category == FileCategory.VIDEO) {
                        val context = LocalContext.current
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.fromFile(File(file.path)))
                                .size(Size(200, 200))
                                .build(),
                            contentDescription = file.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            when (painter.state) {
                                is coil3.compose.AsyncImagePainter.State.Success -> {
                                    androidx.compose.foundation.Image(
                                        painter = painter,
                                        contentDescription = file.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getFileIcon(file.category),
                                            contentDescription = null,
                                            tint = getIconTint(file.category),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getFileIcon(file.category),
                                contentDescription = null,
                                tint = getIconTint(file.category),
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                    // #4 Size + date subtitle under filename
                    if (!file.isDirectory) {
                        Text(
                            text = "${FileUtils.formatFileSize(file.size)}  ·  ${FileUtils.formatDate(file.lastModified)}",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 6.dp)
                        )
                    }
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
