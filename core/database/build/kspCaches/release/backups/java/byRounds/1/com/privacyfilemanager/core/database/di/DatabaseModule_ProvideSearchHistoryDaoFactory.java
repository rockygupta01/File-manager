package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.SearchHistoryDao;
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
public final class DatabaseModule_ProvideSearchHistoryDaoFactory implements Factory<SearchHistoryDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideSearchHistoryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SearchHistoryDao get() {
    return provideSearchHistoryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSearchHistoryDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideSearchHistoryDaoFactory(dbProvider);
  }

  public static SearchHistoryDao provideSearchHistoryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSearchHistoryDao(db));
  }
}
