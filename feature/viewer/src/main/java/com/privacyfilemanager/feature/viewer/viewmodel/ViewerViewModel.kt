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
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class ViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    val exoPlayer: ExoPlayer
) : ViewModel() {

    private val pathString: String = savedStateHandle.get<String>("path") ?: ""
    private val file = File(pathString)
    
    val category: FileCategory = FileUtils.getFileCategory(file)
    val fileName: String = file.name

    fun getPlayer(fileToPlay: File): ExoPlayer {
        // Only set media item if playing a different file, or if idle
        if (exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString() != fileToPlay.toURI().toString()) {
            val mediaItemBuilder = MediaItem.Builder()
                .setUri(fileToPlay.toURI().toString())

            // Auto sideload subtitles if present
            if (category == FileCategory.VIDEO) {
                val parentDir = fileToPlay.parentFile
                val baseName = fileToPlay.nameWithoutExtension
                if (parentDir != null && parentDir.isDirectory) {
                    val supportedSubExtensions = listOf("srt", "vtt", "ass", "ssa")
                    val subtitleFile = parentDir.listFiles()?.firstOrNull { 
                        it.isFile && it.nameWithoutExtension.equals(baseName, ignoreCase = true) &&
                        it.extension.lowercase() in supportedSubExtensions
                    }
                    
                    if (subtitleFile != null) {
                        val mimeType = when (subtitleFile.extension.lowercase()) {
                            "srt" -> androidx.media3.common.MimeTypes.APPLICATION_SUBRIP
                            "vtt" -> androidx.media3.common.MimeTypes.TEXT_VTT
                            "ass", "ssa" -> androidx.media3.common.MimeTypes.TEXT_SSA
                            else -> androidx.media3.common.MimeTypes.APPLICATION_SUBRIP
                        }
                        
                        val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.fromFile(subtitleFile))
                            .setMimeType(mimeType)
                            .setLanguage("en")
                            .setSelectionFlags(androidx.media3.common.C.SELECTION_FLAG_DEFAULT)
                            .build()
                            
                        mediaItemBuilder.setSubtitleConfigurations(listOf(subtitleConfig))
                    }
                }
            }

            exoPlayer.setMediaItem(mediaItemBuilder.build())
            exoPlayer.prepare()
        }
        exoPlayer.playWhenReady = true
        return exoPlayer
    }

    override fun onCleared() {
        super.onCleared()
        // Only stop and clear if we are not actively playing (e.g., normal exit, not PiP/background)
        if (exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString() == file.toURI().toString()) {
            if (!exoPlayer.playWhenReady) {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
            }
        }
    }

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
