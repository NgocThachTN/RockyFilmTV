package com.rocky.filmtv.data.local.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WatchHistoryEntity> __insertionAdapterOfWatchHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteHistoryById;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWatchHistoryEntity = new EntityInsertionAdapter<WatchHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_history` (`id`,`name`,`slug`,`posterUrl`,`lastWatchedEpisodeName`,`lastWatchedEpisodeSlug`,`lastWatchedPosition`,`totalDuration`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WatchHistoryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSlug());
        statement.bindString(4, entity.getPosterUrl());
        statement.bindString(5, entity.getLastWatchedEpisodeName());
        statement.bindString(6, entity.getLastWatchedEpisodeSlug());
        statement.bindLong(7, entity.getLastWatchedPosition());
        statement.bindLong(8, entity.getTotalDuration());
        statement.bindLong(9, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteHistoryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM watch_history WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertHistory(final WatchHistoryEntity history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWatchHistoryEntity.insert(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHistoryById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteHistoryById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfDeleteHistoryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WatchHistoryEntity>> getAllHistory() {
    final String _sql = "SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"watch_history"}, new Callable<List<WatchHistoryEntity>>() {
      @Override
      @NonNull
      public List<WatchHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "posterUrl");
          final int _cursorIndexOfLastWatchedEpisodeName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedEpisodeName");
          final int _cursorIndexOfLastWatchedEpisodeSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedEpisodeSlug");
          final int _cursorIndexOfLastWatchedPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedPosition");
          final int _cursorIndexOfTotalDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDuration");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<WatchHistoryEntity> _result = new ArrayList<WatchHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WatchHistoryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSlug;
            _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            final String _tmpPosterUrl;
            _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            final String _tmpLastWatchedEpisodeName;
            _tmpLastWatchedEpisodeName = _cursor.getString(_cursorIndexOfLastWatchedEpisodeName);
            final String _tmpLastWatchedEpisodeSlug;
            _tmpLastWatchedEpisodeSlug = _cursor.getString(_cursorIndexOfLastWatchedEpisodeSlug);
            final long _tmpLastWatchedPosition;
            _tmpLastWatchedPosition = _cursor.getLong(_cursorIndexOfLastWatchedPosition);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new WatchHistoryEntity(_tmpId,_tmpName,_tmpSlug,_tmpPosterUrl,_tmpLastWatchedEpisodeName,_tmpLastWatchedEpisodeSlug,_tmpLastWatchedPosition,_tmpTotalDuration,_tmpTimestamp);
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
  public Object getHistoryById(final String id,
      final Continuation<? super WatchHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM watch_history WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WatchHistoryEntity>() {
      @Override
      @Nullable
      public WatchHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "posterUrl");
          final int _cursorIndexOfLastWatchedEpisodeName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedEpisodeName");
          final int _cursorIndexOfLastWatchedEpisodeSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedEpisodeSlug");
          final int _cursorIndexOfLastWatchedPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedPosition");
          final int _cursorIndexOfTotalDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDuration");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final WatchHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSlug;
            _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            final String _tmpPosterUrl;
            _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            final String _tmpLastWatchedEpisodeName;
            _tmpLastWatchedEpisodeName = _cursor.getString(_cursorIndexOfLastWatchedEpisodeName);
            final String _tmpLastWatchedEpisodeSlug;
            _tmpLastWatchedEpisodeSlug = _cursor.getString(_cursorIndexOfLastWatchedEpisodeSlug);
            final long _tmpLastWatchedPosition;
            _tmpLastWatchedPosition = _cursor.getLong(_cursorIndexOfLastWatchedPosition);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new WatchHistoryEntity(_tmpId,_tmpName,_tmpSlug,_tmpPosterUrl,_tmpLastWatchedEpisodeName,_tmpLastWatchedEpisodeSlug,_tmpLastWatchedPosition,_tmpTotalDuration,_tmpTimestamp);
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
