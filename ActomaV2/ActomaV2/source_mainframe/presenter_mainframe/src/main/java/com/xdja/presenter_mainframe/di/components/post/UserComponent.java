package com.xdja.presenter_mainframe.di.components.post;

import com.xdja.comm_mainframe.annotations.UserScope;
import com.xdja.data_mainframe.di.PostRepositoryModule;
import com.xdja.data_mainframe.di.PostStoreModule;
import com.xdja.data_mainframe.di.UserCacheModule;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.presenter_mainframe.di.modules.AppModule;
import com.xdja.presenter_mainframe.di.modules.PostModule;
import com.xdja.presenter_mainframe.global.GlobalLifeCycle;
import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;

import javax.inject.Named;

import dagger.Subcomponent;

/**
 * <p>Summary:生命周期从登录成功开始</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.di.components</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/4</p>
 * <p>Time:15:10</p>
 */

@UserScope
@Subcomponent(modules = {
        PostModule.class,
        UserCacheModule.class,
        PostRepositoryModule.class,
        PostStoreModule.class
})
public interface UserComponent {

    PostUseCaseComponent plus(PostUseCaseModule postUseCaseModule, PreUseCaseModule preUseCaseModule);

    @Named(AppModule.GLOBAL_USER)
    GlobalLifeCycle globalLifeCycle();

    @InteractorSpe(value = DomainConfig.DISK_LOGOUT)
    Ext0Interactor<Void> diskLogout();

    @Named(PostModule.ACCOUNT_LIFE_CYCLE_CONTROLLER)
    AccountLifeCycle accountLifeCycle();
}
