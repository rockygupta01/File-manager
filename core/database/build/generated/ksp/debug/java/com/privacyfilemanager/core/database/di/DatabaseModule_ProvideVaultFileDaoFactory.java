package com.privacyfilemanager.core.database.di;

import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.VaultFileDao;
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
public final class DatabaseModule_ProvideVaultFileDaoFactory implements Factory<VaultFileDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideVaultFileDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VaultFileDao get() {
    return provideVaultFileDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideVaultFileDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideVaultFileDaoFactory(dbProvider);
  }

  public static VaultFileDao provideVaultFileDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVaultFileDao(db));
  }
}
