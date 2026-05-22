# Hilt Proguard Rules
-keep public class * extends android.app.Application {
    *;
}

# Keep Hilt generated classes
-keep class com.rocky.filmtv.Hilt_* { *; }
-keep class * extends com.rocky.filmtv.Hilt_* { *; }
-keep interface dagger.hilt.internal.GeneratedEntryPoint { *; }
-keep interface dagger.hilt.internal.ComponentEntryPoint { *; }
-keep interface dagger.hilt.android.internal.builders.* { *; }

# Keep Dagger/Hilt classes that are referenced
-keep class dagger.hilt.** { *; }
-keep class dagger.internal.DoubleCheck { *; }

# Room Proguard Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.limits.Limit

# Retrofit & OkHttp Proguard Rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Keep Retrofit serializable classes
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Retrofit API interfaces and their methods completely
-keep interface com.rocky.filmtv.data.remote.api.** { *; }

# Keep all remote DTO classes (both Gson serializable and direct model classes)
-keep class com.rocky.filmtv.data.remote.dto.** { *; }
-keep class com.rocky.filmtv.data.remote.api.GitHubReleaseDto { *; }
-keep class com.rocky.filmtv.data.remote.api.GitHubAssetDto { *; }

# Keep generic signature of Call, Response and Coroutines Continuation for suspend functions in Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep OkHttp components
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Keep Timber logging
-keep class timber.log.Timber { *; }
-keep class timber.log.Timber$* { *; }
