package com.privacyfilemanager.feature.automation.worker

import android.content.Context
import android.os.Environment
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val sourcePathsRaw = inputData.getString(KEY_SOURCE_PATHS) ?: return@withContext Result.failure()
            val sourcePaths = sourcePathsRaw.split(",").filter { it.isNotBlank() }
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupRoot = File(Environment.getExternalStorageDirectory(), "AutoBackup/$timestamp")
            backupRoot.mkdirs()

            for (srcPath in sourcePaths) {
                val src = File(srcPath)
                if (src.exists()) {
                    if (src.isDirectory) {
                        src.copyRecursively(File(backupRoot, src.name), overwrite = true)
                    } else {
                        src.copyTo(File(backupRoot, src.name), overwrite = true)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_SOURCE_PATHS = "source_paths"
    }
}
