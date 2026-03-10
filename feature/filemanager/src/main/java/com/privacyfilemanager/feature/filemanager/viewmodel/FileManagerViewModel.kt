package com.privacyfilemanager.feature.filemanager.viewmodel

import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.core.database.dao.BookmarkDao
import com.privacyfilemanager.core.database.dao.RecentFileDao
import com.privacyfilemanager.core.database.entity.BookmarkEntity
import com.privacyfilemanager.core.database.entity.RecentFileEntity
import com.privacyfilemanager.core.domain.model.*
import com.privacyfilemanager.core.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fileRepository: FileRepository,
    private val bookmarkDao: BookmarkDao,
    private val recentFileDao: RecentFileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileManagerUiState())
    val uiState: StateFlow<FileManagerUiState> = _uiState.asStateFlow()

    private val _clipboard = MutableStateFlow(FileClipboard())
    val clipboard: StateFlow<FileClipboard> = _clipboard.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentFiles: StateFlow<List<RecentFileEntity>> = recentFileDao.getRecentFilesLimited(20)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        val startPath = savedStateHandle.get<String>("startPath")
        val initialPath = if (!startPath.isNullOrBlank() && java.io.File(startPath).isDirectory) {
            startPath
        } else {
            Environment.getExternalStorageDirectory().absolutePath
        }
        navigateTo(initialPath)
    }

    fun navigateTo(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = fileRepository.listFiles(
                directoryPath = path,
                showHidden = _uiState.value.showHidden,
                sortConfig = _uiState.value.sortConfig
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            currentPath = path,
                            files = result.data,
                            selectedFiles = emptySet(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun navigateUp(): Boolean {
        val current = _uiState.value.currentPath
        val root = Environment.getExternalStorageDirectory().absolutePath
        val currentFile = java.io.File(current)
        val rootFile = java.io.File(root)
        
        // BUG 5 FIX: Prevent ascending out of the external storage sandbox via symlink tricks
        if (current == root || current == "/" || !currentFile.canonicalPath.startsWith(rootFile.canonicalPath)) return false

        val parent = currentFile.parent ?: return false
        if (parent == "/" || !java.io.File(parent).canonicalPath.startsWith(rootFile.canonicalPath)) {
            navigateTo(root)
            return true
        }
        
        navigateTo(parent)
        return true
    }

    fun openFile(fileItem: FileItem) {
        if (fileItem.isDirectory) {
            navigateTo(fileItem.path)
        } else {
            // Log to recent files (local only)
            viewModelScope.launch {
                recentFileDao.upsert(
                    RecentFileEntity(
                        path = fileItem.path,
                        lastAccessed = System.currentTimeMillis(),
                        mimeType = fileItem.mimeType,
                        size = fileItem.size,
                        name = fileItem.name
                    )
                )
            }
            _uiState.update { it.copy(fileToOpen = fileItem) }
        }
    }

    fun clearFileToOpen() {
        _uiState.update { it.copy(fileToOpen = null) }
    }

    // ===== Selection =====

    fun toggleSelection(path: String) {
        _uiState.update {
            val newSelection = it.selectedFiles.toMutableSet()
            if (path in newSelection) newSelection.remove(path) else newSelection.add(path)
            it.copy(selectedFiles = newSelection)
        }
    }

    fun selectAll() {
        _uiState.update {
            it.copy(selectedFiles = it.files.map { f -> f.path }.toSet())
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFiles = emptySet()) }
    }

    // ===== Clipboard =====

    fun copySelected() {
        _clipboard.value = FileClipboard(
            files = _uiState.value.selectedFiles.toList(),
            operation = ClipboardOperation.COPY
        )
        clearSelection()
    }

    fun cutSelected() {
        _clipboard.value = FileClipboard(
            files = _uiState.value.selectedFiles.toList(),
            operation = ClipboardOperation.CUT
        )
        clearSelection()
    }

    fun paste() {
        val clip = _clipboard.value
        if (clip.files.isEmpty()) return

        viewModelScope.launch {
            val destination = _uiState.value.currentPath
            when (clip.operation) {
                ClipboardOperation.COPY -> {
                    fileRepository.copy(clip.files, destination).collect { progress ->
                        _uiState.update { it.copy(operationProgress = progress) }
                        if (progress.isComplete) {
                            // BUG 5 FIX: clear clipboard after COPY too, not just CUT
                            _clipboard.value = FileClipboard()
                            refreshCurrentDirectory()
                            _uiState.update { it.copy(operationProgress = null) }
                        }
                    }
                }
                ClipboardOperation.CUT -> {
                    fileRepository.move(clip.files, destination).collect { progress ->
                        _uiState.update { it.copy(operationProgress = progress) }
                        if (progress.isComplete) {
                            _clipboard.value = FileClipboard()
                            refreshCurrentDirectory()
                            _uiState.update { it.copy(operationProgress = null) }
                        }
                    }
                }
                ClipboardOperation.NONE -> {}
            }
        }
    }

    // ===== CRUD =====

    fun createNewFile(name: String) {
        viewModelScope.launch {
            try {
                fileRepository.createFile(_uiState.value.currentPath, name)
                refreshCurrentDirectory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun createNewFolder(name: String) {
        viewModelScope.launch {
            try {
                fileRepository.createDirectory(_uiState.value.currentPath, name)
                refreshCurrentDirectory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            try {
                fileRepository.delete(_uiState.value.selectedFiles.toList())
                clearSelection()
                refreshCurrentDirectory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun rename(path: String, newName: String) {
        viewModelScope.launch {
            try {
                fileRepository.rename(path, newName)
                refreshCurrentDirectory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // ===== Bookmarks =====

    fun toggleBookmark(path: String) {
        viewModelScope.launch {
            if (bookmarkDao.isBookmarked(path)) {
                bookmarkDao.deleteByPath(path)
            } else {
                bookmarkDao.insert(
                    BookmarkEntity(
                        path = path,
                        label = java.io.File(path).name
                    )
                )
            }
        }
    }

    // ===== Settings =====

    fun toggleHiddenFiles() {
        // BUG 6 FIX: capture new value first to avoid race condition between update and refresh
        val newShowHidden = !_uiState.value.showHidden
        _uiState.update { it.copy(showHidden = newShowHidden) }
        viewModelScope.launch {
            when (val result = fileRepository.listFiles(
                directoryPath = _uiState.value.currentPath,
                showHidden = newShowHidden,
                sortConfig = _uiState.value.sortConfig
            )) {
                is com.privacyfilemanager.core.common.util.Result.Success ->
                    _uiState.update { it.copy(files = result.data, isLoading = false, error = null) }
                is com.privacyfilemanager.core.common.util.Result.Error ->
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun setSortConfig(sortConfig: SortConfig) {
        _uiState.update { it.copy(sortConfig = sortConfig) }
        refreshCurrentDirectory()
    }

    fun toggleViewMode() {
        _uiState.update {
            it.copy(
                viewMode = if (it.viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            )
        }
    }

    private fun refreshCurrentDirectory() {
        navigateTo(_uiState.value.currentPath)
    }
}

data class FileManagerUiState(
    val currentPath: String = "",
    val files: List<FileItem> = emptyList(),
    val selectedFiles: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showHidden: Boolean = false,
    val sortConfig: SortConfig = SortConfig(),
    val viewMode: ViewMode = ViewMode.LIST,
    val fileToOpen: FileItem? = null,
    val operationProgress: com.privacyfilemanager.core.domain.repository.CopyProgress? = null,
    val storageInfo: com.privacyfilemanager.core.domain.repository.StorageInfo? = null // #16
)
