package com.rocky.filmtv.data.remote.api

import retrofit2.http.GET

interface GitHubUpdateService {
    @GET("repos/NgocThachTN/RockyFilmTV/releases/latest")
    suspend fun getLatestRelease(): GitHubReleaseDto

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}

data class GitHubReleaseDto(
    val tag_name: String,
    val name: String?,
    val body: String?,
    val assets: List<GitHubAssetDto>
)

data class GitHubAssetDto(
    val name: String,
    val size: Long,
    val browser_download_url: String
)
