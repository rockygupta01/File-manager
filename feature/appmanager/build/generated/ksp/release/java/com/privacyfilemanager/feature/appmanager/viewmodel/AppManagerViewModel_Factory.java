package com.privacyfilemanager.feature.appmanager.viewmodel;

import com.privacyfilemanager.feature.appmanager.domain.AppManagerRepository;
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
public final class AppManagerViewModel_Factory implements Factory<AppManagerViewModel> {
  private final Provider<AppManagerRepository> repositoryProvider;

  public AppManagerViewModel_Factory(Provider<AppManagerRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AppManagerViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static AppManagerViewModel_Factory create(
      Provider<AppManagerRepository> repositoryProvider) {
    return new AppManagerViewModel_Factory(repositoryProvider);
  }

  public static AppManagerViewModel newInstance(AppManagerRepository repository) {
    return new AppManagerViewModel(repository);
  }
}
