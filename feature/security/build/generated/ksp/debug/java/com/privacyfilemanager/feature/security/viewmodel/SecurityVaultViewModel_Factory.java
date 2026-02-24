package com.privacyfilemanager.feature.security.viewmodel;

import com.privacyfilemanager.core.security.AppLockManager;
import com.privacyfilemanager.core.security.EncryptionManager;
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
public final class SecurityVaultViewModel_Factory implements Factory<SecurityVaultViewModel> {
  private final Provider<AppLockManager> appLockManagerProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  public SecurityVaultViewModel_Factory(Provider<AppLockManager> appLockManagerProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    this.appLockManagerProvider = appLockManagerProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
  }

  @Override
  public SecurityVaultViewModel get() {
    return newInstance(appLockManagerProvider.get(), encryptionManagerProvider.get());
  }

  public static SecurityVaultViewModel_Factory create(
      Provider<AppLockManager> appLockManagerProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    return new SecurityVaultViewModel_Factory(appLockManagerProvider, encryptionManagerProvider);
  }

  public static SecurityVaultViewModel newInstance(AppLockManager appLockManager,
      EncryptionManager encryptionManager) {
    return new SecurityVaultViewModel(appLockManager, encryptionManager);
  }
}
