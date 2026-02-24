package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.FileTagDao;
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
public final class DatabaseModule_ProvideFileTagDaoFactory implements Factory<FileTagDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideFileTagDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FileTagDao get() {
    return provideFileTagDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFileTagDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideFileTagDaoFactory(dbProvider);
  }

  public static FileTagDao provideFileTagDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFileTagDao(db));
  }
}
