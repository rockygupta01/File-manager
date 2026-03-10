package com.privacyfilemanager.core.domain.repository

import com.privacyfilemanager.core.domain.model.FileItem
import kotlinx.coroutines.flow.Flow

interface MediaLibraryRepository {
    fun getAudioFiles(sortBy: MediaSortOption = MediaSortOption.DATE_MODIFIED): Flow<List<FileItem>>
    fun getVideoFiles(sortBy: MediaSortOption = MediaSortOption.DATE_MODIFIED): Flow<List<FileItem>>
    fun getRecentlyPlayedMedia(): Flow<List<FileItem>>
    fun getLargestMediaFiles(): Flow<List<FileItem>>
    suspend fun markMediaAsPlayed(path: String)
}

enum class MediaSortOption {
    NAME, SIZE, DATE_MODIFIED, DURATION
}
