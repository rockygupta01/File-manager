package com.privacyfilemanager.feature.archive.viewmodel;

import androidx.lifecycle.SavedStateHandle;
import com.privacyfilemanager.feature.archive.domain.ArchiveManager;
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
public final class ArchiveViewModel_Factory implements Factory<ArchiveViewModel> {
  private final Provider<ArchiveManager> archiveManagerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ArchiveViewModel_Factory(Provider<ArchiveManager> archiveManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.archiveManagerProvider = archiveManagerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ArchiveViewModel get() {
    return newInstance(archiveManagerProvider.get(), savedStateHandleProvider.get());
  }

  public static ArchiveViewModel_Factory create(Provider<ArchiveManager> archiveManagerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ArchiveViewModel_Factory(archiveManagerProvider, savedStateHandleProvider);
  }

  public static ArchiveViewModel newInstance(ArchiveManager archiveManager,
      SavedStateHandle savedStateHandle) {
    return new ArchiveViewModel(archiveManager, savedStateHandle);
  }
}
