package com.rocky.filmtv.data.repository

import com.rocky.filmtv.data.local.database.FavoriteDao
import com.rocky.filmtv.data.local.database.FavoriteMovieEntity
import com.rocky.filmtv.data.local.database.HistoryDao
import com.rocky.filmtv.data.local.database.WatchHistoryEntity
import com.rocky.filmtv.data.remote.api.OPhimApiService
import com.rocky.filmtv.data.remote.mapper.Movie
import com.rocky.filmtv.data.remote.mapper.MovieDetail
import com.rocky.filmtv.data.remote.mapper.toMovie
import com.rocky.filmtv.data.remote.mapper.toMovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: OPhimApiService,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {

    suspend fun getPhimMoiCapNhat(page: Int): List<Movie> {
        val response = apiService.getPhimMoiCapNhat(page)
        return response.items?.map { it.toMovie() } ?: emptyList()
    }

    suspend fun getPhimTheoTheLoai(type: String, page: Int): List<Movie> {
        val response = apiService.getPhimTheoTheLoai(type, page)
        return response.data?.items?.map { it.toMovie() } ?: emptyList()
    }

    suspend fun getChiTietPhim(slug: String): MovieDetail {
        val response = apiService.getChiTietPhim(slug)
        val movieDetailDto = response.movie ?: throw Exception("Movie not found")
        return movieDetailDto.toMovieDetail(response.episodes)
    }

    suspend fun timKiemPhim(keyword: String, page: Int): List<Movie> {
        val response = apiService.timKiemPhim(keyword, page)
        return response.data?.items?.map { it.toMovie() } ?: emptyList()
    }

    fun getAllFavorites(): Flow<List<Movie>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Movie(
                    id = entity.id,
                    name = entity.name,
                    slug = entity.slug,
                    originName = entity.originName,
                    posterUrl = entity.posterUrl,
                    thumbUrl = entity.thumbUrl,
                    type = entity.type,
                    year = entity.year
                )
            }
        }
    }

    suspend fun insertFavorite(movie: Movie) {
        val entity = FavoriteMovieEntity(
            id = movie.id,
            name = movie.name,
            slug = movie.slug,
            originName = movie.originName,
            posterUrl = movie.posterUrl,
            thumbUrl = movie.thumbUrl,
            type = movie.type,
            year = movie.year
        )
        favoriteDao.insertFavorite(entity)
    }

    suspend fun insertFavorite(movie: MovieDetail) {
        val entity = FavoriteMovieEntity(
            id = movie.id,
            name = movie.name,
            slug = movie.slug,
            originName = movie.originName,
            posterUrl = movie.posterUrl,
            thumbUrl = movie.thumbUrl,
            type = movie.type,
            year = movie.year
        )
        favoriteDao.insertFavorite(entity)
    }

    suspend fun deleteFavorite(id: String) {
        val entity = FavoriteMovieEntity(
            id = id,
            name = "",
            slug = "",
            originName = "",
            posterUrl = "",
            thumbUrl = "",
            type = "",
            year = 0
        )
        favoriteDao.deleteFavorite(entity)
    }

    fun isFavorite(id: String): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    fun getAllHistory(): Flow<List<WatchHistoryEntity>> {
        return historyDao.getAllHistory()
    }

    suspend fun insertHistory(history: WatchHistoryEntity) {
        historyDao.insertHistory(history)
    }

    suspend fun deleteHistoryById(id: String) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun getHistoryById(id: String): WatchHistoryEntity? {
        return historyDao.getHistoryById(id)
    }
}
