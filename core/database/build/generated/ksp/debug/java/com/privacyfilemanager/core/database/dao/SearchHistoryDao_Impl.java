package com.privacyfilemanager.core.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.privacyfilemanager.core.database.entity.SearchHistoryEntity;
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
public final class SearchHistoryDao_Impl implements SearchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchHistoryEntity> __insertionAdapterOfSearchHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearHistory;

  public SearchHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchHistoryEntity = new EntityInsertionAdapter<SearchHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `search_history` (`id`,`query`,`timestamp`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getQuery());
        statement.bindLong(3, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfClearHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_history";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SearchHistoryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSearchHistoryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearHistory.acquire();
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
          __preparedStmtOfClearHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SearchHistoryEntity>> getRecentSearches() {
    final String _sql = "SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"search_history"}, new Callable<List<SearchHistoryEntity>>() {
      @Override
      @NonNull
      public List<SearchHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuery = CursorUtil.getColumnIndexOrThrow(_cursor, "query");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<SearchHistoryEntity> _result = new ArrayList<SearchHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchHistoryEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpQuery;
            _tmpQuery = _cursor.getString(_cursorIndexOfQuery);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new SearchHistoryEntity(_tmpId,_tmpQuery,_tmpTimestamp);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
