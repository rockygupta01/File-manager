package com.privacyfilemanager.core.domain.repository

import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.SortConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for file operations.
 * All implementations must be fully offline.
 */
interface FileRepository {

    /**
     * List files in a directory.
     */
    suspend fun listFiles(
        directoryPath: String,
        showHidden: Boolean = false,
        sortConfig: SortConfig = SortConfig()
    ): Result<List<FileItem>>

    /**
     * Create a new file.
     */
    suspend fun createFile(parentPath: String, name: String): Result<FileItem>

    /**
     * Create a new directory.
     */
    suspend fun createDirectory(parentPath: String, name: String): Result<FileItem>

    /**
     * Delete files/directories.
     */
    suspend fun delete(paths: List<String>): Result<Int>

    /**
     * Rename a file/directory.
     */
    suspend fun rename(path: String, newName: String): Result<FileItem>

    /**
     * Copy files to a destination.
     */
    fun copy(sourcePaths: List<String>, destinationPath: String): Flow<CopyProgress>

    /**
     * Move files to a destination.
     */
    fun move(sourcePaths: List<String>, destinationPath: String): Flow<CopyProgress>

    /**
     * Get file details.
     */
    suspend fun getFileDetails(path: String): Result<FileItem>

    /**
     * Search files by name.
     */
    suspend fun searchFiles(
        query: String,
        rootPath: String,
        mimeTypeFilter: String? = null,
        searchContent: Boolean = false,
        searchMetadata: Boolean = false
    ): Result<List<FileItem>>

    /**
     * Get storage info.
     */
    suspend fun getStorageInfo(path: String): Result<StorageInfo>
}

data class CopyProgress(
    val currentFile: String,
    val currentFileIndex: Int,
    val totalFiles: Int,
    val bytesProcessed: Long,
    val totalBytes: Long,
    val isComplete: Boolean = false,
    val error: String? = null
)

data class StorageInfo(
    val totalBytes: Long,
    val usedBytes: Long,
    val freeBytes: Long
)
