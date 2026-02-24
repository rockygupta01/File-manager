package com.privacyfilemanager.feature.devtools.viewmodel;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DevToolsViewModel_Factory implements Factory<DevToolsViewModel> {
  private final Provider<Context> contextProvider;

  public DevToolsViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DevToolsViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static DevToolsViewModel_Factory create(Provider<Context> contextProvider) {
    return new DevToolsViewModel_Factory(contextProvider);
  }

  public static DevToolsViewModel newInstance(Context context) {
    return new DevToolsViewModel(context);
  }
}
