package com.xdja.presenter_mainframe.di.components.pre;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PostRepositoryModule;
import com.xdja.data_mainframe.di.PostStoreModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.di.PreStoreModule;
import com.xdja.data_mainframe.di.UserCacheModule;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.di.components.ApplicationComponent;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.di.modules.AppModule;
import com.xdja.presenter_mainframe.di.modules.PostModule;
import com.xdja.presenter_mainframe.global.GlobalLifeCycle;

import java.util.Map;

import javax.inject.Named;

import dagger.Component;

/**
 * Created by xdja-fanjiandong on 2016/3/17.
 */
@AppScope
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {
                AppModule.class,
                PreStoreModule.class,
                PreRepositoryModule.class,
                CacheModule.class
        }
)
public interface AppComponent extends ApplicationComponent {

    UserComponent plus(PostModule postModule,
                       UserCacheModule userCacheModule,
                       PostRepositoryModule postRepositoryModule,
                       PostStoreModule postStoreModule);

    PreUseCaseComponent plus(PreUseCaseModule preUseCaseModule);

    @Named(AppModule.GLOBAL_APP)
    GlobalLifeCycle globalLifeCycle();

    @Named(DiConfig.PN_PROPERTIES_NAME)
    Map<String, String> pnMap();

    PreferencesUtil sharedPreferencesUtil();
}
