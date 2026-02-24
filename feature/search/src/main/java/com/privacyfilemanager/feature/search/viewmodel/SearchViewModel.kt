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

    // #19 Toggle MIME type category filter (tap again to deselect)
    fun setMimeFilter(filter: MimeFilter) {
        val newFilter = if (_uiState.value.mimeFilter == filter) MimeFilter.ALL else filter
        _uiState.value = _uiState.value.copy(mimeFilter = newFilter)
        if (_uiState.value.query.isNotBlank()) onQueryChange(_uiState.value.query)
    }

    // #18 Remove a recent search suggestion
    fun removeRecentSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            recentSearches = _uiState.value.recentSearches - query
        )
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
            delay(500)
            val rootPath = Environment.getExternalStorageDirectory().absolutePath
            val state = _uiState.value
            val mimeType = when (state.mimeFilter) {
                MimeFilter.IMAGES -> "image/"
                MimeFilter.VIDEOS -> "video/"
                MimeFilter.AUDIO  -> "audio/"
                MimeFilter.DOCS   -> "application/"
                MimeFilter.ALL    -> null
            }
            when (val result = fileRepository.searchFiles(
                query = query,
                rootPath = rootPath,
                mimeTypeFilter = mimeType,
                searchContent = state.searchContent,
                searchMetadata = state.searchMetadata
            )) {
                is Result.Success -> {
                    val updated = (listOf(query) + state.recentSearches).distinct().take(5)
                    _uiState.value = _uiState.value.copy(
                        results = result.data,
                        isLoading = false,
                        recentSearches = updated
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> {}
            }
        }
    }
}

enum class MimeFilter { ALL, IMAGES, VIDEOS, AUDIO, DOCS }

data class SearchUiState(
    val query: String = "",
    val results: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchContent: Boolean = false,
    val searchMetadata: Boolean = false,
    val mimeFilter: MimeFilter = MimeFilter.ALL,
    val recentSearches: List<String> = emptyList()
)
