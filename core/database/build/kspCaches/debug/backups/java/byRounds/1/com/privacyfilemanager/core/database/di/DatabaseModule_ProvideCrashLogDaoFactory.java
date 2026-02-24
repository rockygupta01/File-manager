package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.CrashLogDao;
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
public final class DatabaseModule_ProvideCrashLogDaoFactory implements Factory<CrashLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideCrashLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CrashLogDao get() {
    return provideCrashLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCrashLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideCrashLogDaoFactory(dbProvider);
  }

  public static CrashLogDao provideCrashLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCrashLogDao(db));
  }
}
