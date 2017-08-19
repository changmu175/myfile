package com.xdja.presenter_mainframe.di.modules;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.util.Util;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.net.ServiceGenerator;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.di.modules.NetworkModule;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.BuildConfig;
import com.xdja.presenter_mainframe.global.GlobalLife;
import com.xdja.presenter_mainframe.global.GlobalLifeCycle;
import com.xdja.presenter_mainframe.global.PushController;
import com.xdja.presenter_mainframe.global.PushControllerImp;
import com.xdja.presenter_mainframe.global.obs.BindDeviceObservable;
import com.xdja.presenter_mainframe.global.obs.Observable;
import com.xdja.safeauth.okhttp.SafeAuthInterceptor;

import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */

@Module
public class AppModule {

    public static final String GLOBAL_APP = "global_app";
    public static final String GLOBAL_USER = "global_user";
    public static final String OBSERBABLE_BINDDEVICE = "bindDevice";
    public static final String INTERCEPTOR_SAFEAUTH = "interceptor_safeauth";

    private ActomaApplication application;

    public AppModule(ActomaApplication application) {
        this.application = application;
    }

    @Provides
    @AppScope
    @Named(GLOBAL_APP)
    GlobalLifeCycle provideGlobalLifeCycle(GlobalLife globalLife) {
        return globalLife;
    }

    @Provides
    @AppScope
    PushController providePushController(PushControllerImp pushControllerImp) {
        return pushControllerImp;
    }

    @AppScope
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF)
    OkHttpClient.Builder provideOkHttpsDefClientBuilder(@ConnSecuritySpe(DiConfig.CONN_HTTPS)
                                                        OkHttpClient.Builder builder,
                                                        @Named(INTERCEPTOR_SAFEAUTH) Interceptor interceptor) {
        //[S]modify by lixiaolong on 20160901. fix bug 3484. review by myself.
        builder.addInterceptor(interceptor);
        // TODO: 2016/5/31 For Debug
        if (BuildConfig.LOGABLE) {
            builder.addInterceptor(NetworkModule.getHttpLoggingInterceptor());
        }
        //[E]modify by lixiaolong on 20160901. fix bug 3484. review by myself.
        return builder;
    }

    @AppScope
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF)
    ServiceGenerator provideAccountServiceGenerator(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF)
                                                    OkHttpClient.Builder builder,
                                                    @Named(DiConfig.CONFIG_PROPERTIES_NAME)
                                                    Map<String, String> configs,
                                                    PreferencesUtil util) {
        String accountUrl = util.gPrefStringValue("accountUrl");
        if (!TextUtils.isEmpty(accountUrl)) {
            return new ServiceGenerator(builder, Util.generateFullAccountUrl(accountUrl));
        }
        return new ServiceGenerator(builder, configs.get("baseUrl"));
    }

    @AppScope
    @Provides
    @Named(INTERCEPTOR_SAFEAUTH)
    Interceptor provideSafeAuthInterceptor(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        return new SafeAuthInterceptor(context, "111111");
    }

    @AppScope
    @Provides
    @Named(OBSERBABLE_BINDDEVICE)
    Observable provideBindDeviceObservable(BindDeviceObservable bindDeviceObservable) {
        return bindDeviceObservable;
    }

    @AppScope
    @Provides
    @Named(DiConfig.CONFIG_PROPERTIES_NAME)
    Map<String, String> provideConfigMap(@Named(DiConfig.CONFIG_PROPERTIES_NAME) ConfigCache configCache) {
        return configCache.get();
    }

    @AppScope
    @Provides
    @Named(DiConfig.PN_PROPERTIES_NAME)
    Map<String, String> providePushMap(@Named(DiConfig.PN_PROPERTIES_NAME) ConfigCache configCache) {
        return configCache.get();
    }
}
