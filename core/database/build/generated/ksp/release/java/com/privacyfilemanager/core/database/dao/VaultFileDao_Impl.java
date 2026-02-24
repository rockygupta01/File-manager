package com.privacyfilemanager.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privacyfilemanager.core.database.entity.VaultFileEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class VaultFileDao_Impl implements VaultFileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VaultFileEntity> __insertionAdapterOfVaultFileEntity;

  private final EntityDeletionOrUpdateAdapter<VaultFileEntity> __deletionAdapterOfVaultFileEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public VaultFileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVaultFileEntity = new EntityInsertionAdapter<VaultFileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vault_files` (`id`,`originalPath`,`encryptedPath`,`iv`,`originalName`,`mimeType`,`size`,`hiddenAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VaultFileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getOriginalPath());
        statement.bindString(3, entity.getEncryptedPath());
        statement.bindString(4, entity.getIv());
        statement.bindString(5, entity.getOriginalName());
        statement.bindString(6, entity.getMimeType());
        statement.bindLong(7, entity.getSize());
        statement.bindLong(8, entity.getHiddenAt());
      }
    };
    this.__deletionAdapterOfVaultFileEntity = new EntityDeletionOrUpdateAdapter<VaultFileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `vault_files` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VaultFileEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vault_files WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VaultFileEntity file, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVaultFileEntity.insert(file);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VaultFileEntity file, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVaultFileEntity.handle(file);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final int id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VaultFileEntity>> getAllVaultFiles() {
    final String _sql = "SELECT * FROM vault_files ORDER BY hiddenAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vault_files"}, new Callable<List<VaultFileEntity>>() {
      @Override
      @NonNull
      public List<VaultFileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOriginalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "originalPath");
          final int _cursorIndexOfEncryptedPath = CursorUtil.getColumnIndexOrThrow(_cursor, "encryptedPath");
          final int _cursorIndexOfIv = CursorUtil.getColumnIndexOrThrow(_cursor, "iv");
          final int _cursorIndexOfOriginalName = CursorUtil.getColumnIndexOrThrow(_cursor, "originalName");
          final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfHiddenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "hiddenAt");
          final List<VaultFileEntity> _result = new ArrayList<VaultFileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VaultFileEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpOriginalPath;
            _tmpOriginalPath = _cursor.getString(_cursorIndexOfOriginalPath);
            final String _tmpEncryptedPath;
            _tmpEncryptedPath = _cursor.getString(_cursorIndexOfEncryptedPath);
            final String _tmpIv;
            _tmpIv = _cursor.getString(_cursorIndexOfIv);
            final String _tmpOriginalName;
            _tmpOriginalName = _cursor.getString(_cursorIndexOfOriginalName);
            final String _tmpMimeType;
            _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
            final long _tmpSize;
            _tmpSize = _cursor.getLong(_cursorIndexOfSize);
            final long _tmpHiddenAt;
            _tmpHiddenAt = _cursor.getLong(_cursorIndexOfHiddenAt);
            _item = new VaultFileEntity(_tmpId,_tmpOriginalPath,_tmpEncryptedPath,_tmpIv,_tmpOriginalName,_tmpMimeType,_tmpSize,_tmpHiddenAt);
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
  public Object getByOriginalPath(final String originalPath,
      final Continuation<? super VaultFileEntity> $completion) {
    final String _sql = "SELECT * FROM vault_files WHERE originalPath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, originalPath);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VaultFileEntity>() {
      @Override
      @Nullable
      public VaultFileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOriginalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "originalPath");
          final int _cursorIndexOfEncryptedPath = CursorUtil.getColumnIndexOrThrow(_cursor, "encryptedPath");
          final int _cursorIndexOfIv = CursorUtil.getColumnIndexOrThrow(_cursor, "iv");
          final int _cursorIndexOfOriginalName = CursorUtil.getColumnIndexOrThrow(_cursor, "originalName");
          final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfHiddenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "hiddenAt");
          final VaultFileEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpOriginalPath;
            _tmpOriginalPath = _cursor.getString(_cursorIndexOfOriginalPath);
            final String _tmpEncryptedPath;
            _tmpEncryptedPath = _cursor.getString(_cursorIndexOfEncryptedPath);
            final String _tmpIv;
            _tmpIv = _cursor.getString(_cursorIndexOfIv);
            final String _tmpOriginalName;
            _tmpOriginalName = _cursor.getString(_cursorIndexOfOriginalName);
            final String _tmpMimeType;
            _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
            final long _tmpSize;
            _tmpSize = _cursor.getLong(_cursorIndexOfSize);
            final long _tmpHiddenAt;
            _tmpHiddenAt = _cursor.getLong(_cursorIndexOfHiddenAt);
            _result = new VaultFileEntity(_tmpId,_tmpOriginalPath,_tmpEncryptedPath,_tmpIv,_tmpOriginalName,_tmpMimeType,_tmpSize,_tmpHiddenAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
