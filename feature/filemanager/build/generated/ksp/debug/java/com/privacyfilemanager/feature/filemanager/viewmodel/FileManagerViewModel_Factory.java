package com.privacyfilemanager.feature.filemanager.viewmodel;

import com.privacyfilemanager.core.database.dao.BookmarkDao;
import com.privacyfilemanager.core.database.dao.RecentFileDao;
import com.privacyfilemanager.core.domain.repository.FileRepository;
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
public final class FileManagerViewModel_Factory implements Factory<FileManagerViewModel> {
  private final Provider<FileRepository> fileRepositoryProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<RecentFileDao> recentFileDaoProvider;

  public FileManagerViewModel_Factory(Provider<FileRepository> fileRepositoryProvider,
      Provider<BookmarkDao> bookmarkDaoProvider, Provider<RecentFileDao> recentFileDaoProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.recentFileDaoProvider = recentFileDaoProvider;
  }

  @Override
  public FileManagerViewModel get() {
    return newInstance(fileRepositoryProvider.get(), bookmarkDaoProvider.get(), recentFileDaoProvider.get());
  }

  public static FileManagerViewModel_Factory create(Provider<FileRepository> fileRepositoryProvider,
      Provider<BookmarkDao> bookmarkDaoProvider, Provider<RecentFileDao> recentFileDaoProvider) {
    return new FileManagerViewModel_Factory(fileRepositoryProvider, bookmarkDaoProvider, recentFileDaoProvider);
  }

  public static FileManagerViewModel newInstance(FileRepository fileRepository,
      BookmarkDao bookmarkDao, RecentFileDao recentFileDao) {
    return new FileManagerViewModel(fileRepository, bookmarkDao, recentFileDao);
  }
}
