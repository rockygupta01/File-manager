package com.privacyfilemanager.feature.viewer.viewmodel;

import androidx.lifecycle.SavedStateHandle;
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
public final class ViewerViewModel_Factory implements Factory<ViewerViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ViewerViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ViewerViewModel get() {
    return newInstance(savedStateHandleProvider.get());
  }

  public static ViewerViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ViewerViewModel_Factory(savedStateHandleProvider);
  }

  public static ViewerViewModel newInstance(SavedStateHandle savedStateHandle) {
    return new ViewerViewModel(savedStateHandle);
  }
}
