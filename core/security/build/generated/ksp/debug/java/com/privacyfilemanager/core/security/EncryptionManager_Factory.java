package com.privacyfilemanager.core.security;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class EncryptionManager_Factory implements Factory<EncryptionManager> {
  private final Provider<Context> contextProvider;

  public EncryptionManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public EncryptionManager get() {
    return newInstance(contextProvider.get());
  }

  public static EncryptionManager_Factory create(Provider<Context> contextProvider) {
    return new EncryptionManager_Factory(contextProvider);
  }

  public static EncryptionManager newInstance(Context context) {
    return new EncryptionManager(context);
  }
}
