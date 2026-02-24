package com.privacyfilemanager.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey
    val path: String,
    val lastAccessed: Long,
    val mimeType: String,
    val size: Long,
    val name: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val path: String,
    val label: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "file_tags")
data class FileTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val filePath: String,
    val tag: String
)

@Entity(tableName = "vault_files")
data class VaultFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalPath: String,
    val encryptedPath: String,
    val iv: String,
    val originalName: String,
    val mimeType: String,
    val size: Long,
    val hiddenAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sourceDir: String,
    val destDir: String,
    val typeFilter: String, // comma-separated extensions
    val schedule: String, // "daily", "weekly", "manual"
    val enabled: Boolean = true,
    val lastRun: Long? = null
)

@Entity(tableName = "search_index")
data class SearchIndexEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val filePath: String,
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val lastModified: Long,
    val parentDir: String
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_lock_config")
data class AppLockConfigEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton
    val lockType: String, // "pin", "pattern", "none"
    val hashedCredential: String?,
    val timeoutMinutes: Int = 1,
    val biometricEnabled: Boolean = false,
    val failedAttempts: Int = 0,
    val lockoutUntil: Long? = null
)

@Entity(tableName = "crash_logs")
data class CrashLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val threadName: String,
    val stackTrace: String,
    val deviceInfo: String
)
