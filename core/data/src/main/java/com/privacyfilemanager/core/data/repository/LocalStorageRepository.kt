package com.privacyfilemanager.core.data.repository

import android.os.Environment
import android.os.StatFs
import com.privacyfilemanager.core.domain.model.StorageStats
import com.privacyfilemanager.core.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorageRepository @Inject constructor() : StorageRepository {

    override fun getStorageStats(): Flow<StorageStats> = flow {
        val path = Environment.getExternalStorageDirectory().path
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val freeBytes = availableBlocks * blockSize

        // For privacy file manager, avoid long running deep scans if not necessary,
        // or we could add a basic scan. For now, we return basic statfs data.
        val stats = StorageStats(
            totalBytes = totalBytes,
            freeBytes = freeBytes
        )
        emit(stats)
    }.flowOn(Dispatchers.IO)
}
