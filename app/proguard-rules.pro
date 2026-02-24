# ─── Kotlin / General ────────────────────────────────────────────────────────
-keepattributes *Annotation*, Signature, Exception
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ─── Hilt ────────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# ─── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Dao class * { *; }
-dontwarn androidx.room.paging.**

# ─── WorkManager / Hilt Workers ───────────────────────────────────────────────
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class androidx.work.** { *; }
-keep class * extends dagger.assisted.AssistedInject { *; }
-keepnames @androidx.hilt.work.HiltWorker class *

# ─── NanoHTTPD (LAN Transfer) ─────────────────────────────────────────────────
-keep class fi.iki.elonen.** { *; }
-dontwarn fi.iki.elonen.**

# ─── ML Kit (On-Device OCR) ───────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }
-dontwarn com.google.mlkit.**

# ─── ExifInterface ────────────────────────────────────────────────────────────
-keep class androidx.exifinterface.** { *; }

# ─── Coil Image Loading ───────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ─── Media3 / ExoPlayer ───────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ─── Kotlin Coroutines ────────────────────────────────────────────────────────
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}

# ─── zip4j ────────────────────────────────────────────────────────────────────
-keep class net.lingala.zip4j.** { *; }
-dontwarn net.lingala.zip4j.**

# ─── Security Crypto ──────────────────────────────────────────────────────────
-keep class androidx.security.crypto.** { *; }

