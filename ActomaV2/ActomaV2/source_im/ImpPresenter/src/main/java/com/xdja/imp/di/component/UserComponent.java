package com.xdja.imp.di.component;

import com.xdja.imp.data.di.annotation.UserScope;
import com.xdja.imp.data.repository.datasource.CloudDataStore;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.di.module.AssistModule;
import com.xdja.imp.di.module.DataStoreModule;
import com.xdja.imp.di.module.RepositoryModule;
import com.xdja.imp.domain.interactor.mx.LogOutUseCase;
import com.xdja.imp.domain.repository.IMProxyCallBack;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp.domain.repository.SecurityRepository;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp.util.MsgDisplay;
import com.xdja.imsdk.ImClient;
import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;

import dagger.Component;

/**
 * Created by gbc on 2016/9/2.
 */
@UserScope
@Component(dependencies = {
        ApplicationComponent.class
        },
        modules = {
                AssistModule.class,
                DataStoreModule.class,
                RepositoryModule.class,
        })
public interface UserComponent extends ApplicationComponent{
    LogOutUseCase logoutUseCase();

    CloudDataStore cloudDataStore();

    DiskDataStore diskDataStore();

    ImClient imClient();

    UserOperateRepository userOperateRepository();

    IMProxyRepository iMProxyRepository();

    SecurityRepository securityRepository();

    IMProxyCallBack iMProxyCallBack();

    CallbackFunction callbackFunction();

    IMFileInfoCallback iMFileInfoCallback();

    IMSessionCallback iMSessionCallback();

    IMMessageCallback iMMessageCallback();

    IMSecurityCallback iMSecurityCallback();

    MsgDisplay msgDisplay();
}
