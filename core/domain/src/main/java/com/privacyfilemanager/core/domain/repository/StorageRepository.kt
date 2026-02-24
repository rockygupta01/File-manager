package com.privacyfilemanager.core.domain.repository

import com.privacyfilemanager.core.domain.model.StorageStats
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getStorageStats(): Flow<StorageStats>
}
