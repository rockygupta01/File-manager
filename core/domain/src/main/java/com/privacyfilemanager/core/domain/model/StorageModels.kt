package com.privacyfilemanager.core.domain.model

data class StorageStats(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long = totalBytes - freeBytes,
    val categoryBreakdown: Map<String, Long> = emptyMap()
)

data class LargeFileItem(
    val file: FileItem,
    val size: Long
)
