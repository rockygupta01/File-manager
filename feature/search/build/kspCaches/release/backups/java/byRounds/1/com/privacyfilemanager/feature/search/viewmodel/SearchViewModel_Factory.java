package com.privacyfilemanager.feature.search.viewmodel;

import com.privacyfilemanager.core.domain.repository.FileRepository;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<FileRepository> fileRepositoryProvider;

  public SearchViewModel_Factory(Provider<FileRepository> fileRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(fileRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<FileRepository> fileRepositoryProvider) {
    return new SearchViewModel_Factory(fileRepositoryProvider);
  }

  public static SearchViewModel newInstance(FileRepository fileRepository) {
    return new SearchViewModel(fileRepository);
  }
}
