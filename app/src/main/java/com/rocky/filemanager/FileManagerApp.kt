package com.rocky.filemanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

@HiltAndroidApp
class FileManagerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupLocalCrashHandler()
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
