package com.privacyfilemanager.feature.archive.domain;

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
public final class ArchiveManager_Factory implements Factory<ArchiveManager> {
  @Override
  public ArchiveManager get() {
    return newInstance();
  }

  public static ArchiveManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ArchiveManager newInstance() {
    return new ArchiveManager();
  }

  private static final class InstanceHolder {
    private static final ArchiveManager_Factory INSTANCE = new ArchiveManager_Factory();
  }
}
