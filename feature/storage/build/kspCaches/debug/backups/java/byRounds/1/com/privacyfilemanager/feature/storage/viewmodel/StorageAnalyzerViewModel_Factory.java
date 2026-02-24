package com.privacyfilemanager.feature.storage.viewmodel;

import com.privacyfilemanager.core.domain.repository.StorageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class StorageAnalyzerViewModel_Factory implements Factory<StorageAnalyzerViewModel> {
  private final Provider<StorageRepository> storageRepositoryProvider;

  public StorageAnalyzerViewModel_Factory(Provider<StorageRepository> storageRepositoryProvider) {
    this.storageRepositoryProvider = storageRepositoryProvider;
  }

  @Override
  public StorageAnalyzerViewModel get() {
    return newInstance(storageRepositoryProvider.get());
  }

  public static StorageAnalyzerViewModel_Factory create(
      Provider<StorageRepository> storageRepositoryProvider) {
    return new StorageAnalyzerViewModel_Factory(storageRepositoryProvider);
  }

  public static StorageAnalyzerViewModel newInstance(StorageRepository storageRepository) {
    return new StorageAnalyzerViewModel(storageRepository);
  }
}
