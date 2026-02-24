package com.privacyfilemanager.core.domain.repository

import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.StorageStats
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getStorageStats(): Flow<StorageStats>
    fun getLargeFiles(minSizeBytes: Long = 100 * 1024 * 1024): Flow<List<FileItem>> // Defaults to 100MB
    fun getDuplicateFiles(): Flow<List<List<FileItem>>>
    fun getJunkFiles(): Flow<List<FileItem>>
}
