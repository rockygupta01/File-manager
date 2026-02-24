package com.privacyfilemanager.core.data.repository


import com.privacyfilemanager.core.common.util.FileUtils
import com.privacyfilemanager.core.common.util.Result
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.SortBy
import com.privacyfilemanager.core.domain.model.SortConfig
import com.privacyfilemanager.core.domain.model.SortOrder
import com.privacyfilemanager.core.domain.repository.CopyProgress
import com.privacyfilemanager.core.domain.repository.FileRepository
import com.privacyfilemanager.core.domain.repository.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local-only file repository implementation.
 * All operations use java.io.File and java.nio — no network calls.
 */
@Singleton
class LocalFileRepository @Inject constructor() : FileRepository {

    override suspend fun listFiles(
        directoryPath: String,
        showHidden: Boolean,
        sortConfig: SortConfig
    ): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val dir = File(directoryPath)
            if (!dir.exists() || !dir.isDirectory) {
                return@withContext Result.Error("Directory does not exist: $directoryPath")
            }

            val files = dir.listFiles()
                ?.filter { showHidden || !it.isHidden }
                ?.map { it.toFileItem() }
                ?.sortedWith(buildComparator(sortConfig))
                ?: emptyList()

            Result.Success(files)
        } catch (e: Exception) {
            Result.Error("Failed to list files: ${e.message}", e)
        }
    }

    override suspend fun createFile(parentPath: String, name: String): Result<FileItem> =
        withContext(Dispatchers.IO) {
            try {
                val file = File(parentPath, name)
                if (file.exists()) {
                    return@withContext Result.Error("File already exists: ${file.path}")
                }
                file.createNewFile()
                Result.Success(file.toFileItem())
            } catch (e: Exception) {
                Result.Error("Failed to create file: ${e.message}", e)
            }
        }

    override suspend fun createDirectory(parentPath: String, name: String): Result<FileItem> =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(parentPath, name)
                if (dir.exists()) {
                    return@withContext Result.Error("Directory already exists: ${dir.path}")
                }
                dir.mkdirs()
                Result.Success(dir.toFileItem())
            } catch (e: Exception) {
                Result.Error("Failed to create directory: ${e.message}", e)
            }
        }

    override suspend fun delete(paths: List<String>): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                var deleted = 0
                paths.forEach { path ->
                    val file = File(path)
                    if (file.exists()) {
                        if (file.isDirectory) {
                            file.deleteRecursively()
                        } else {
                            file.delete()
                        }
                        deleted++
                    }
                }
                Result.Success(deleted)
            } catch (e: Exception) {
                Result.Error("Failed to delete: ${e.message}", e)
            }
        }

    override suspend fun rename(path: String, newName: String): Result<FileItem> =
        withContext(Dispatchers.IO) {
            try {
                val source = File(path)
                val target = File(source.parent, newName)
                if (target.exists()) {
                    return@withContext Result.Error("A file with that name already exists")
                }
                if (source.renameTo(target)) {
                    Result.Success(target.toFileItem())
                } else {
                    Result.Error("Rename failed")
                }
            } catch (e: Exception) {
                Result.Error("Failed to rename: ${e.message}", e)
            }
        }

    override fun copy(sourcePaths: List<String>, destinationPath: String): Flow<CopyProgress> =
        flow {
            val destDir = File(destinationPath)
            if (!destDir.exists()) destDir.mkdirs()

            val sourceFiles = sourcePaths.map { File(it) }
            val totalBytes = sourceFiles.sumOf { if (it.isDirectory) getDirSize(it) else it.length() }
            var bytesProcessed = 0L

            sourceFiles.forEachIndexed { index, source ->
                val target = File(destDir, source.name)

                if (source.isDirectory) {
                    bytesProcessed = copyDirectoryRecursive(
                        source, target, index, sourceFiles.size,
                        bytesProcessed, totalBytes
                    ) { emit(it) }
                } else {
                    copyFileWithChannel(source, target)
                    bytesProcessed += source.length()
                    emit(
                        CopyProgress(
                            currentFile = source.name,
                            currentFileIndex = index + 1,
                            totalFiles = sourceFiles.size,
                            bytesProcessed = bytesProcessed,
                            totalBytes = totalBytes
                        )
                    )
                }
            }

            emit(
                CopyProgress(
                    currentFile = "",
                    currentFileIndex = sourceFiles.size,
                    totalFiles = sourceFiles.size,
                    bytesProcessed = totalBytes,
                    totalBytes = totalBytes,
                    isComplete = true
                )
            )
        }.flowOn(Dispatchers.IO)

    override fun move(sourcePaths: List<String>, destinationPath: String): Flow<CopyProgress> =
        flow {
            val destDir = File(destinationPath)
            if (!destDir.exists()) destDir.mkdirs()

            sourcePaths.forEachIndexed { index, sourcePath ->
                val source = File(sourcePath)
                val target = File(destDir, source.name)

                // Try rename first (instant for same filesystem)
                if (!source.renameTo(target)) {
                    // Fallback: copy then delete
                    if (source.isDirectory) {
                        source.copyRecursively(target, overwrite = true)
                    } else {
                        copyFileWithChannel(source, target)
                    }
                    source.deleteRecursively()
                }

                emit(
                    CopyProgress(
                        currentFile = source.name,
                        currentFileIndex = index + 1,
                        totalFiles = sourcePaths.size,
                        bytesProcessed = (index + 1).toLong(),
                        totalBytes = sourcePaths.size.toLong(),
                        isComplete = index == sourcePaths.lastIndex
                    )
                )
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getFileDetails(path: String): Result<FileItem> =
        withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    Result.Error("File not found: $path")
                } else {
                    Result.Success(file.toFileItem())
                }
            } catch (e: Exception) {
                Result.Error("Failed to get details: ${e.message}", e)
            }
        }

    override suspend fun searchFiles(
        query: String,
        rootPath: String,
        mimeTypeFilter: String?
    ): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val root = File(rootPath)
            val results = mutableListOf<FileItem>()

            root.walkTopDown()
                .maxDepth(10)
                .filter { it.name.contains(query, ignoreCase = true) }
                .filter { mimeTypeFilter == null || FileUtils.getMimeType(it).startsWith(mimeTypeFilter) }
                .take(200) // Limit results
                .forEach { results.add(it.toFileItem()) }

            Result.Success(results)
        } catch (e: Exception) {
            Result.Error("Search failed: ${e.message}", e)
        }
    }

    override suspend fun getStorageInfo(path: String): Result<StorageInfo> =
        withContext(Dispatchers.IO) {
            try {
                val stat = android.os.StatFs(path)
                Result.Success(
                    StorageInfo(
                        totalBytes = stat.totalBytes,
                        usedBytes = stat.totalBytes - stat.availableBytes,
                        freeBytes = stat.availableBytes
                    )
                )
            } catch (e: Exception) {
                Result.Error("Failed to get storage info: ${e.message}", e)
            }
        }

    // ===== Private helpers =====

    private fun File.toFileItem(): FileItem = FileItem(
        name = name,
        path = absolutePath,
        isDirectory = isDirectory,
        size = if (isDirectory) 0L else length(),
        lastModified = lastModified(),
        mimeType = if (isDirectory) "inode/directory" else FileUtils.getMimeType(this),
        category = FileUtils.getFileCategory(this),
        isHidden = isHidden,
        isReadable = canRead(),
        isWritable = canWrite(),
        childCount = if (isDirectory) (listFiles()?.size ?: 0) else 0
    )

    private fun buildComparator(config: SortConfig): Comparator<FileItem> {
        var comparator: Comparator<FileItem> = when (config.sortBy) {
            SortBy.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it: FileItem -> it.name }
            SortBy.SIZE -> compareBy { it: FileItem -> it.size }
            SortBy.DATE -> compareBy { it: FileItem -> it.lastModified }
            SortBy.TYPE -> compareBy(String.CASE_INSENSITIVE_ORDER) { it: FileItem ->
                if (it.isDirectory) "" else it.name.substringAfterLast('.', "")
            }
        }

        if (config.sortOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed()
        }

        if (config.foldersFirst) {
            comparator = compareByDescending<FileItem> { it.isDirectory }.then(comparator)
        }

        return comparator
    }

    private fun copyFileWithChannel(source: File, target: File) {
        FileInputStream(source).channel.use { srcChannel ->
            FileOutputStream(target).channel.use { dstChannel ->
                dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
            }
        }
        target.setLastModified(source.lastModified())
    }

    private suspend fun copyDirectoryRecursive(
        source: File,
        target: File,
        fileIndex: Int,
        totalFiles: Int,
        startBytes: Long,
        totalBytes: Long,
        emit: suspend (CopyProgress) -> Unit
    ): Long {
        target.mkdirs()
        var bytesProcessed = startBytes

        source.listFiles()?.forEach { child ->
            val childTarget = File(target, child.name)
            if (child.isDirectory) {
                bytesProcessed = copyDirectoryRecursive(
                    child, childTarget, fileIndex, totalFiles,
                    bytesProcessed, totalBytes, emit
                )
            } else {
                copyFileWithChannel(child, childTarget)
                bytesProcessed += child.length()
                emit(
                    CopyProgress(
                        currentFile = child.name,
                        currentFileIndex = fileIndex + 1,
                        totalFiles = totalFiles,
                        bytesProcessed = bytesProcessed,
                        totalBytes = totalBytes
                    )
                )
            }
        }

        return bytesProcessed
    }

    private fun getDirSize(dir: File): Long =
        dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
}
