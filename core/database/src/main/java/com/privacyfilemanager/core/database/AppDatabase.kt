package com.privacyfilemanager.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.privacyfilemanager.core.database.dao.*
import com.privacyfilemanager.core.database.entity.*

/**
 * Room database — all data stays in app-private storage.
 * No cloud sync, no remote backup, no telemetry.
 */
@Database(
    entities = [
        RecentFileEntity::class,
        BookmarkEntity::class,
        FileTagEntity::class,
        VaultFileEntity::class,
        AutomationRuleEntity::class,
        SearchIndexEntity::class,
        SearchHistoryEntity::class,
        AppLockConfigEntity::class,
        CrashLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun fileTagDao(): FileTagDao
    abstract fun vaultFileDao(): VaultFileDao
    abstract fun automationRuleDao(): AutomationRuleDao
    abstract fun searchIndexDao(): SearchIndexDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun appLockConfigDao(): AppLockConfigDao
    abstract fun crashLogDao(): CrashLogDao
}
