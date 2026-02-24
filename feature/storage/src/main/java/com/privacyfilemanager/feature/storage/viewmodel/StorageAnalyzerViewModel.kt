package com.privacyfilemanager.feature.storage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.StorageStats
import com.privacyfilemanager.core.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageAnalyzerViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageAnalyzerUiState())
    val uiState: StateFlow<StorageAnalyzerUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        loadLargeFiles()
        loadDuplicateFiles()
        loadJunkFiles()
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
