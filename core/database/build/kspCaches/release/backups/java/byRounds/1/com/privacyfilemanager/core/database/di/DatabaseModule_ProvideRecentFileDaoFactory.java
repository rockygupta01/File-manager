package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.RecentFileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideRecentFileDaoFactory implements Factory<RecentFileDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideRecentFileDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RecentFileDao get() {
    return provideRecentFileDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRecentFileDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideRecentFileDaoFactory(dbProvider);
  }

  public static RecentFileDao provideRecentFileDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRecentFileDao(db));
  }
}
