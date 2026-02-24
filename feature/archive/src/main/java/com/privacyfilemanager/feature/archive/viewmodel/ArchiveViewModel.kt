package com.privacyfilemanager.feature.archive.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.feature.archive.domain.ArchiveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val archiveManager: ArchiveManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Expecting "paths" argument (comma separated) and "mode" ("compress" or "extract")
    private val pathsString: String = savedStateHandle.get<String>("paths") ?: ""
    private val paths: List<String> = if (pathsString.isNotEmpty()) pathsString.split(",") else emptyList()
    val mode: String = savedStateHandle.get<String>("mode") ?: "compress"

    private val _uiState = MutableStateFlow(ArchiveUiState(paths = paths))
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateArchiveName(name: String) {
        _uiState.value = _uiState.value.copy(archiveName = name)
    }

    fun startOperation() {
        if (paths.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "No files selected")
            return
        }

        val currentState = _uiState.value
        val password = currentState.password.takeIf { it.isNotBlank() }

        if (mode == "compress") {
            val defaultDest = paths.first().substringBeforeLast("/") + "/" + 
                    (currentState.archiveName.takeIf { it.isNotBlank() } ?: "archive.zip")
            
            viewModelScope.launch {
                archiveManager.compressFiles(paths, defaultDest, password).collect { result ->
                    handleResult(result)
                }
            }
        } else {
            // Extract
            val zipFile = paths.first()
            val destFolder = zipFile.substringBeforeLast(".") // e.g., /path/to/archive.zip -> /path/to/archive
            
            viewModelScope.launch {
                archiveManager.extractArchive(zipFile, destFolder, password).collect { result ->
                    handleResult(result)
                }
            }
        }
    }

    private fun handleResult(result: Result<String>) {
        when (result) {
            is Result.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
            is Result.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            is Result.Success -> _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
        }
    }
}

data class ArchiveUiState(
    val paths: List<String> = emptyList(),
    val password: String = "",
    val archiveName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
