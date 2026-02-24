package com.privacyfilemanager.core.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LocalStorageRepository_Factory implements Factory<LocalStorageRepository> {
  @Override
  public LocalStorageRepository get() {
    return newInstance();
  }

  public static LocalStorageRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LocalStorageRepository newInstance() {
    return new LocalStorageRepository();
  }

  private static final class InstanceHolder {
    private static final LocalStorageRepository_Factory INSTANCE = new LocalStorageRepository_Factory();
  }
}
