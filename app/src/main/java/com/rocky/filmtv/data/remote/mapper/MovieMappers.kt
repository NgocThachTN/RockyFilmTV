package com.rocky.filmtv.data.remote.mapper

import com.rocky.filmtv.data.remote.api.OPhimApiService
import com.rocky.filmtv.data.remote.dto.*

data class Movie(
    val id: String,
    val name: String,
    val slug: String,
    val originName: String,
    val posterUrl: String,
    val thumbUrl: String,
    val type: String,
    val year: Int
)

data class MovieDetail(
    val id: String,
    val name: String,
    val slug: String,
    val originName: String,
    val content: String,
    val type: String,
    val status: String,
    val thumbUrl: String,
    val posterUrl: String,
    val trailerUrl: String,
    val time: String,
    val episodeCurrent: String,
    val episodeTotal: String,
    val quality: String,
    val lang: String,
    val year: Int,
    val actors: List<String>,
    val directors: List<String>,
    val categories: List<String>,
    val countries: List<String>,
    val episodes: List<EpisodeServer>
)

data class EpisodeServer(
    val serverName: String,
    val episodes: List<Episode>
)

data class Episode(
    val name: String,
    val slug: String,
    val linkM3u8: String
)

private fun getFullImageUrl(url: String?): String {
    if (url.isNullOrEmpty()) return ""
    return if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        "${OPhimApiService.IMAGE_BASE_URL}$url"
    }
}

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        name = name,
        slug = slug,
        originName = originName,
        posterUrl = getFullImageUrl(posterUrl),
        thumbUrl = getFullImageUrl(thumbUrl ?: posterUrl),
        type = type ?: "",
        year = year ?: 0
    )
}

fun MovieDetailDto.toMovieDetail(episodes: List<EpisodeServerDto>?): MovieDetail {
    return MovieDetail(
        id = id,
        name = name,
        slug = slug,
        originName = originName,
        content = content ?: "",
        type = type ?: "",
        status = status ?: "",
        thumbUrl = getFullImageUrl(thumbUrl),
        posterUrl = getFullImageUrl(posterUrl ?: thumbUrl),
        trailerUrl = trailerUrl ?: "",
        time = time ?: "",
        episodeCurrent = episodeCurrent ?: "",
        episodeTotal = episodeTotal ?: "",
        quality = quality ?: "",
        lang = lang ?: "",
        year = year ?: 0,
        actors = actor ?: emptyList(),
        directors = director ?: emptyList(),
        categories = category?.map { it.name } ?: emptyList(),
        countries = country?.map { it.name } ?: emptyList(),
        episodes = episodes?.map { it.toEpisodeServer() } ?: emptyList()
    )
}

fun EpisodeServerDto.toEpisodeServer(): EpisodeServer {
    return EpisodeServer(
        serverName = serverName,
        episodes = serverData?.map { it.toEpisode() } ?: emptyList()
    )
}

fun EpisodeDto.toEpisode(): Episode {
    return Episode(
        name = name,
        slug = slug,
        linkM3u8 = linkM3u8 ?: linkEmbed ?: ""
    )
}
