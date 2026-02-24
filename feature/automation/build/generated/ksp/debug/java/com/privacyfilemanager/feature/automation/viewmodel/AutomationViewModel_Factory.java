package com.privacyfilemanager.feature.automation.viewmodel;

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
public final class AutomationViewModel_Factory implements Factory<AutomationViewModel> {
  private final Provider<Context> contextProvider;

  public AutomationViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AutomationViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static AutomationViewModel_Factory create(Provider<Context> contextProvider) {
    return new AutomationViewModel_Factory(contextProvider);
  }

  public static AutomationViewModel newInstance(Context context) {
    return new AutomationViewModel(context);
  }
}
