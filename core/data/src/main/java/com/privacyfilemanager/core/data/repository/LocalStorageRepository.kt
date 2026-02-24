package com.privacyfilemanager.core.data.repository

import android.os.Environment
import android.os.StatFs
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.model.StorageStats
import com.privacyfilemanager.core.domain.repository.StorageRepository
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorageRepository @Inject constructor() : StorageRepository {

    override fun getStorageStats(): Flow<StorageStats> = flow {
        val path = Environment.getExternalStorageDirectory().path
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val freeBytes = availableBlocks * blockSize

        // For privacy file manager, avoid long running deep scans if not necessary,
        // or we could add a basic scan. For now, we return basic statfs data.
        val stats = StorageStats(
            totalBytes = totalBytes,
            freeBytes = freeBytes
        )
        emit(stats)
    }.flowOn(Dispatchers.IO)

    override fun getLargeFiles(minSizeBytes: Long): Flow<List<FileItem>> = flow {
        val root = Environment.getExternalStorageDirectory()
        val largeFiles = mutableListOf<FileItem>()
        
        root.walkTopDown()
            .onEnter { !it.name.startsWith(".") } // Skip hidden dirs to save time
            .filter { it.isFile && it.length() >= minSizeBytes }
            .take(100)
            .forEach { largeFiles.add(it.toFileItem()) }
            
        emit(largeFiles.sortedByDescending { it.size })
    }.flowOn(Dispatchers.IO)

    override fun getDuplicateFiles(): Flow<List<List<FileItem>>> = flow {
        val root = Environment.getExternalStorageDirectory()
        
        // Strategy: First group by size, then compare hashes for identical sizes.
        // For performance, we limit the search to files > 1MB.
        val sizeMap = mutableMapOf<Long, MutableList<File>>()
        
        root.walkTopDown()
            .onEnter { !it.name.startsWith(".") }
            .filter { it.isFile && it.length() > 1024 * 1024 }
            .take(1000)
            .forEach { file ->
                val size = file.length()
                sizeMap.getOrPut(size) { mutableListOf() }.add(file)
            }
            
        val potentialDuplicates = sizeMap.filterValues { it.size > 1 }
        
        val actualDuplicates = mutableListOf<List<FileItem>>()
        for ((_, files) in potentialDuplicates) {
            val hashMap = mutableMapOf<String, MutableList<FileItem>>()
            for (file in files) {
                // Rough hash: just read first 8KB to save time
                val hash = file.runCatching {
                    java.security.MessageDigest.getInstance("MD5").let { md ->
                        inputStream().use { input ->
                            val buffer = ByteArray(8192)
                            val read = input.read(buffer)
                            if (read > 0) md.update(buffer, 0, read)
                        }
                        md.digest().joinToString("") { "%02x".format(it) }
                    }
                }.getOrNull() ?: continue
                
                hashMap.getOrPut(hash) { mutableListOf() }.add(file.toFileItem())
            }
            
            hashMap.values.filter { it.size > 1 }.forEach {
                actualDuplicates.add(it)
            }
        }
        
        emit(actualDuplicates)
    }.flowOn(Dispatchers.IO)

    override fun getJunkFiles(): Flow<List<FileItem>> = flow {
        val root = Environment.getExternalStorageDirectory()
        val junkFiles = mutableListOf<FileItem>()
        val junkExtensions = setOf("tmp", "log", "bak", "chk")

        root.walkTopDown()
            .onEnter { !it.name.startsWith(".") }
            .filter { file -> 
                file.isFile && (file.extension.lowercase() in junkExtensions || file.length() == 0L)
            }
            .take(200)
            .forEach { junkFiles.add(it.toFileItem()) }
            
        emit(junkFiles)
    }.flowOn(Dispatchers.IO)

    // Helper mapper
    private fun java.io.File.toFileItem(): FileItem = FileItem(
        name = name,
        path = absolutePath,
        isDirectory = isDirectory,
        size = if (isDirectory) 0L else length(),
        lastModified = lastModified(),
        mimeType = if (isDirectory) "inode/directory" else com.privacyfilemanager.core.common.util.FileUtils.getMimeType(this),
        category = com.privacyfilemanager.core.common.util.FileUtils.getFileCategory(this),
        isHidden = isHidden,
        isReadable = canRead(),
        isWritable = canWrite(),
        childCount = if (isDirectory) (listFiles()?.size ?: 0) else 0
    )
}
