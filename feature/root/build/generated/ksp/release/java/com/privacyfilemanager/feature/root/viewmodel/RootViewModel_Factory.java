package com.privacyfilemanager.feature.root.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class RootViewModel_Factory implements Factory<RootViewModel> {
  @Override
  public RootViewModel get() {
    return newInstance();
  }

  public static RootViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RootViewModel newInstance() {
    return new RootViewModel();
  }

  private static final class InstanceHolder {
    private static final RootViewModel_Factory INSTANCE = new RootViewModel_Factory();
  }
}
