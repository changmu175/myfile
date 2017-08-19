package com.xdja.presenter_mainframe.di.modules;

import android.text.TextUtils;

import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm_mainframe.annotations.UserScope;
import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.data_mainframe.util.Util;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.usecase.userInfo.DiskLogoutUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryForceLogoutNoticeUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryOnlineNoticeUseCase;
import com.xdja.domain_mainframe.usecase.userInfo.QueryUnBindDeviceNoticeUseCase;
import com.xdja.frame.data.net.ServiceGenerator;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.di.modules.NetworkModule;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.presenter_mainframe.BuildConfig;
import com.xdja.presenter_mainframe.global.GlobalLifeCycle;
import com.xdja.presenter_mainframe.global.PostGlobalLife;
import com.xdja.presenter_mainframe.global.account.AccountLifeCycleController;
import com.xdja.presenter_mainframe.global.obs.DeviceOnLineObservable;
import com.xdja.presenter_mainframe.global.obs.ForceLogoutObservable;
import com.xdja.presenter_mainframe.global.obs.Observable;
import com.xdja.presenter_mainframe.global.obs.UnBindDeviceObservable;

import java.io.IOException;
import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>Summary:登录后的依赖提供者</p>
 * <p>Description:生命周期为：登录之后，退出之前</p>
 * <p>Package:com.xdja.presenter_mainframe.di.modules</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/4</p>
 * <p>Time:20:16</p>
 */
@Module
public class PostModule {

    public PostModule() {

    }

    public static final String OBSERBABLE_UNBINDDEVICE = "unBindeDevice";
    public static final String OBSERBABLE_FORCELOGOUT = "forceLogout";
    public static final String OBSERBABLE_ONLINENOTICE = "onLineNotice";
    public static final String ACCOUNT_LIFE_CYCLE_CONTROLLER = "accountLifeCycleController";

    @Provides
    @UserScope
    @Named(ACCOUNT_LIFE_CYCLE_CONTROLLER)
    AccountLifeCycle provideAccountLifeCycle(AccountLifeCycleController accountLifeCycleController){
        return accountLifeCycleController;
    }

    @Provides
    @UserScope
    @Named(AppModule.GLOBAL_USER)
    GlobalLifeCycle provideGlobalLifeCycle(PostGlobalLife globalLife) {
        return globalLife;
    }

    /**
     * 获取解绑设备消息用例
     *
     * @param useCase fill方法的参数依次为：帐号
     * @return
     */
    @Provides
    @UserScope
    @InteractorSpe(DomainConfig.QUERY_UN_BIND_DEVICE_NOTICE)
    Ext0Interactor<Map<String, String>> provideQueryUnBindDeviceNoticeUseCase(QueryUnBindDeviceNoticeUseCase useCase) {
        return useCase;
    }

    @UserScope
    @Provides
    @Named(OBSERBABLE_UNBINDDEVICE)
    Observable<Map<String, String>> provideUnBindDeviceObservable(UnBindDeviceObservable unBindDeviceObservable) {
        return unBindDeviceObservable;
    }

    /**
     * 查询强制下线通知用例
     *
     * @param useCase fill方法的参数依次为：帐号
     * @return
     */
    @Provides
    @UserScope
    @InteractorSpe(DomainConfig.QUERY_FORCE_LOGOUT_NOTICE)
    Ext0Interactor<Map<String, String>> provideQueryForceLogoutNoticeUseCase(QueryForceLogoutNoticeUseCase useCase) {
        return useCase;
    }

    @UserScope
    @Provides
    @Named(OBSERBABLE_FORCELOGOUT)
    Observable<Map<String, String>> provideForceLogoutObservable(ForceLogoutObservable forceLogoutObservable) {
        return forceLogoutObservable;
    }

    /**
     * 获取上线通知消息用例
     *
     * @param useCase fill方法的参数依次为：无
     * @return
     */
    @Provides
    @UserScope
    @InteractorSpe(DomainConfig.QUERY_ONLINE_NOTICE)
    Ext0Interactor<Map<String, String>> provideQueryOnlineNoticeUseCase(QueryOnlineNoticeUseCase useCase) {
        return useCase;
    }

    @UserScope
    @Provides
    @Named(OBSERBABLE_ONLINENOTICE)
    Observable<Map<String, String>> provideOnLineNoticeObservable(DeviceOnLineObservable deviceOnLineObservable) {
        return deviceOnLineObservable;
    }


    @UserScope
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
    OkHttpClient.Builder provideOkHttpsTicketClientBuilder(final UserCache userCache,
                                                           @ConnSecuritySpe(DiConfig.CONN_HTTPS)
                                                           OkHttpClient.Builder builder) {
        builder.addInterceptor(
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();
                        builder.header("ticket", userCache.getTicket());
                        return chain.proceed(builder.build());
                    }
                }
        );
        //[S]modify by lixiaolong on 20160901. fix bug 3484. review by myself.
        // TODO: 2016/5/31 For Debug
        if (BuildConfig.LOGABLE) {
            builder.addInterceptor(NetworkModule.getHttpLoggingInterceptor());
        }
        //[E]modify by lixiaolong on 20160901. fix bug 3484. review by myself.
        return builder;
    }

    @UserScope
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
    ServiceGenerator provideHttpsTicketServiceGenerator(@ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
                                                        OkHttpClient.Builder builder,
                                                        PreferencesUtil util,
                                                        @Named(DiConfig.CONFIG_PROPERTIES_NAME)
                                                        Map<String, String> configs) {
        String accountUrl = util.gPrefStringValue("accountUrl");
        if (!TextUtils.isEmpty(accountUrl)) {
            return new ServiceGenerator(builder, Util.generateFullAccountUrl(accountUrl));
        }
        return new ServiceGenerator(builder, configs.get("baseUrl"));
    }

    /**
     * 查询在本地存储的上次登录账号
     */
    @UserScope
    @Provides
    @InteractorSpe(DomainConfig.DISK_LOGOUT)
    Ext0Interactor<Void> provideDiskLogoutUseCase(DiskLogoutUseCase useCase) {
        return useCase;
    }

}
