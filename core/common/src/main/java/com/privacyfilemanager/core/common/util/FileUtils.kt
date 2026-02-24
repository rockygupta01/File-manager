package com.privacyfilemanager.core.common.util

import android.webkit.MimeTypeMap
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * File utility extensions — all local, no network calls.
 */
object FileUtils {

    private val sizeFormat = DecimalFormat("#,##0.#")
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    /**
     * Format file size in human-readable form.
     */
    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${sizeFormat.format(sizeInBytes / 1024.0)} KB"
            sizeInBytes < 1024 * 1024 * 1024 -> "${sizeFormat.format(sizeInBytes / (1024.0 * 1024.0))} MB"
            else -> "${sizeFormat.format(sizeInBytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }

    /**
     * Format timestamp to readable date string.
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Get MIME type from file extension — uses Android's built-in resolver.
     */
    fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }

    /**
     * Get file category for display icons.
     */
    fun getFileCategory(file: File): FileCategory {
        if (file.isDirectory) return FileCategory.FOLDER

        return when (file.extension.lowercase()) {
            // Images
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "heic", "heif" -> FileCategory.IMAGE
            // Videos
            "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "3gp", "m4v" -> FileCategory.VIDEO
            // Audio
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "opus" -> FileCategory.AUDIO
            // Documents
            "pdf" -> FileCategory.PDF
            "doc", "docx", "odt", "rtf" -> FileCategory.DOCUMENT
            "xls", "xlsx", "ods", "csv" -> FileCategory.SPREADSHEET
            "ppt", "pptx", "odp" -> FileCategory.PRESENTATION
            "txt", "log", "md", "rst" -> FileCategory.TEXT
            // Code
            "kt", "java", "py", "js", "ts", "html", "css", "xml", "json", "yaml", "yml",
            "c", "cpp", "h", "hpp", "rs", "go", "swift", "dart", "rb", "php", "sql",
            "sh", "bat", "ps1", "gradle", "toml" -> FileCategory.CODE
            // Archives
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "zst" -> FileCategory.ARCHIVE
            // APK
            "apk", "xapk", "aab" -> FileCategory.APK
            // Other
            else -> FileCategory.OTHER
        }
    }

    /**
     * Count items in a directory (non-recursive).
     */
    fun getDirectoryItemCount(directory: File): Int {
        return directory.listFiles()?.size ?: 0
    }
}

enum class FileCategory {
    FOLDER, IMAGE, VIDEO, AUDIO, PDF, DOCUMENT, SPREADSHEET,
    PRESENTATION, TEXT, CODE, ARCHIVE, APK, OTHER
}
