package com.rocky.filmtv.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.rocky.filmtv.BuildConfig
import com.rocky.filmtv.data.remote.api.GitHubUpdateService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
    data class Success(val apkFile: File) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitHubUpdateService: GitHubUpdateService,
    private val okHttpClient: OkHttpClient
) {
    suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val latestRelease = gitHubUpdateService.getLatestRelease()
            val serverVersion = latestRelease.tag_name.replace(Regex("^[vV]"), "").trim()
            val currentVersion = BuildConfig.VERSION_NAME

            if (isNewerVersion(currentVersion, serverVersion)) {
                val apkAsset = latestRelease.assets.firstOrNull { it.name.endsWith(".apk") }
                if (apkAsset != null) {
                    UpdateInfo(
                        newVersion = serverVersion,
                        changelog = latestRelease.body ?: "Không có mô tả chi tiết.",
                        downloadUrl = apkAsset.browser_download_url,
                        apkSize = apkAsset.size
                    )
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun downloadApk(downloadUrl: String): Flow<DownloadState> = flow {
        emit(DownloadState.Downloading(0f, 0, 0))
        val request = Request.Builder().url(downloadUrl).build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) throw Exception("Lỗi tải xuống từ máy chủ: ${response.code}")

            val body = response.body ?: throw Exception("Nội dung tải trống")
            val totalBytes = body.contentLength()
            val apkFile = File(context.cacheDir, "update.apk")
            if (apkFile.exists()) apkFile.delete()

            body.byteStream().use { inputStream ->
                FileOutputStream(apkFile).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead = 0L

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progress = if (totalBytes > 0) totalRead.toFloat() / totalBytes.toFloat() else 0f
                        emit(DownloadState.Downloading(progress, totalRead, totalBytes))
                    }
                }
            }
            emit(DownloadState.Success(apkFile))
        } catch (e: Exception) {
            emit(DownloadState.Error(e.localizedMessage ?: "Lỗi khi tải bản cập nhật"))
        }
    }.flowOn(Dispatchers.IO)

    fun installApk(apkFile: File) {
        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        } else {
            Uri.fromFile(apkFile)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    private fun isNewerVersion(current: String, server: String): Boolean {
        return try {
            val currParts = current.split(".").map { it.toInt() }
            val servParts = server.split(".").map { it.toInt() }
            for (i in 0 until minOf(currParts.size, servParts.size)) {
                if (servParts[i] > currParts[i]) return true
                if (currParts[i] > servParts[i]) return false
            }
            servParts.size > currParts.size
        } catch (e: Exception) {
            server != current
        }
    }
}

data class UpdateInfo(
    val newVersion: String,
    val changelog: String,
    val downloadUrl: String,
    val apkSize: Long
)
