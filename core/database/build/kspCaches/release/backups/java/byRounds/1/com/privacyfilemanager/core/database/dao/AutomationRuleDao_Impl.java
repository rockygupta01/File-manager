package com.privacyfilemanager.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privacyfilemanager.core.database.entity.AutomationRuleEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AutomationRuleDao_Impl implements AutomationRuleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AutomationRuleEntity> __insertionAdapterOfAutomationRuleEntity;

  private final EntityDeletionOrUpdateAdapter<AutomationRuleEntity> __deletionAdapterOfAutomationRuleEntity;

  private final EntityDeletionOrUpdateAdapter<AutomationRuleEntity> __updateAdapterOfAutomationRuleEntity;

  public AutomationRuleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAutomationRuleEntity = new EntityInsertionAdapter<AutomationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `automation_rules` (`id`,`name`,`sourceDir`,`destDir`,`typeFilter`,`schedule`,`enabled`,`lastRun`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AutomationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSourceDir());
        statement.bindString(4, entity.getDestDir());
        statement.bindString(5, entity.getTypeFilter());
        statement.bindString(6, entity.getSchedule());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getLastRun() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastRun());
        }
      }
    };
    this.__deletionAdapterOfAutomationRuleEntity = new EntityDeletionOrUpdateAdapter<AutomationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `automation_rules` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AutomationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAutomationRuleEntity = new EntityDeletionOrUpdateAdapter<AutomationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `automation_rules` SET `id` = ?,`name` = ?,`sourceDir` = ?,`destDir` = ?,`typeFilter` = ?,`schedule` = ?,`enabled` = ?,`lastRun` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AutomationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSourceDir());
        statement.bindString(4, entity.getDestDir());
        statement.bindString(5, entity.getTypeFilter());
        statement.bindString(6, entity.getSchedule());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getLastRun() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastRun());
        }
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final AutomationRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAutomationRuleEntity.insert(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AutomationRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAutomationRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AutomationRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAutomationRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AutomationRuleEntity>> getAllRules() {
    final String _sql = "SELECT * FROM automation_rules ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"automation_rules"}, new Callable<List<AutomationRuleEntity>>() {
      @Override
      @NonNull
      public List<AutomationRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSourceDir = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceDir");
          final int _cursorIndexOfDestDir = CursorUtil.getColumnIndexOrThrow(_cursor, "destDir");
          final int _cursorIndexOfTypeFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "typeFilter");
          final int _cursorIndexOfSchedule = CursorUtil.getColumnIndexOrThrow(_cursor, "schedule");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfLastRun = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRun");
          final List<AutomationRuleEntity> _result = new ArrayList<AutomationRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutomationRuleEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSourceDir;
            _tmpSourceDir = _cursor.getString(_cursorIndexOfSourceDir);
            final String _tmpDestDir;
            _tmpDestDir = _cursor.getString(_cursorIndexOfDestDir);
            final String _tmpTypeFilter;
            _tmpTypeFilter = _cursor.getString(_cursorIndexOfTypeFilter);
            final String _tmpSchedule;
            _tmpSchedule = _cursor.getString(_cursorIndexOfSchedule);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final Long _tmpLastRun;
            if (_cursor.isNull(_cursorIndexOfLastRun)) {
              _tmpLastRun = null;
            } else {
              _tmpLastRun = _cursor.getLong(_cursorIndexOfLastRun);
            }
            _item = new AutomationRuleEntity(_tmpId,_tmpName,_tmpSourceDir,_tmpDestDir,_tmpTypeFilter,_tmpSchedule,_tmpEnabled,_tmpLastRun);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEnabledRules(
      final Continuation<? super List<AutomationRuleEntity>> $completion) {
    final String _sql = "SELECT * FROM automation_rules WHERE enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AutomationRuleEntity>>() {
      @Override
      @NonNull
      public List<AutomationRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSourceDir = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceDir");
          final int _cursorIndexOfDestDir = CursorUtil.getColumnIndexOrThrow(_cursor, "destDir");
          final int _cursorIndexOfTypeFilter = CursorUtil.getColumnIndexOrThrow(_cursor, "typeFilter");
          final int _cursorIndexOfSchedule = CursorUtil.getColumnIndexOrThrow(_cursor, "schedule");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfLastRun = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRun");
          final List<AutomationRuleEntity> _result = new ArrayList<AutomationRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutomationRuleEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSourceDir;
            _tmpSourceDir = _cursor.getString(_cursorIndexOfSourceDir);
            final String _tmpDestDir;
            _tmpDestDir = _cursor.getString(_cursorIndexOfDestDir);
            final String _tmpTypeFilter;
            _tmpTypeFilter = _cursor.getString(_cursorIndexOfTypeFilter);
            final String _tmpSchedule;
            _tmpSchedule = _cursor.getString(_cursorIndexOfSchedule);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final Long _tmpLastRun;
            if (_cursor.isNull(_cursorIndexOfLastRun)) {
              _tmpLastRun = null;
            } else {
              _tmpLastRun = _cursor.getLong(_cursorIndexOfLastRun);
            }
            _item = new AutomationRuleEntity(_tmpId,_tmpName,_tmpSourceDir,_tmpDestDir,_tmpTypeFilter,_tmpSchedule,_tmpEnabled,_tmpLastRun);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
