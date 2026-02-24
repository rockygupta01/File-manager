package com.privacyfilemanager.feature.root.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.feature.root.shell.RootShell
import com.privacyfilemanager.feature.root.shell.ShellResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RootUiState(
    val isRootAvailable: Boolean? = null,  // null = checking
    val isLoading: Boolean = false,
    val currentPath: String = "/",
    val directoryListing: String = "",
    val terminalHistory: List<Pair<String, String>> = emptyList(), // command -> output
    val currentCommand: String = ""
)

@HiltViewModel
class RootViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RootUiState())
    val uiState: StateFlow<RootUiState> = _uiState.asStateFlow()

    init {
        checkRoot()
    }

    private fun checkRoot() {
        viewModelScope.launch {
            val available = RootShell.isRootAvailable()
            _uiState.value = _uiState.value.copy(isRootAvailable = available)
            if (available) listCurrentDirectory()
        }
    }

    fun onCommandChange(cmd: String) {
        _uiState.value = _uiState.value.copy(currentCommand = cmd)
    }

    fun runCommand() {
        val cmd = _uiState.value.currentCommand.trim()
        if (cmd.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentCommand = "")
            val result = RootShell.execute(cmd)
            val output = buildString {
                if (result.output.isNotBlank()) append(result.output)
                if (result.error.isNotBlank()) append("ERROR: ${result.error}")
                if (result.exitCode != 0) append("\n[exit code: ${result.exitCode}]")
            }.ifBlank { "(no output)" }
            val history = _uiState.value.terminalHistory.takeLast(49) + (cmd to output)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                terminalHistory = history
            )
        }
    }

    fun chmod(path: String, permissions: String) {
        viewModelScope.launch {
            val result = RootShell.chmod(path, permissions)
            val output = if (result.exitCode == 0) "chmod $permissions $path: OK" else result.error
            val history = _uiState.value.terminalHistory.takeLast(49) + ("chmod $permissions $path" to output)
            _uiState.value = _uiState.value.copy(terminalHistory = history)
        }
    }

    private fun listCurrentDirectory() {
        viewModelScope.launch {
            val result = RootShell.listDirectory(_uiState.value.currentPath)
            _uiState.value = _uiState.value.copy(directoryListing = result.output)
        }
    }
}
