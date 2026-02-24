package com.rocky.filemanager;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FileManagerApp_MembersInjector implements MembersInjector<FileManagerApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public FileManagerApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<FileManagerApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new FileManagerApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(FileManagerApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.rocky.filemanager.FileManagerApp.workerFactory")
  public static void injectWorkerFactory(FileManagerApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
