package com.xdja.imp;

import android.content.Context;

import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.SettingServer;
import com.xdja.contactopproxy.ContactCache;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.di.HasComponent;
import com.xdja.imp.di.component.ApplicationComponent;
import com.xdja.imp.di.component.DaggerUserComponent;
import com.xdja.imp.di.component.UserComponent;
import com.xdja.imp.util.NotificationUtil;
import com.xdja.proxy.imp.MxModuleProxyImp;

import rx.Subscriber;

/**
 * 项目名称：Blade
 * 类描述：退出时IM所要做的操作
 * 创建人：xdjaxa
 * 创建时间：2016/7/19 17:06
 * 修改人：xdjaxa
 * 修改时间：2016/7/19 17:06
 * 修改备注：
 */
public class IMAccountLifeCycle implements AccountLifeCycle, HasComponent<UserComponent> {
    public static IMAccountLifeCycle imAccountLifeCycle;

    private UserComponent userComponent;
    @Override
    public void login() {
        LogUtil.getUtils().e("IMAccountLifeCycle login!");
        imAccountLifeCycle = this;
        ApplicationComponent applicationComponent =
                ImApplication.androidApplication.getComponent();
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(applicationComponent)
                .build();
        //初始化IM
        //todo gbc
        MxModuleProxyImp.getInstance().startMXService(ActomaController.getApp().getApplicationContext(), "", "", "");
        ContactCache.getInstance().clearCache();//add by wangchao
        SettingServer.clearSafeLockSate();
    }

    @Override
    public void accountChange() {
        ContactCache.getInstance().clearCache();//add by lixiaolong on 20160918.
    }

    @Override
    public void logout() {
        LogUtil.getUtils().e("IMAccountLifeCycle logout!");
        final Context appContext = ActomaController.getApp().getApplicationContext();
        MxModuleProxyImp.getInstance().stopService(appContext);
        if (null != userComponent) {
            userComponent.logoutUseCase().execute(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    NotificationUtil.getInstance(appContext).clearPNNotification();
                    userComponent = null;
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.getUtils().e("logout release disk data store error!!");
                }

                @Override
                public void onNext(Integer integer) {

                }
            });
            //add by zya,20161027
            userComponent.userCache().clearCacheText();
            //end
        }
        ContactCache.getInstance().clearCache();//add by wangchao
        //fix by licong
        SettingServer.clearSafeLockSate();
    }

    @Override
    public UserComponent getComponent() {
        return this.userComponent;
    }
}
