package com.privacyfilemanager.feature.security.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.security.AppLockManager
import com.privacyfilemanager.core.security.EncryptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@HiltViewModel
class SecurityVaultViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    // ── Backup / Restore ──────────────────────────────────────────────────────

    /**
     * Creates a local backup ZIP of the Room database files into the app's
     * private files/backups/ directory. No data is sent anywhere — 100% local.
     */
    fun createBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBackupInProgress = true, backupMessage = null)
            withContext(Dispatchers.IO) {
                try {
                    val backupDir = File(context.filesDir, "backups").also { it.mkdirs() }
                    val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val backupFile = File(backupDir, "backup_$ts.zip")

                    ZipOutputStream(backupFile.outputStream().buffered()).use { zip ->
                        // Back up all Room database files
                        val dbDir = context.getDatabasePath("app_database").parentFile
                        dbDir?.listFiles()?.forEach { dbFile ->
                            if (dbFile.exists()) {
                                zip.putNextEntry(ZipEntry("db/${dbFile.name}"))
                                dbFile.inputStream().use { it.copyTo(zip) }
                                zip.closeEntry()
                            }
                        }
                    }

                    // Keep only the last 5 backups
                    val allBackups = backupDir.listFiles()
                        ?.filter { it.name.startsWith("backup_") && it.name.endsWith(".zip") }
                        ?.sortedByDescending { it.lastModified() } ?: emptyList()
                    if (allBackups.size > 5) {
                        allBackups.drop(5).forEach { it.delete() }
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            isBackupInProgress = false,
                            backupMessage = "Backup saved: ${backupFile.name}"
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            isBackupInProgress = false,
                            backupMessage = "Backup failed: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    /** Lists available local backups sorted newest-first. */
    fun loadBackups() {
        viewModelScope.launch(Dispatchers.IO) {
            val backupDir = File(context.filesDir, "backups")
            val files = backupDir.listFiles()
                ?.filter { it.name.endsWith(".zip") }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(availableBackups = files.map { it.name })
            }
        }
    }

    fun clearBackupMessage() {
        _uiState.value = _uiState.value.copy(backupMessage = null)
    }
}

data class SecurityVaultUiState(
    val isLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBackupInProgress: Boolean = false,
    val backupMessage: String? = null,
    val availableBackups: List<String> = emptyList()
)
