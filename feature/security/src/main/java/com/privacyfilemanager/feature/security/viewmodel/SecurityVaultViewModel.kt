package com.privacyfilemanager.feature.security.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.security.AppLockManager
import com.privacyfilemanager.core.security.EncryptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityVaultViewModel @Inject constructor(
    private val appLockManager: AppLockManager,
    private val encryptionManager: EncryptionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityVaultUiState())
    val uiState: StateFlow<SecurityVaultUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = _uiState.value.copy(
            isLockEnabled = appLockManager.isLockEnabled,
            isBiometricEnabled = appLockManager.isBiometricEnabled
        )
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            appLockManager.setPin(pin)
            loadSettings()
        }
    }

    fun removeLock() {
        viewModelScope.launch {
            appLockManager.removeLock()
            loadSettings()
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            appLockManager.setBiometricEnabled(enabled)
            loadSettings()
        }
    }
}

data class SecurityVaultUiState(
    val isLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false
)
