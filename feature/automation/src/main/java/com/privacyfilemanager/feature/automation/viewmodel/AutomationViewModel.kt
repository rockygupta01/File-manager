package com.privacyfilemanager.feature.automation.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.privacyfilemanager.feature.automation.worker.AutoBackupWorker
import com.privacyfilemanager.feature.automation.worker.CleanupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class AutomationRule(
    val id: String,
    val name: String,
    val type: RuleType,
    val isEnabled: Boolean,
    val intervalHours: Int = 24
)

enum class RuleType { AUTO_BACKUP, AUTO_CLEANUP }

data class AutomationUiState(
    val rules: List<AutomationRule> = listOf(
        AutomationRule("backup_dcim", "Backup DCIM", RuleType.AUTO_BACKUP, false, 24),
        AutomationRule("backup_documents", "Backup Documents", RuleType.AUTO_BACKUP, false, 24),
        AutomationRule("cleanup_junk", "Clean Junk Files Weekly", RuleType.AUTO_CLEANUP, false, 168),
    ),
    val statusMessage: String? = null
)

@HiltViewModel
class AutomationViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    private val workManager = WorkManager.getInstance(context)

    fun toggleRule(ruleId: String) {
        val updated = _uiState.value.rules.map { rule ->
            if (rule.id == ruleId) {
                val newRule = rule.copy(isEnabled = !rule.isEnabled)
                if (newRule.isEnabled) scheduleRule(newRule) else cancelRule(newRule.id)
                newRule
            } else rule
        }
        _uiState.value = _uiState.value.copy(rules = updated, statusMessage = "Rule updated")
    }

    private fun scheduleRule(rule: AutomationRule) {
        when (rule.type) {
            RuleType.AUTO_BACKUP -> {
                val sourcePath = when (rule.id) {
                    "backup_dcim" -> "${Environment.getExternalStorageDirectory()}/DCIM"
                    else -> "${Environment.getExternalStorageDirectory()}/Documents"
                }
                val data = workDataOf(AutoBackupWorker.KEY_SOURCE_PATHS to sourcePath)
                val request = PeriodicWorkRequestBuilder<AutoBackupWorker>(
                    rule.intervalHours.toLong(), TimeUnit.HOURS
                ).setInputData(data).build()
                workManager.enqueueUniquePeriodicWork(rule.id, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
            RuleType.AUTO_CLEANUP -> {
                val data = workDataOf(
                    CleanupWorker.KEY_ROOT_PATH to Environment.getExternalStorageDirectory().absolutePath,
                    CleanupWorker.KEY_MAX_AGE_DAYS to 30
                )
                val request = PeriodicWorkRequestBuilder<CleanupWorker>(
                    rule.intervalHours.toLong(), TimeUnit.HOURS
                ).setInputData(data).build()
                workManager.enqueueUniquePeriodicWork(rule.id, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
        }
    }

    private fun cancelRule(ruleId: String) {
        workManager.cancelUniqueWork(ruleId)
    }

    fun runNow(rule: AutomationRule) {
        when (rule.type) {
            RuleType.AUTO_BACKUP -> {
                val sourcePath = when (rule.id) {
                    "backup_dcim" -> "${Environment.getExternalStorageDirectory()}/DCIM"
                    else -> "${Environment.getExternalStorageDirectory()}/Documents"
                }
                val data = workDataOf(AutoBackupWorker.KEY_SOURCE_PATHS to sourcePath)
                val request = OneTimeWorkRequestBuilder<AutoBackupWorker>().setInputData(data).build()
                workManager.enqueue(request)
                _uiState.value = _uiState.value.copy(statusMessage = "Backup started…")
            }
            RuleType.AUTO_CLEANUP -> {
                val data = workDataOf(
                    CleanupWorker.KEY_ROOT_PATH to Environment.getExternalStorageDirectory().absolutePath,
                    CleanupWorker.KEY_MAX_AGE_DAYS to 30
                )
                val request = OneTimeWorkRequestBuilder<CleanupWorker>().setInputData(data).build()
                workManager.enqueue(request)
                _uiState.value = _uiState.value.copy(statusMessage = "Cleanup started…")
            }
        }
    }

    fun clearStatus() { _uiState.value = _uiState.value.copy(statusMessage = null) }
}
