package com.xdja.imp.di.module;

import com.xdja.imp.data.callback.IMSDKCallBack;
import com.xdja.imp.data.di.annotation.UserScope;
import com.xdja.imp.data.repository.SecurityImp;
import com.xdja.imp.data.repository.UserOperateImp;
import com.xdja.imp.data.repository.im.IMProxyCallBackImp;
import com.xdja.imp.data.repository.im.IMProxyImp;
import com.xdja.imp.domain.repository.IMProxyCallBack;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp.domain.repository.SecurityRepository;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:功能性模块注入提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/6</p>
 * <p>Time:11:28</p>
 */
@Module
public class RepositoryModule {
    @UserScope
    @Provides
    UserOperateRepository provideUserSettingRepository(UserOperateImp userSettingImp) {
        return userSettingImp;
    }

    @UserScope
    @Provides
    IMProxyRepository provideIMProxy(IMProxyImp imProxyImp) {
        return imProxyImp;
    }

    @UserScope
    @Provides
    SecurityRepository provideSecurityRepository(SecurityImp securityImp) {
        return securityImp;
    }

    @UserScope
    @Provides
    IMProxyCallBack provideIMProxyCallBack(IMProxyCallBackImp callBackImp) {
        return callBackImp;
    }

    @UserScope
    @Provides
    CallbackFunction provideCallbackFunction(IMSDKCallBack callBack) {
        return callBack;
    }

    @UserScope
    @Provides
    IMFileInfoCallback provideIMFileInfoCallback(IMSDKCallBack callBack) {
        return callBack;
    }

    @UserScope
    @Provides
    IMSessionCallback provideIMSessionCallback(IMSDKCallBack callBack) {
        return callBack;
    }

    @UserScope
    @Provides
    IMMessageCallback provideIMMessageCallback(IMSDKCallBack callBack) {
        return callBack;
    }

    @UserScope
    @Provides
    IMSecurityCallback provideIMSecurityCallback(IMSDKCallBack callback) {
        return callback;
    }

}
