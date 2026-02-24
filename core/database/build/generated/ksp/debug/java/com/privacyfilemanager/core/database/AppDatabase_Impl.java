package com.privacyfilemanager.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.privacyfilemanager.core.database.dao.AppLockConfigDao;
import com.privacyfilemanager.core.database.dao.AppLockConfigDao_Impl;
import com.privacyfilemanager.core.database.dao.AutomationRuleDao;
import com.privacyfilemanager.core.database.dao.AutomationRuleDao_Impl;
import com.privacyfilemanager.core.database.dao.BookmarkDao;
import com.privacyfilemanager.core.database.dao.BookmarkDao_Impl;
import com.privacyfilemanager.core.database.dao.CrashLogDao;
import com.privacyfilemanager.core.database.dao.CrashLogDao_Impl;
import com.privacyfilemanager.core.database.dao.FileTagDao;
import com.privacyfilemanager.core.database.dao.FileTagDao_Impl;
import com.privacyfilemanager.core.database.dao.RecentFileDao;
import com.privacyfilemanager.core.database.dao.RecentFileDao_Impl;
import com.privacyfilemanager.core.database.dao.SearchHistoryDao;
import com.privacyfilemanager.core.database.dao.SearchHistoryDao_Impl;
import com.privacyfilemanager.core.database.dao.SearchIndexDao;
import com.privacyfilemanager.core.database.dao.SearchIndexDao_Impl;
import com.privacyfilemanager.core.database.dao.VaultFileDao;
import com.privacyfilemanager.core.database.dao.VaultFileDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RecentFileDao _recentFileDao;

  private volatile BookmarkDao _bookmarkDao;

  private volatile FileTagDao _fileTagDao;

  private volatile VaultFileDao _vaultFileDao;

  private volatile AutomationRuleDao _automationRuleDao;

  private volatile SearchIndexDao _searchIndexDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile AppLockConfigDao _appLockConfigDao;

  private volatile CrashLogDao _crashLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent_files` (`path` TEXT NOT NULL, `lastAccessed` INTEGER NOT NULL, `mimeType` TEXT NOT NULL, `size` INTEGER NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`path`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `path` TEXT NOT NULL, `label` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `file_tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `tag` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vault_files` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `originalPath` TEXT NOT NULL, `encryptedPath` TEXT NOT NULL, `iv` TEXT NOT NULL, `originalName` TEXT NOT NULL, `mimeType` TEXT NOT NULL, `size` INTEGER NOT NULL, `hiddenAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `automation_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `sourceDir` TEXT NOT NULL, `destDir` TEXT NOT NULL, `typeFilter` TEXT NOT NULL, `schedule` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `lastRun` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_index` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `mimeType` TEXT NOT NULL, `size` INTEGER NOT NULL, `lastModified` INTEGER NOT NULL, `parentDir` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `query` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_lock_config` (`id` INTEGER NOT NULL, `lockType` TEXT NOT NULL, `hashedCredential` TEXT, `timeoutMinutes` INTEGER NOT NULL, `biometricEnabled` INTEGER NOT NULL, `failedAttempts` INTEGER NOT NULL, `lockoutUntil` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crash_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `threadName` TEXT NOT NULL, `stackTrace` TEXT NOT NULL, `deviceInfo` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '00d7f0f745a39926a949166ccf0806a7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `recent_files`");
        db.execSQL("DROP TABLE IF EXISTS `bookmarks`");
        db.execSQL("DROP TABLE IF EXISTS `file_tags`");
        db.execSQL("DROP TABLE IF EXISTS `vault_files`");
        db.execSQL("DROP TABLE IF EXISTS `automation_rules`");
        db.execSQL("DROP TABLE IF EXISTS `search_index`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `app_lock_config`");
        db.execSQL("DROP TABLE IF EXISTS `crash_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRecentFiles = new HashMap<String, TableInfo.Column>(5);
        _columnsRecentFiles.put("path", new TableInfo.Column("path", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("lastAccessed", new TableInfo.Column("lastAccessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecentFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecentFiles = new TableInfo("recent_files", _columnsRecentFiles, _foreignKeysRecentFiles, _indicesRecentFiles);
        final TableInfo _existingRecentFiles = TableInfo.read(db, "recent_files");
        if (!_infoRecentFiles.equals(_existingRecentFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "recent_files(com.privacyfilemanager.core.database.entity.RecentFileEntity).\n"
                  + " Expected:\n" + _infoRecentFiles + "\n"
                  + " Found:\n" + _existingRecentFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarks = new HashMap<String, TableInfo.Column>(4);
        _columnsBookmarks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("path", new TableInfo.Column("path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarks = new TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks);
        final TableInfo _existingBookmarks = TableInfo.read(db, "bookmarks");
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmarks(com.privacyfilemanager.core.database.entity.BookmarkEntity).\n"
                  + " Expected:\n" + _infoBookmarks + "\n"
                  + " Found:\n" + _existingBookmarks);
        }
        final HashMap<String, TableInfo.Column> _columnsFileTags = new HashMap<String, TableInfo.Column>(3);
        _columnsFileTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTags.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFileTags.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFileTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFileTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFileTags = new TableInfo("file_tags", _columnsFileTags, _foreignKeysFileTags, _indicesFileTags);
        final TableInfo _existingFileTags = TableInfo.read(db, "file_tags");
        if (!_infoFileTags.equals(_existingFileTags)) {
          return new RoomOpenHelper.ValidationResult(false, "file_tags(com.privacyfilemanager.core.database.entity.FileTagEntity).\n"
                  + " Expected:\n" + _infoFileTags + "\n"
                  + " Found:\n" + _existingFileTags);
        }
        final HashMap<String, TableInfo.Column> _columnsVaultFiles = new HashMap<String, TableInfo.Column>(8);
        _columnsVaultFiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("originalPath", new TableInfo.Column("originalPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("encryptedPath", new TableInfo.Column("encryptedPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("iv", new TableInfo.Column("iv", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("originalName", new TableInfo.Column("originalName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("hiddenAt", new TableInfo.Column("hiddenAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaultFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVaultFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVaultFiles = new TableInfo("vault_files", _columnsVaultFiles, _foreignKeysVaultFiles, _indicesVaultFiles);
        final TableInfo _existingVaultFiles = TableInfo.read(db, "vault_files");
        if (!_infoVaultFiles.equals(_existingVaultFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "vault_files(com.privacyfilemanager.core.database.entity.VaultFileEntity).\n"
                  + " Expected:\n" + _infoVaultFiles + "\n"
                  + " Found:\n" + _existingVaultFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsAutomationRules = new HashMap<String, TableInfo.Column>(8);
        _columnsAutomationRules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("sourceDir", new TableInfo.Column("sourceDir", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("destDir", new TableInfo.Column("destDir", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("typeFilter", new TableInfo.Column("typeFilter", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("schedule", new TableInfo.Column("schedule", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutomationRules.put("lastRun", new TableInfo.Column("lastRun", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAutomationRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAutomationRules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAutomationRules = new TableInfo("automation_rules", _columnsAutomationRules, _foreignKeysAutomationRules, _indicesAutomationRules);
        final TableInfo _existingAutomationRules = TableInfo.read(db, "automation_rules");
        if (!_infoAutomationRules.equals(_existingAutomationRules)) {
          return new RoomOpenHelper.ValidationResult(false, "automation_rules(com.privacyfilemanager.core.database.entity.AutomationRuleEntity).\n"
                  + " Expected:\n" + _infoAutomationRules + "\n"
                  + " Found:\n" + _existingAutomationRules);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchIndex = new HashMap<String, TableInfo.Column>(7);
        _columnsSearchIndex.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("parentDir", new TableInfo.Column("parentDir", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchIndex = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchIndex = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchIndex = new TableInfo("search_index", _columnsSearchIndex, _foreignKeysSearchIndex, _indicesSearchIndex);
        final TableInfo _existingSearchIndex = TableInfo.read(db, "search_index");
        if (!_infoSearchIndex.equals(_existingSearchIndex)) {
          return new RoomOpenHelper.ValidationResult(false, "search_index(com.privacyfilemanager.core.database.entity.SearchIndexEntity).\n"
                  + " Expected:\n" + _infoSearchIndex + "\n"
                  + " Found:\n" + _existingSearchIndex);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(3);
        _columnsSearchHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("query", new TableInfo.Column("query", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.privacyfilemanager.core.database.entity.SearchHistoryEntity).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsAppLockConfig = new HashMap<String, TableInfo.Column>(7);
        _columnsAppLockConfig.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("lockType", new TableInfo.Column("lockType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("hashedCredential", new TableInfo.Column("hashedCredential", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("timeoutMinutes", new TableInfo.Column("timeoutMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("biometricEnabled", new TableInfo.Column("biometricEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("failedAttempts", new TableInfo.Column("failedAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLockConfig.put("lockoutUntil", new TableInfo.Column("lockoutUntil", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppLockConfig = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppLockConfig = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAppLockConfig = new TableInfo("app_lock_config", _columnsAppLockConfig, _foreignKeysAppLockConfig, _indicesAppLockConfig);
        final TableInfo _existingAppLockConfig = TableInfo.read(db, "app_lock_config");
        if (!_infoAppLockConfig.equals(_existingAppLockConfig)) {
          return new RoomOpenHelper.ValidationResult(false, "app_lock_config(com.privacyfilemanager.core.database.entity.AppLockConfigEntity).\n"
                  + " Expected:\n" + _infoAppLockConfig + "\n"
                  + " Found:\n" + _existingAppLockConfig);
        }
        final HashMap<String, TableInfo.Column> _columnsCrashLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsCrashLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrashLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrashLogs.put("threadName", new TableInfo.Column("threadName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrashLogs.put("stackTrace", new TableInfo.Column("stackTrace", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrashLogs.put("deviceInfo", new TableInfo.Column("deviceInfo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCrashLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCrashLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCrashLogs = new TableInfo("crash_logs", _columnsCrashLogs, _foreignKeysCrashLogs, _indicesCrashLogs);
        final TableInfo _existingCrashLogs = TableInfo.read(db, "crash_logs");
        if (!_infoCrashLogs.equals(_existingCrashLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "crash_logs(com.privacyfilemanager.core.database.entity.CrashLogEntity).\n"
                  + " Expected:\n" + _infoCrashLogs + "\n"
                  + " Found:\n" + _existingCrashLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "00d7f0f745a39926a949166ccf0806a7", "d49b7414cab2f8a545bad9f483c5354b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "recent_files","bookmarks","file_tags","vault_files","automation_rules","search_index","search_history","app_lock_config","crash_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `recent_files`");
      _db.execSQL("DELETE FROM `bookmarks`");
      _db.execSQL("DELETE FROM `file_tags`");
      _db.execSQL("DELETE FROM `vault_files`");
      _db.execSQL("DELETE FROM `automation_rules`");
      _db.execSQL("DELETE FROM `search_index`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `app_lock_config`");
      _db.execSQL("DELETE FROM `crash_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RecentFileDao.class, RecentFileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkDao.class, BookmarkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FileTagDao.class, FileTagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VaultFileDao.class, VaultFileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AutomationRuleDao.class, AutomationRuleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchIndexDao.class, SearchIndexDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AppLockConfigDao.class, AppLockConfigDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CrashLogDao.class, CrashLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RecentFileDao recentFileDao() {
    if (_recentFileDao != null) {
      return _recentFileDao;
    } else {
      synchronized(this) {
        if(_recentFileDao == null) {
          _recentFileDao = new RecentFileDao_Impl(this);
        }
        return _recentFileDao;
      }
    }
  }

  @Override
  public BookmarkDao bookmarkDao() {
    if (_bookmarkDao != null) {
      return _bookmarkDao;
    } else {
      synchronized(this) {
        if(_bookmarkDao == null) {
          _bookmarkDao = new BookmarkDao_Impl(this);
        }
        return _bookmarkDao;
      }
    }
  }

  @Override
  public FileTagDao fileTagDao() {
    if (_fileTagDao != null) {
      return _fileTagDao;
    } else {
      synchronized(this) {
        if(_fileTagDao == null) {
          _fileTagDao = new FileTagDao_Impl(this);
        }
        return _fileTagDao;
      }
    }
  }

  @Override
  public VaultFileDao vaultFileDao() {
    if (_vaultFileDao != null) {
      return _vaultFileDao;
    } else {
      synchronized(this) {
        if(_vaultFileDao == null) {
          _vaultFileDao = new VaultFileDao_Impl(this);
        }
        return _vaultFileDao;
      }
    }
  }

  @Override
  public AutomationRuleDao automationRuleDao() {
    if (_automationRuleDao != null) {
      return _automationRuleDao;
    } else {
      synchronized(this) {
        if(_automationRuleDao == null) {
          _automationRuleDao = new AutomationRuleDao_Impl(this);
        }
        return _automationRuleDao;
      }
    }
  }

  @Override
  public SearchIndexDao searchIndexDao() {
    if (_searchIndexDao != null) {
      return _searchIndexDao;
    } else {
      synchronized(this) {
        if(_searchIndexDao == null) {
          _searchIndexDao = new SearchIndexDao_Impl(this);
        }
        return _searchIndexDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public AppLockConfigDao appLockConfigDao() {
    if (_appLockConfigDao != null) {
      return _appLockConfigDao;
    } else {
      synchronized(this) {
        if(_appLockConfigDao == null) {
          _appLockConfigDao = new AppLockConfigDao_Impl(this);
        }
        return _appLockConfigDao;
      }
    }
  }

  @Override
  public CrashLogDao crashLogDao() {
    if (_crashLogDao != null) {
      return _crashLogDao;
    } else {
      synchronized(this) {
        if(_crashLogDao == null) {
          _crashLogDao = new CrashLogDao_Impl(this);
        }
        return _crashLogDao;
      }
    }
  }
}
