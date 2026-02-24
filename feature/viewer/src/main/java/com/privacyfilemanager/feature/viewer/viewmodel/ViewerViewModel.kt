package com.privacyfilemanager.feature.viewer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.common.util.FileCategory
import com.privacyfilemanager.core.common.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pathString: String = savedStateHandle.get<String>("path") ?: ""
    private val file = File(pathString)
    
    val category: FileCategory = FileUtils.getFileCategory(file)
    val fileName: String = file.name

    private val _uiState = MutableStateFlow(ViewerUiState())
    val uiState: StateFlow<ViewerUiState> = _uiState.asStateFlow()

    init {
        loadFile()
    }

    private fun loadFile() {
        if (!file.exists() || !file.canRead()) {
            _uiState.value = _uiState.value.copy(error = "File does not exist or cannot be read")
            return
        }

        when (category) {
            FileCategory.TEXT, FileCategory.CODE -> loadTextFile()
            else -> {
                // Image/Video/Audio/PDF handled by UI with file path
                _uiState.value = _uiState.value.copy(file = file)
            }
        }
    }

    private fun loadTextFile() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Prevent loading huge files into memory
                if (file.length() > 2 * 1024 * 1024) { 
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "File is too large to preview (>2MB)."
                    )
                    return@launch
                }
                
                val content = file.readText()
                _uiState.value = _uiState.value.copy(
                    textContent = content,
                    isLoading = false,
                    file = file
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to read text file"
                )
            }
        }
    }
}

data class ViewerUiState(
    val file: File? = null,
    val textContent: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
