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
public final class LocalFileRepository_Factory implements Factory<LocalFileRepository> {
  @Override
  public LocalFileRepository get() {
    return newInstance();
  }

  public static LocalFileRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LocalFileRepository newInstance() {
    return new LocalFileRepository();
  }

  private static final class InstanceHolder {
    private static final LocalFileRepository_Factory INSTANCE = new LocalFileRepository_Factory();
  }
}
