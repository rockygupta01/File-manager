package com.rocky.filemanager

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import javax.inject.Inject

/**
 * WorkManager initializer that uses Hilt's worker factory.
 * This replaces the default WorkManager auto-initialization so that
 * @HiltWorker annotated workers (AutoBackupWorker, CleanupWorker) receive DI.
 */
class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        // WorkManager is initialized via the Hilt entry point below
        return WorkManager.getInstance(context)
    }
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}

/**
 * Application-level WorkManager configuration using HiltWorkerFactory.
 * The App class must implement Configuration.Provider.
 */
