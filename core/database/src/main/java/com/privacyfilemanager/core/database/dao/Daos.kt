package com.privacyfilemanager.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.privacyfilemanager.core.database.entity.*
import kotlinx.coroutines.flow.Flow

// ===== Recent Files =====
@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastAccessed DESC")
    fun getRecentFiles(): Flow<List<RecentFileEntity>>

    @Query("SELECT * FROM recent_files ORDER BY lastAccessed DESC LIMIT :limit")
    fun getRecentFilesLimited(limit: Int): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE path = :path")
    suspend fun deleteByPath(path: String)

    @Query("DELETE FROM recent_files")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM recent_files")
    suspend fun getCount(): Int
}

// ===== Bookmarks =====
@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE path = :path")
    suspend fun deleteByPath(path: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE path = :path)")
    suspend fun isBookmarked(path: String): Boolean
}

// ===== File Tags =====
@Dao
interface FileTagDao {
    @Query("SELECT * FROM file_tags WHERE filePath = :filePath")
    fun getTagsForFile(filePath: String): Flow<List<FileTagEntity>>

    @Query("SELECT DISTINCT tag FROM file_tags ORDER BY tag")
    fun getAllTags(): Flow<List<String>>

    @Query("SELECT * FROM file_tags WHERE tag = :tag")
    fun getFilesByTag(tag: String): Flow<List<FileTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: FileTagEntity)

    @Delete
    suspend fun delete(tag: FileTagEntity)
}

// ===== Vault Files =====
@Dao
interface VaultFileDao {
    @Query("SELECT * FROM vault_files ORDER BY hiddenAt DESC")
    fun getAllVaultFiles(): Flow<List<VaultFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: VaultFileEntity)

    @Delete
    suspend fun delete(file: VaultFileEntity)

    @Query("DELETE FROM vault_files WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM vault_files WHERE originalPath = :originalPath")
    suspend fun getByOriginalPath(originalPath: String): VaultFileEntity?
}

// ===== Automation Rules =====
@Dao
interface AutomationRuleDao {
    @Query("SELECT * FROM automation_rules ORDER BY name")
    fun getAllRules(): Flow<List<AutomationRuleEntity>>

    @Query("SELECT * FROM automation_rules WHERE enabled = 1")
    suspend fun getEnabledRules(): List<AutomationRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: AutomationRuleEntity)

    @Update
    suspend fun update(rule: AutomationRuleEntity)

    @Delete
    suspend fun delete(rule: AutomationRuleEntity)
}

// ===== Search Index =====
@Dao
interface SearchIndexDao {
    @Query("SELECT * FROM search_index WHERE fileName LIKE '%' || :query || '%' ORDER BY lastModified DESC")
    fun search(query: String): PagingSource<Int, SearchIndexEntity>

    @Query("SELECT * FROM search_index WHERE fileName LIKE '%' || :query || '%' AND mimeType LIKE :mimeFilter || '%' ORDER BY lastModified DESC")
    fun searchWithFilter(query: String, mimeFilter: String): PagingSource<Int, SearchIndexEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<SearchIndexEntity>)

    @Query("DELETE FROM search_index")
    suspend fun clearIndex()

    @Query("SELECT COUNT(*) FROM search_index")
    suspend fun getIndexSize(): Int
}

// ===== Search History =====
@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}

// ===== App Lock Config =====
@Dao
interface AppLockConfigDao {
    @Query("SELECT * FROM app_lock_config WHERE id = 1")
    suspend fun getConfig(): AppLockConfigEntity?

    @Query("SELECT * FROM app_lock_config WHERE id = 1")
    fun observeConfig(): Flow<AppLockConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: AppLockConfigEntity)
}

// ===== Crash Logs =====
@Dao
interface CrashLogDao {
    @Query("SELECT * FROM crash_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CrashLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CrashLogEntity)

    @Query("DELETE FROM crash_logs")
    suspend fun clearAll()

    @Query("DELETE FROM crash_logs WHERE id NOT IN (SELECT id FROM crash_logs ORDER BY timestamp DESC LIMIT 50)")
    suspend fun trimOldLogs()
}
