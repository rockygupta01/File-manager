package com.privacyfilemanager.feature.appmanager.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.feature.appmanager.domain.AppItem
import com.privacyfilemanager.feature.appmanager.domain.AppManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val repository: AppManagerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppManagerUiState())
    val uiState: StateFlow<AppManagerUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps(includeSystem: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showSystemApps = includeSystem)
            repository.getInstalledApps(includeSystem).collect { apps ->
                _uiState.value = _uiState.value.copy(
                    apps = apps,
                    isLoading = false
                )
            }
        }
    }

    fun toggleSystemApps() {
        val current = _uiState.value.showSystemApps
        loadApps(!current)
    }

    fun backupApp(app: AppItem) {
        viewModelScope.launch {
            val destDir = File(Environment.getExternalStorageDirectory(), "AppBackups")
            _uiState.value = _uiState.value.copy(backupProgressMsg = "Backing up ${app.name}...")
            
            repository.backupApp(app, destDir).collect { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            backupProgressMsg = null,
                            toastMessage = "Backed up to ${result.data}"
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            backupProgressMsg = null,
                            toastMessage = "Backup failed: ${result.message}"
                        )
                    }
                }
            }
        }
    }
    
    fun clearToastMessage() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
    
    fun getUninstallIntent(packageName: String) = repository.getUninstallIntent(packageName)
}

data class AppManagerUiState(
    val apps: List<AppItem> = emptyList(),
    val isLoading: Boolean = false,
    val showSystemApps: Boolean = false,
    val backupProgressMsg: String? = null,
    val toastMessage: String? = null
)
