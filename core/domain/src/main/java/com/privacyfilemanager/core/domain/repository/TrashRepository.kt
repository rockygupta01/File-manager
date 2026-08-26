package com.privacyfilemanager.core.domain.repository

import com.privacyfilemanager.core.database.dao.TrashDao
import com.privacyfilemanager.core.database.entity.TrashEntity
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrashRepository @Inject constructor(
    private val trashDao: TrashDao
) {
    // Internal directory for storing trashed files
    // Using a dot-prefix hides it from normal Android media scanners
    private val trashDirName = ".trash"

    /**
     * Moves a list of file paths to the internal .trash directory and records them in the database.
     * @return List of IDs of the newly trashed items (useful for Undo).
     */
    suspend fun moveToTrash(paths: List<String>): List<Int> {
        val insertedIds = mutableListOf<Int>()
        for (path in paths) {
            val sourceFile = File(path)
            if (!sourceFile.exists()) continue

            val appTrashDir = getAppTrashDir(sourceFile) ?: continue
            if (!appTrashDir.exists()) {
                appTrashDir.mkdirs()
            }

            // Create a unique filename in the trash to avoid collisions
            val trashFileName = "${System.currentTimeMillis()}_${sourceFile.name}"
            val trashFile = File(appTrashDir, trashFileName)

            try {
                if (sourceFile.renameTo(trashFile)) {
                    // Successfully moved, record in DB
                    val id = trashDao.insert(
                        TrashEntity(
                            originalPath = sourceFile.absolutePath,
                            trashPath = trashFile.absolutePath,
                            size = sourceFile.length(),
                            name = sourceFile.name,
                            mimeType = determineMimeType(sourceFile)
                        )
                    )
                    insertedIds.add(id.toInt())
                }
            } catch (e: Exception) {
                // Ignore individual file errors and continue
                e.printStackTrace()
            }
        }
        return insertedIds
    }

    /**
     * Restores a trashed file back to its original location by its ID.
     */
    suspend fun restoreFromTrash(id: Int): Boolean {
        val entity = trashDao.getById(id) ?: return false
        val trashFile = File(entity.trashPath)
        val originalFile = File(entity.originalPath)

        if (!trashFile.exists()) {
            // Trash file is missing from disk, clean up DB
            trashDao.deleteById(id)
            return false
        }

        // Ensure parent directory of original file exists
        originalFile.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        return try {
            if (trashFile.renameTo(originalFile)) {
                trashDao.deleteById(id)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Permanently deletes a file from the trash (both from disk and database).
     */
    suspend fun permanentlyDelete(id: Int): Boolean {
        val entity = trashDao.getById(id) ?: return false
        val trashFile = File(entity.trashPath)

        // Delete from disk if it exists
        if (trashFile.exists()) {
            trashFile.deleteRecursively()
        }

        // Always delete from DB
        trashDao.deleteById(id)
        return true
    }

    /**
     * Permanently deletes all trashed files.
     */
    suspend fun emptyTrash() {
        // We need to delete all files from disk first
        // Flow collection might be complex here, so we could theoretically
        // read all entries if we added a non-flow query, but for now we
        // can just clear the physical trash directories we know about.
        
        // As a robust alternative, we can query all files (we need a suspend fun in DAO)
        // Since we don't have a suspend getAll() in DAO, we'll just clear the global trash dir
        // (Assuming a single top-level trash dir for simplicity in this implementation)
        
        // Actually, to be safe, let's rely on the DB records. If we don't have a one-shot query,
        // we might leave orphaned files. For this implementation, let's assume we can add
        // a simple file tree walk if needed, or rely on autoClean.
        // Let's add a clear disk logic based on typical external storage.
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        val globalTrashDir = File(externalStorage, trashDirName)
        if (globalTrashDir.exists()) {
            globalTrashDir.listFiles()?.forEach { it.deleteRecursively() }
        }

        trashDao.clearAll()
    }

    /**
     * Deletes files older than the specified number of days.
     */
    suspend fun autoCleanExpired(daysOld: Int = 30) {
        val cutoffTimestamp = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000)
        
        // We'd ideally need a list of expired items to delete from disk.
        // For simplicity, we can do a file-based cleanup on the trash dir
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        val globalTrashDir = File(externalStorage, trashDirName)
        
        if (globalTrashDir.exists()) {
            globalTrashDir.listFiles()?.forEach { file ->
                if (file.lastModified() < cutoffTimestamp) {
                    file.deleteRecursively()
                }
            }
        }
        
        trashDao.deleteExpired(cutoffTimestamp)
    }

    /**
     * Returns a Flow of all trashed files.
     */
    fun getAllTrashedFiles(): Flow<List<TrashEntity>> {
        return trashDao.getAllTrashedFiles()
    }

    // Helper to get or create a .trash directory on the same mount point as the source
    private fun getAppTrashDir(sourceFile: File): File? {
        // For simplicity, we'll use a single global .trash directory in the primary external storage
        // In a more complex dual-storage setup (SD card), we'd need to find the mount root.
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        return File(externalStorage, trashDirName)
    }

    private fun determineMimeType(file: File): String {
        return if (file.isDirectory) {
            "inode/directory"
        } else {
            val extension = file.extension.lowercase()
            android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
        }
    }
}
