package com.rocky.filmtv.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("thumb_url") val thumbUrl: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("year") val year: Int?
)

data class PaginationDto(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("totalItemsPerPage") val totalItemsPerPage: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class MovieMoiResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("items") val items: List<MovieDto>?,
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class V1MovieResponse(
    @SerializedName("status") val status: Any?, // Can be Boolean or String
    @SerializedName("data") val data: V1DataDto?
)

data class V1DataDto(
    @SerializedName("items") val items: List<MovieDto>?,
    @SerializedName("params") val params: V1ParamsDto?
)

data class V1ParamsDto(
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class MovieDetailResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("msg") val msg: String?,
    @SerializedName("movie") val movie: MovieDetailDto?,
    @SerializedName("episodes") val episodes: List<EpisodeServerDto>?
)

data class MovieDetailDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("content") val content: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("thumb_url") val thumbUrl: String?,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("trailer_url") val trailerUrl: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("episode_current") val episodeCurrent: String?,
    @SerializedName("episode_total") val episodeTotal: String?,
    @SerializedName("quality") val quality: String?,
    @SerializedName("lang") val lang: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("actor") val actor: List<String>?,
    @SerializedName("director") val director: List<String>?,
    @SerializedName("category") val category: List<CategoryDto>?,
    @SerializedName("country") val country: List<CountryDto>?
)

data class CategoryDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)

data class CountryDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)

data class EpisodeServerDto(
    @SerializedName("server_name") val serverName: String,
    @SerializedName("server_data") val serverData: List<EpisodeDto>?
)

data class EpisodeDto(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("filename") val filename: String?,
    @SerializedName("link_embed") val linkEmbed: String?,
    @SerializedName("link_m3u8") val linkM3u8: String?
)
