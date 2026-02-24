package com.privacyfilemanager.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.paging.PagingSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.paging.LimitOffsetPagingSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privacyfilemanager.core.database.entity.SearchIndexEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SearchIndexDao_Impl implements SearchIndexDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchIndexEntity> __insertionAdapterOfSearchIndexEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearIndex;

  public SearchIndexDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchIndexEntity = new EntityInsertionAdapter<SearchIndexEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `search_index` (`id`,`filePath`,`fileName`,`mimeType`,`size`,`lastModified`,`parentDir`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchIndexEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFilePath());
        statement.bindString(3, entity.getFileName());
        statement.bindString(4, entity.getMimeType());
        statement.bindLong(5, entity.getSize());
        statement.bindLong(6, entity.getLastModified());
        statement.bindString(7, entity.getParentDir());
      }
    };
    this.__preparedStmtOfClearIndex = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_index";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<SearchIndexEntity> entries,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSearchIndexEntity.insert(entries);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearIndex(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearIndex.acquire();
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
          __preparedStmtOfClearIndex.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public PagingSource<Integer, SearchIndexEntity> search(final String query) {
    final String _sql = "SELECT * FROM search_index WHERE fileName LIKE '%' || ? || '%' ORDER BY lastModified DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return new LimitOffsetPagingSource<SearchIndexEntity>(_statement, __db, "search_index") {
      @Override
      @NonNull
      protected List<SearchIndexEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(cursor, "filePath");
        final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(cursor, "fileName");
        final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(cursor, "mimeType");
        final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(cursor, "size");
        final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(cursor, "lastModified");
        final int _cursorIndexOfParentDir = CursorUtil.getColumnIndexOrThrow(cursor, "parentDir");
        final List<SearchIndexEntity> _result = new ArrayList<SearchIndexEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final SearchIndexEntity _item;
          final int _tmpId;
          _tmpId = cursor.getInt(_cursorIndexOfId);
          final String _tmpFilePath;
          _tmpFilePath = cursor.getString(_cursorIndexOfFilePath);
          final String _tmpFileName;
          _tmpFileName = cursor.getString(_cursorIndexOfFileName);
          final String _tmpMimeType;
          _tmpMimeType = cursor.getString(_cursorIndexOfMimeType);
          final long _tmpSize;
          _tmpSize = cursor.getLong(_cursorIndexOfSize);
          final long _tmpLastModified;
          _tmpLastModified = cursor.getLong(_cursorIndexOfLastModified);
          final String _tmpParentDir;
          _tmpParentDir = cursor.getString(_cursorIndexOfParentDir);
          _item = new SearchIndexEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpMimeType,_tmpSize,_tmpLastModified,_tmpParentDir);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, SearchIndexEntity> searchWithFilter(final String query,
      final String mimeFilter) {
    final String _sql = "SELECT * FROM search_index WHERE fileName LIKE '%' || ? || '%' AND mimeType LIKE ? || '%' ORDER BY lastModified DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, mimeFilter);
    return new LimitOffsetPagingSource<SearchIndexEntity>(_statement, __db, "search_index") {
      @Override
      @NonNull
      protected List<SearchIndexEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(cursor, "filePath");
        final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(cursor, "fileName");
        final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(cursor, "mimeType");
        final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(cursor, "size");
        final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(cursor, "lastModified");
        final int _cursorIndexOfParentDir = CursorUtil.getColumnIndexOrThrow(cursor, "parentDir");
        final List<SearchIndexEntity> _result = new ArrayList<SearchIndexEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final SearchIndexEntity _item;
          final int _tmpId;
          _tmpId = cursor.getInt(_cursorIndexOfId);
          final String _tmpFilePath;
          _tmpFilePath = cursor.getString(_cursorIndexOfFilePath);
          final String _tmpFileName;
          _tmpFileName = cursor.getString(_cursorIndexOfFileName);
          final String _tmpMimeType;
          _tmpMimeType = cursor.getString(_cursorIndexOfMimeType);
          final long _tmpSize;
          _tmpSize = cursor.getLong(_cursorIndexOfSize);
          final long _tmpLastModified;
          _tmpLastModified = cursor.getLong(_cursorIndexOfLastModified);
          final String _tmpParentDir;
          _tmpParentDir = cursor.getString(_cursorIndexOfParentDir);
          _item = new SearchIndexEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpMimeType,_tmpSize,_tmpLastModified,_tmpParentDir);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public Object getIndexSize(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM search_index";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
