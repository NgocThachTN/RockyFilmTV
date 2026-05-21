package com.rocky.filmtv.data.local.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteMovieEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val originName: String,
    val posterUrl: String,
    val thumbUrl: String,
    val type: String,
    val year: Int,
    val addedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val posterUrl: String,
    val lastWatchedEpisodeName: String,
    val lastWatchedEpisodeSlug: String,
    val lastWatchedPosition: Long,
    val totalDuration: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedTime DESC")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteMovieEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteMovieEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 20")
    fun getAllHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteHistoryById(id: String)

    @Query("SELECT * FROM watch_history WHERE id = :id LIMIT 1")
    suspend fun getHistoryById(id: String): WatchHistoryEntity?
}

@Database(entities = [FavoriteMovieEntity::class, WatchHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DATABASE_NAME = "rocky_filmtv.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
