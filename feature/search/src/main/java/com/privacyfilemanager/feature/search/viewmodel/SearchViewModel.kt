package com.privacyfilemanager.feature.search.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun toggleSearchContent() {
        _uiState.value = _uiState.value.copy(searchContent = !_uiState.value.searchContent)
        if (_uiState.value.query.isNotBlank()) onQueryChange(_uiState.value.query)
    }

    fun toggleSearchMetadata() {
        _uiState.value = _uiState.value.copy(searchMetadata = !_uiState.value.searchMetadata)
        if (_uiState.value.query.isNotBlank()) onQueryChange(_uiState.value.query)
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isLoading = false, error = null)
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(500) // Debounce
            
            val rootPath = Environment.getExternalStorageDirectory().absolutePath
            val state = _uiState.value
            when (val result = fileRepository.searchFiles(
                query = query, 
                rootPath = rootPath,
                searchContent = state.searchContent,
                searchMetadata = state.searchMetadata
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        results = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {}
            }
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val results: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchContent: Boolean = false,
    val searchMetadata: Boolean = false
)
