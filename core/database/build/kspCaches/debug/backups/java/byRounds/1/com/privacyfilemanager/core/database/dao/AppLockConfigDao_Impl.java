package com.privacyfilemanager.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privacyfilemanager.core.database.entity.AppLockConfigEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppLockConfigDao_Impl implements AppLockConfigDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppLockConfigEntity> __insertionAdapterOfAppLockConfigEntity;

  public AppLockConfigDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppLockConfigEntity = new EntityInsertionAdapter<AppLockConfigEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_lock_config` (`id`,`lockType`,`hashedCredential`,`timeoutMinutes`,`biometricEnabled`,`failedAttempts`,`lockoutUntil`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppLockConfigEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLockType());
        if (entity.getHashedCredential() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getHashedCredential());
        }
        statement.bindLong(4, entity.getTimeoutMinutes());
        final int _tmp = entity.getBiometricEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getFailedAttempts());
        if (entity.getLockoutUntil() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLockoutUntil());
        }
      }
    };
  }

  @Override
  public Object upsert(final AppLockConfigEntity config,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppLockConfigEntity.insert(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getConfig(final Continuation<? super AppLockConfigEntity> $completion) {
    final String _sql = "SELECT * FROM app_lock_config WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppLockConfigEntity>() {
      @Override
      @Nullable
      public AppLockConfigEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLockType = CursorUtil.getColumnIndexOrThrow(_cursor, "lockType");
          final int _cursorIndexOfHashedCredential = CursorUtil.getColumnIndexOrThrow(_cursor, "hashedCredential");
          final int _cursorIndexOfTimeoutMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timeoutMinutes");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfFailedAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final AppLockConfigEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpLockType;
            _tmpLockType = _cursor.getString(_cursorIndexOfLockType);
            final String _tmpHashedCredential;
            if (_cursor.isNull(_cursorIndexOfHashedCredential)) {
              _tmpHashedCredential = null;
            } else {
              _tmpHashedCredential = _cursor.getString(_cursorIndexOfHashedCredential);
            }
            final int _tmpTimeoutMinutes;
            _tmpTimeoutMinutes = _cursor.getInt(_cursorIndexOfTimeoutMinutes);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final int _tmpFailedAttempts;
            _tmpFailedAttempts = _cursor.getInt(_cursorIndexOfFailedAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new AppLockConfigEntity(_tmpId,_tmpLockType,_tmpHashedCredential,_tmpTimeoutMinutes,_tmpBiometricEnabled,_tmpFailedAttempts,_tmpLockoutUntil);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<AppLockConfigEntity> observeConfig() {
    final String _sql = "SELECT * FROM app_lock_config WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_lock_config"}, new Callable<AppLockConfigEntity>() {
      @Override
      @Nullable
      public AppLockConfigEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLockType = CursorUtil.getColumnIndexOrThrow(_cursor, "lockType");
          final int _cursorIndexOfHashedCredential = CursorUtil.getColumnIndexOrThrow(_cursor, "hashedCredential");
          final int _cursorIndexOfTimeoutMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timeoutMinutes");
          final int _cursorIndexOfBiometricEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricEnabled");
          final int _cursorIndexOfFailedAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedAttempts");
          final int _cursorIndexOfLockoutUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntil");
          final AppLockConfigEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpLockType;
            _tmpLockType = _cursor.getString(_cursorIndexOfLockType);
            final String _tmpHashedCredential;
            if (_cursor.isNull(_cursorIndexOfHashedCredential)) {
              _tmpHashedCredential = null;
            } else {
              _tmpHashedCredential = _cursor.getString(_cursorIndexOfHashedCredential);
            }
            final int _tmpTimeoutMinutes;
            _tmpTimeoutMinutes = _cursor.getInt(_cursorIndexOfTimeoutMinutes);
            final boolean _tmpBiometricEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfBiometricEnabled);
            _tmpBiometricEnabled = _tmp != 0;
            final int _tmpFailedAttempts;
            _tmpFailedAttempts = _cursor.getInt(_cursorIndexOfFailedAttempts);
            final Long _tmpLockoutUntil;
            if (_cursor.isNull(_cursorIndexOfLockoutUntil)) {
              _tmpLockoutUntil = null;
            } else {
              _tmpLockoutUntil = _cursor.getLong(_cursorIndexOfLockoutUntil);
            }
            _result = new AppLockConfigEntity(_tmpId,_tmpLockType,_tmpHashedCredential,_tmpTimeoutMinutes,_tmpBiometricEnabled,_tmpFailedAttempts,_tmpLockoutUntil);
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
