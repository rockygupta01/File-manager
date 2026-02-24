package com.privacyfilemanager.feature.automation.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
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
public final class AutoBackupWorker_Factory {
  public AutoBackupWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams);
  }

  public static AutoBackupWorker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AutoBackupWorker newInstance(Context context, WorkerParameters workerParams) {
    return new AutoBackupWorker(context, workerParams);
  }

  private static final class InstanceHolder {
    private static final AutoBackupWorker_Factory INSTANCE = new AutoBackupWorker_Factory();
  }
}
