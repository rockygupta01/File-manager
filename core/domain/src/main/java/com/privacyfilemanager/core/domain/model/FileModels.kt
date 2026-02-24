package com.privacyfilemanager.core.domain.model

import com.privacyfilemanager.core.common.util.FileCategory

/**
 * Domain model for a file/directory item.
 * Pure Kotlin — no Android framework dependencies.
 */
data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String,
    val category: FileCategory,
    val isHidden: Boolean = name.startsWith("."),
    val isReadable: Boolean = true,
    val isWritable: Boolean = true,
    val childCount: Int = 0 // Only for directories
)

/**
 * Sort options for file listings.
 */
enum class SortBy {
    NAME, SIZE, DATE, TYPE
}

enum class SortOrder {
    ASCENDING, DESCENDING
}

data class SortConfig(
    val sortBy: SortBy = SortBy.NAME,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val foldersFirst: Boolean = true
)

/**
 * View mode for file listings.
 */
enum class ViewMode {
    LIST, GRID
}

/**
 * Clipboard operation.
 */
data class FileClipboard(
    val files: List<String> = emptyList(),
    val operation: ClipboardOperation = ClipboardOperation.NONE
)

enum class ClipboardOperation {
    NONE, COPY, CUT
}
