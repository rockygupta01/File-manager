package com.privacyfilemanager.feature.devtools.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class DevToolsUiState(
    val activeTab: Int = 0,
    // Terminal
    val terminalHistory: List<Pair<String, String>> = emptyList(),
    val currentCommand: String = "",
    val isTerminalLoading: Boolean = false,
    // OCR
    val imagePath: String = "",
    val ocrText: String = "",
    val isOcrLoading: Boolean = false,
    val ocrError: String? = null
)

@HiltViewModel
class DevToolsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevToolsUiState())
    val uiState: StateFlow<DevToolsUiState> = _uiState.asStateFlow()

    // ─── Terminal ────────────────────────────────────────────────────
    fun onCommandChange(cmd: String) {
        _uiState.value = _uiState.value.copy(currentCommand = cmd)
    }

    fun runCommand() {
        val cmd = _uiState.value.currentCommand.trim()
        if (cmd.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTerminalLoading = true, currentCommand = "")
            val output = withContext(Dispatchers.IO) {
                try {
                    val process = Runtime.getRuntime().exec(cmd)
                    val out = process.inputStream.bufferedReader().readText()
                    val err = process.errorStream.bufferedReader().readText()
                    process.waitFor()
                    buildString {
                        if (out.isNotBlank()) append(out)
                        if (err.isNotBlank()) append("ERR: $err")
                    }.ifBlank { "(no output)" }
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
            }
            val history = _uiState.value.terminalHistory.takeLast(49) + (cmd to output)
            _uiState.value = _uiState.value.copy(isTerminalLoading = false, terminalHistory = history)
        }
    }

    // ─── OCR ─────────────────────────────────────────────────────────
    fun setImagePath(path: String) {
        _uiState.value = _uiState.value.copy(imagePath = path, ocrText = "", ocrError = null)
    }

    fun runOcr() {
        val path = _uiState.value.imagePath
        if (path.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOcrLoading = true, ocrError = null)
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeFile(File(path).absolutePath)
                } ?: throw Exception("Could not decode image")

                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val result = recognizer.process(image).await()
                recognizer.close()

                _uiState.value = _uiState.value.copy(
                    isOcrLoading = false,
                    ocrText = result.text.ifBlank { "No text detected in this image." }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isOcrLoading = false,
                    ocrError = "OCR failed: ${e.message}"
                )
            }
        }
    }

    fun clearOcr() {
        _uiState.value = _uiState.value.copy(imagePath = "", ocrText = "", ocrError = null)
    }

    fun setTab(tab: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }
}
