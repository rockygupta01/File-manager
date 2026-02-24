package com.privacyfilemanager.core.data.di

import com.privacyfilemanager.core.data.repository.LocalFileRepository
import com.privacyfilemanager.core.data.repository.LocalStorageRepository
import com.privacyfilemanager.core.domain.repository.FileRepository
import com.privacyfilemanager.core.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindFileRepository(impl: LocalFileRepository): FileRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(impl: LocalStorageRepository): StorageRepository
}
