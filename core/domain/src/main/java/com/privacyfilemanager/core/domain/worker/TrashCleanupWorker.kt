package com.privacyfilemanager.core.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.privacyfilemanager.core.domain.repository.TrashRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * A background worker that automatically deletes files from the trash
 * that are older than the configured threshold (e.g., 30 days).
 */
@HiltWorker
class TrashCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val trashRepository: TrashRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        return try {
            // Default 30 days auto-purge
            trashRepository.autoCleanExpired(daysOld = 30)
            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            androidx.work.ListenableWorker.Result.failure()
        }
    }
}
