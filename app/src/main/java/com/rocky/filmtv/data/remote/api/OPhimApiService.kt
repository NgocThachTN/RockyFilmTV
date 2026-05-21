package com.rocky.filmtv.data.remote.api

import com.rocky.filmtv.data.remote.dto.MovieDetailResponse
import com.rocky.filmtv.data.remote.dto.MovieMoiResponse
import com.rocky.filmtv.data.remote.dto.V1MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OPhimApiService {

    @GET("danh-sach/phim-moi-cap-nhat")
    suspend fun getPhimMoiCapNhat(
        @Query("page") page: Int
    ): MovieMoiResponse

    @GET("v1/api/danh-sach/{type}")
    suspend fun getPhimTheoTheLoai(
        @Path("type") type: String,
        @Query("page") page: Int
    ): V1MovieResponse

    @GET("v1/api/the-loai/{slug}")
    suspend fun getPhimTheoTheLoaiV1(
        @Path("slug") slug: String,
        @Query("page") page: Int
    ): V1MovieResponse

    @GET("v1/api/quoc-gia/{slug}")
    suspend fun getPhimTheoQuocGiaV1(
        @Path("slug") slug: String,
        @Query("page") page: Int
    ): V1MovieResponse

    @GET("phim/{slug}")
    suspend fun getChiTietPhim(
        @Path("slug") slug: String
    ): MovieDetailResponse

    @GET("v1/api/tim-kiem")
    suspend fun timKiemPhim(
        @Query("keyword") keyword: String,
        @Query("page") page: Int
    ): V1MovieResponse

    companion object {
        const val BASE_URL = "https://ophim1.com/"
        const val IMAGE_BASE_URL = "https://img.ophim.live/uploads/movies/"
    }
}
