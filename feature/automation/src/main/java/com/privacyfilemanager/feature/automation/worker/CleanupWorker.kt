package com.privacyfilemanager.feature.automation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val rootPath = inputData.getString(KEY_ROOT_PATH) ?: return@withContext Result.failure()
            val maxAgeDays = inputData.getInt(KEY_MAX_AGE_DAYS, 30)
            val junkExtensions = setOf("tmp", "log", "bak", "chk", "dmp", "crdownload")
            val cutoffMs = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
            var deletedCount = 0

            File(rootPath).walkTopDown()
                .onEnter { !it.name.startsWith(".") }
                .filter { it.isFile }
                .filter { file ->
                    val isJunk = file.extension.lowercase() in junkExtensions
                    val isOld = file.lastModified() < cutoffMs
                    isJunk || (isOld && file.length() == 0L)
                }
                .forEach { file ->
                    if (file.delete()) deletedCount++
                }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_ROOT_PATH = "root_path"
        const val KEY_MAX_AGE_DAYS = "max_age_days"
    }
}
