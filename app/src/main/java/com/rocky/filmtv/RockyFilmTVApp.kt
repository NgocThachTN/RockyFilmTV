package com.rocky.filmtv

import android.app.Application
import android.graphics.Bitmap
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RockyFilmTVApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Use RGB_565 config globally which saves 50% memory with almost no visible loss
            .bitmapConfig(Bitmap.Config.RGB_565)
            // Configure memory cache: max 15% of available memory to prevent OOM on 2GB RAM devices
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            // Configure custom disk cache to load from disk faster
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100MB disk cache limit
                    .build()
            }
            .crossfade(true)
            .allowHardware(true) // Hardware bitmaps are highly optimized for GPU memory
            .build()
    }
}
