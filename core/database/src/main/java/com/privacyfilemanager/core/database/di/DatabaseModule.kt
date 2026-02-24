package com.privacyfilemanager.core.database.di

import android.content.Context
import androidx.room.Room
import com.privacyfilemanager.core.database.AppDatabase
import com.privacyfilemanager.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "privacy_file_manager.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideRecentFileDao(db: AppDatabase): RecentFileDao = db.recentFileDao()
    @Provides fun provideBookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideFileTagDao(db: AppDatabase): FileTagDao = db.fileTagDao()
    @Provides fun provideVaultFileDao(db: AppDatabase): VaultFileDao = db.vaultFileDao()
    @Provides fun provideAutomationRuleDao(db: AppDatabase): AutomationRuleDao = db.automationRuleDao()
    @Provides fun provideSearchIndexDao(db: AppDatabase): SearchIndexDao = db.searchIndexDao()
    @Provides fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()
    @Provides fun provideAppLockConfigDao(db: AppDatabase): AppLockConfigDao = db.appLockConfigDao()
    @Provides fun provideCrashLogDao(db: AppDatabase): CrashLogDao = db.crashLogDao()
}
