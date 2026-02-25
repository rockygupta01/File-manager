package com.privacyfilemanager.feature.search.worker

import android.content.Context
import android.os.Environment
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.privacyfilemanager.core.database.dao.SearchIndexDao
import com.privacyfilemanager.core.database.entity.SearchIndexEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker that scans the external storage and builds a
 * full-text search index in the Room database.
 *
 * Runs on-device only — no network, no telemetry.
 */
@HiltWorker
class SearchIndexWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val searchIndexDao: SearchIndexDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entries = mutableListOf<SearchIndexEntity>()
            val rootDir = Environment.getExternalStorageDirectory()

            // Walk the external storage tree, collecting file metadata
            rootDir.walkTopDown()
                .onEnter { dir ->
                    // Skip hidden directories and certain system folders
                    !dir.name.startsWith(".") &&
                        dir.name != "Android" // very large system folder — skip for speed
                }
                .filter { it.isFile }
                .take(50_000) // safety cap — avoid OOM on huge storage
                .forEach { file ->
                    entries.add(
                        SearchIndexEntity(
                            filePath = file.absolutePath,
                            fileName = file.name,
                            mimeType = getMimeType(file.extension),
                            size = file.length(),
                            lastModified = file.lastModified(),
                            parentDir = file.parent ?: ""
                        )
                    )

                    // Flush in batches of 500 to avoid large transactions
                    if (entries.size >= 500) {
                        searchIndexDao.insertAll(entries.toList())
                        entries.clear()
                    }
                }

            // Flush remaining entries
            if (entries.isNotEmpty()) {
                searchIndexDao.insertAll(entries)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getMimeType(extension: String): String = when (extension.lowercase()) {
        "jpg", "jpeg", "png", "gif", "webp", "heic", "heif", "bmp" -> "image/"
        "mp4", "mkv", "avi", "mov", "webm", "3gp" -> "video/"
        "mp3", "flac", "aac", "ogg", "wav", "m4a" -> "audio/"
        "pdf" -> "application/pdf"
        "doc", "docx" -> "application/word"
        "xls", "xlsx" -> "application/excel"
        "ppt", "pptx" -> "application/powerpoint"
        "zip", "rar", "7z", "tar", "gz" -> "application/archive"
        "apk" -> "application/vnd.android.package-archive"
        "txt", "md", "log", "csv" -> "text/"
        "kt", "java", "py", "js", "ts", "json", "xml", "html", "css" -> "text/code"
        else -> "application/octet-stream"
    }
}
