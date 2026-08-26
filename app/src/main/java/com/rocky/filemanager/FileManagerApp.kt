package com.rocky.filemanager

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.privacyfilemanager.feature.search.worker.SearchIndexWorker
import com.privacyfilemanager.core.domain.worker.TrashCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FileManagerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupLocalCrashHandler()
        scheduleSearchIndexing()
        scheduleTrashCleanup()
    }

    private fun scheduleSearchIndexing() {
        val request = OneTimeWorkRequestBuilder<SearchIndexWorker>().build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "search_index",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    /** Runs daily to permanently delete trash files older than 30 days. */
    private fun scheduleTrashCleanup() {
        val request = androidx.work.PeriodicWorkRequestBuilder<TrashCleanupWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trash_cleanup",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun setupLocalCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val logDir = File(filesDir, "logs")
                logDir.mkdirs()
                val logFile = File(logDir, "crash_${System.currentTimeMillis()}.txt")
                logFile.writeText(buildString {
                    appendLine("=== CRASH REPORT ===")
                    appendLine("Time: ${Date()}")
                    appendLine("Thread: ${thread.name}")
                    appendLine("Device: ${android.os.Build.MODEL} (API ${android.os.Build.VERSION.SDK_INT})")
                    appendLine("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    appendLine()
                    appendLine("=== STACK TRACE ===")
                    val sw = StringWriter()
                    throwable.printStackTrace(PrintWriter(sw))
                    appendLine(sw.toString())
                })
            } catch (_: Exception) {
                // Cannot crash while handling crash
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
