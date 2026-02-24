package com.rocky.filemanager;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.privacyfilemanager.core.data.repository.LocalFileRepository;
import com.privacyfilemanager.core.data.repository.LocalStorageRepository;
import com.privacyfilemanager.core.database.AppDatabase;
import com.privacyfilemanager.core.database.dao.BookmarkDao;
import com.privacyfilemanager.core.database.dao.RecentFileDao;
import com.privacyfilemanager.core.database.di.DatabaseModule_ProvideBookmarkDaoFactory;
import com.privacyfilemanager.core.database.di.DatabaseModule_ProvideDatabaseFactory;
import com.privacyfilemanager.core.database.di.DatabaseModule_ProvideRecentFileDaoFactory;
import com.privacyfilemanager.core.security.AppLockManager;
import com.privacyfilemanager.core.security.EncryptionManager;
import com.privacyfilemanager.feature.appmanager.domain.AppManagerRepository;
import com.privacyfilemanager.feature.appmanager.viewmodel.AppManagerViewModel;
import com.privacyfilemanager.feature.appmanager.viewmodel.AppManagerViewModel_HiltModules;
import com.privacyfilemanager.feature.appmanager.viewmodel.AppManagerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.appmanager.viewmodel.AppManagerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.archive.domain.ArchiveManager;
import com.privacyfilemanager.feature.archive.viewmodel.ArchiveViewModel;
import com.privacyfilemanager.feature.archive.viewmodel.ArchiveViewModel_HiltModules;
import com.privacyfilemanager.feature.archive.viewmodel.ArchiveViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.archive.viewmodel.ArchiveViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.automation.viewmodel.AutomationViewModel;
import com.privacyfilemanager.feature.automation.viewmodel.AutomationViewModel_HiltModules;
import com.privacyfilemanager.feature.automation.viewmodel.AutomationViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.automation.viewmodel.AutomationViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel;
import com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel_HiltModules;
import com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel;
import com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel_HiltModules;
import com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.lan.viewmodel.LanViewModel;
import com.privacyfilemanager.feature.lan.viewmodel.LanViewModel_HiltModules;
import com.privacyfilemanager.feature.lan.viewmodel.LanViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.lan.viewmodel.LanViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.root.viewmodel.RootViewModel;
import com.privacyfilemanager.feature.root.viewmodel.RootViewModel_HiltModules;
import com.privacyfilemanager.feature.root.viewmodel.RootViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.root.viewmodel.RootViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.search.viewmodel.SearchViewModel;
import com.privacyfilemanager.feature.search.viewmodel.SearchViewModel_HiltModules;
import com.privacyfilemanager.feature.search.viewmodel.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.search.viewmodel.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel;
import com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel_HiltModules;
import com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel;
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel_HiltModules;
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel;
import com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel_HiltModules;
import com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerFileManagerApp_HiltComponents_SingletonC {
  private DaggerFileManagerApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public FileManagerApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements FileManagerApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements FileManagerApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements FileManagerApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements FileManagerApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements FileManagerApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements FileManagerApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements FileManagerApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public FileManagerApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends FileManagerApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends FileManagerApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends FileManagerApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends FileManagerApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(11).put(AppManagerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AppManagerViewModel_HiltModules.KeyModule.provide()).put(ArchiveViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ArchiveViewModel_HiltModules.KeyModule.provide()).put(AutomationViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AutomationViewModel_HiltModules.KeyModule.provide()).put(DevToolsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DevToolsViewModel_HiltModules.KeyModule.provide()).put(FileManagerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FileManagerViewModel_HiltModules.KeyModule.provide()).put(LanViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LanViewModel_HiltModules.KeyModule.provide()).put(RootViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RootViewModel_HiltModules.KeyModule.provide()).put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide()).put(SecurityVaultViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SecurityVaultViewModel_HiltModules.KeyModule.provide()).put(StorageAnalyzerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, StorageAnalyzerViewModel_HiltModules.KeyModule.provide()).put(ViewerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ViewerViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends FileManagerApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AppManagerViewModel> appManagerViewModelProvider;

    private Provider<ArchiveViewModel> archiveViewModelProvider;

    private Provider<AutomationViewModel> automationViewModelProvider;

    private Provider<DevToolsViewModel> devToolsViewModelProvider;

    private Provider<FileManagerViewModel> fileManagerViewModelProvider;

    private Provider<LanViewModel> lanViewModelProvider;

    private Provider<RootViewModel> rootViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SecurityVaultViewModel> securityVaultViewModelProvider;

    private Provider<StorageAnalyzerViewModel> storageAnalyzerViewModelProvider;

    private Provider<ViewerViewModel> viewerViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.appManagerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.archiveViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.automationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.devToolsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.fileManagerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.lanViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.rootViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.securityVaultViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.storageAnalyzerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.viewerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(11).put(AppManagerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) appManagerViewModelProvider)).put(ArchiveViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) archiveViewModelProvider)).put(AutomationViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) automationViewModelProvider)).put(DevToolsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) devToolsViewModelProvider)).put(FileManagerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) fileManagerViewModelProvider)).put(LanViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) lanViewModelProvider)).put(RootViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) rootViewModelProvider)).put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) searchViewModelProvider)).put(SecurityVaultViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) securityVaultViewModelProvider)).put(StorageAnalyzerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) storageAnalyzerViewModelProvider)).put(ViewerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) viewerViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.privacyfilemanager.feature.appmanager.viewmodel.AppManagerViewModel 
          return (T) new AppManagerViewModel(singletonCImpl.appManagerRepositoryProvider.get());

          case 1: // com.privacyfilemanager.feature.archive.viewmodel.ArchiveViewModel 
          return (T) new ArchiveViewModel(singletonCImpl.archiveManagerProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.privacyfilemanager.feature.automation.viewmodel.AutomationViewModel 
          return (T) new AutomationViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.privacyfilemanager.feature.devtools.viewmodel.DevToolsViewModel 
          return (T) new DevToolsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.privacyfilemanager.feature.filemanager.viewmodel.FileManagerViewModel 
          return (T) new FileManagerViewModel(singletonCImpl.localFileRepositoryProvider.get(), singletonCImpl.bookmarkDao(), singletonCImpl.recentFileDao());

          case 5: // com.privacyfilemanager.feature.lan.viewmodel.LanViewModel 
          return (T) new LanViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.privacyfilemanager.feature.root.viewmodel.RootViewModel 
          return (T) new RootViewModel();

          case 7: // com.privacyfilemanager.feature.search.viewmodel.SearchViewModel 
          return (T) new SearchViewModel(singletonCImpl.localFileRepositoryProvider.get());

          case 8: // com.privacyfilemanager.feature.security.viewmodel.SecurityVaultViewModel 
          return (T) new SecurityVaultViewModel(singletonCImpl.appLockManagerProvider.get(), singletonCImpl.encryptionManagerProvider.get());

          case 9: // com.privacyfilemanager.feature.storage.viewmodel.StorageAnalyzerViewModel 
          return (T) new StorageAnalyzerViewModel(singletonCImpl.localStorageRepositoryProvider.get());

          case 10: // com.privacyfilemanager.feature.viewer.viewmodel.ViewerViewModel 
          return (T) new ViewerViewModel(viewModelCImpl.savedStateHandle);

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends FileManagerApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends FileManagerApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends FileManagerApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppManagerRepository> appManagerRepositoryProvider;

    private Provider<ArchiveManager> archiveManagerProvider;

    private Provider<LocalFileRepository> localFileRepositoryProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<AppLockManager> appLockManagerProvider;

    private Provider<EncryptionManager> encryptionManagerProvider;

    private Provider<LocalStorageRepository> localStorageRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private BookmarkDao bookmarkDao() {
      return DatabaseModule_ProvideBookmarkDaoFactory.provideBookmarkDao(provideDatabaseProvider.get());
    }

    private RecentFileDao recentFileDao() {
      return DatabaseModule_ProvideRecentFileDaoFactory.provideRecentFileDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.appManagerRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AppManagerRepository>(singletonCImpl, 0));
      this.archiveManagerProvider = DoubleCheck.provider(new SwitchingProvider<ArchiveManager>(singletonCImpl, 1));
      this.localFileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LocalFileRepository>(singletonCImpl, 2));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 3));
      this.appLockManagerProvider = DoubleCheck.provider(new SwitchingProvider<AppLockManager>(singletonCImpl, 4));
      this.encryptionManagerProvider = DoubleCheck.provider(new SwitchingProvider<EncryptionManager>(singletonCImpl, 5));
      this.localStorageRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LocalStorageRepository>(singletonCImpl, 6));
    }

    @Override
    public void injectFileManagerApp(FileManagerApp fileManagerApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.privacyfilemanager.feature.appmanager.domain.AppManagerRepository 
          return (T) new AppManagerRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.privacyfilemanager.feature.archive.domain.ArchiveManager 
          return (T) new ArchiveManager();

          case 2: // com.privacyfilemanager.core.data.repository.LocalFileRepository 
          return (T) new LocalFileRepository();

          case 3: // com.privacyfilemanager.core.database.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.privacyfilemanager.core.security.AppLockManager 
          return (T) new AppLockManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.privacyfilemanager.core.security.EncryptionManager 
          return (T) new EncryptionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.privacyfilemanager.core.data.repository.LocalStorageRepository 
          return (T) new LocalStorageRepository();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
