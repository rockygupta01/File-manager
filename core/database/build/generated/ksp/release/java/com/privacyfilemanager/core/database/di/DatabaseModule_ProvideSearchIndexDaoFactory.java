package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.SearchIndexDao;
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
public final class DatabaseModule_ProvideSearchIndexDaoFactory implements Factory<SearchIndexDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideSearchIndexDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SearchIndexDao get() {
    return provideSearchIndexDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSearchIndexDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideSearchIndexDaoFactory(dbProvider);
  }

  public static SearchIndexDao provideSearchIndexDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSearchIndexDao(db));
  }
}
