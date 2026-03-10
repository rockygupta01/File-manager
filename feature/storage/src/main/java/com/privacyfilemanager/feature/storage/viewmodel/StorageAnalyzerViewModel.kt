package com.privacyfilemanager.feature.storage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.StorageStats
import com.privacyfilemanager.core.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StorageAnalyzerViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageAnalyzerUiState())
    val uiState: StateFlow<StorageAnalyzerUiState> = _uiState.asStateFlow()

    init {
        // BUG 7 FIX: Stagger intensive parallel storage scans to avoid overloading device I/O entirely
        viewModelScope.launch {
            loadStats()
            kotlinx.coroutines.delay(250)
            loadLargeFiles()
            kotlinx.coroutines.delay(250)
            loadDuplicateFiles()
            kotlinx.coroutines.delay(250)
            loadJunkFiles()
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            loadStats()
            kotlinx.coroutines.delay(250)
            loadLargeFiles()
            kotlinx.coroutines.delay(250)
            loadDuplicateFiles()
            kotlinx.coroutines.delay(250)
            loadJunkFiles()
        }
    }

    /** Delete a single file and remove it from the appropriate list. */
    fun deleteFile(file: FileItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try { File(file.path).delete() } catch (_: Exception) {}
            }
            _uiState.value = _uiState.value.copy(
                largeFiles = _uiState.value.largeFiles.filter { it.path != file.path },
                junkFiles = _uiState.value.junkFiles.filter { it.path != file.path },
                duplicateFiles = _uiState.value.duplicateFiles.map { group ->
                    group.filter { it.path != file.path }
                }.filter { it.isNotEmpty() }
            )
        }
    }

    /** Delete all junk files. */
    fun deleteAllJunk() {
        viewModelScope.launch {
            val junk = _uiState.value.junkFiles
            withContext(Dispatchers.IO) {
                junk.forEach { try { File(it.path).delete() } catch (_: Exception) {} }
            }
            _uiState.value = _uiState.value.copy(junkFiles = emptyList())
        }
    }

    /** Delete duplicates in a group, keeping the first file. */
    fun deleteDuplicatesKeepFirst(groupIndex: Int) {
        viewModelScope.launch {
            val group = _uiState.value.duplicateFiles.getOrNull(groupIndex) ?: return@launch
            val toDelete = group.drop(1) // keep first
            withContext(Dispatchers.IO) {
                toDelete.forEach { try { File(it.path).delete() } catch (_: Exception) {} }
            }
            val updated = _uiState.value.duplicateFiles.toMutableList()
            updated.removeAt(groupIndex)
            _uiState.value = _uiState.value.copy(duplicateFiles = updated)
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            storageRepository.getStorageStats().collect { stats ->
                _uiState.value = _uiState.value.copy(
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }

    private fun loadLargeFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLargeFilesLoading = true)
            storageRepository.getLargeFiles().collect { files ->
                _uiState.value = _uiState.value.copy(
                    largeFiles = files,
                    isLargeFilesLoading = false
                )
            }
        }
    }

    private fun loadDuplicateFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDuplicatesLoading = true)
            storageRepository.getDuplicateFiles().collect { files ->
                _uiState.value = _uiState.value.copy(
                    duplicateFiles = files,
                    isDuplicatesLoading = false
                )
            }
        }
    }

    private fun loadJunkFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJunkLoading = true)
            storageRepository.getJunkFiles().collect { files ->
                _uiState.value = _uiState.value.copy(
                    junkFiles = files,
                    isJunkLoading = false
                )
            }
        }
    }
}

data class StorageAnalyzerUiState(
    val stats: StorageStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val largeFiles: List<FileItem> = emptyList(),
    val isLargeFilesLoading: Boolean = false,
    val duplicateFiles: List<List<FileItem>> = emptyList(),
    val isDuplicatesLoading: Boolean = false,
    val junkFiles: List<FileItem> = emptyList(),
    val isJunkLoading: Boolean = false
)
