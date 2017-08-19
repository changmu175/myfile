package com.xdja.frame.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.net.OkHttpsBuilder;
import com.xdja.frame.data.net.ServiceGenerator;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/6</p>
 * <p>Time:11:30</p>
 */
@Module
public class NetworkModule {

    private boolean isLog = true;

    public NetworkModule() {

    }

    public NetworkModule(boolean isLog) {
        this.isLog = isLog;
    }

//    @Singleton
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTP)
    OkHttpClient.Builder provideOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //modify by alh@xdja.com to fix bug: 522 2016-07-04 start (rummager : wangchao1)
        builder.connectTimeout(OkHttpsBuilder.CONN_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .readTimeout(OkHttpsBuilder.CONN_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .writeTimeout(OkHttpsBuilder.CONN_TIME_OUT_UNIT, TimeUnit.MILLISECONDS);
        //modify by alh@xdja.com to fix bug: 522 2016-07-04 end (rummager : wangchao1)
        if (this.isLog) {
            builder.addInterceptor(getHttpLoggingInterceptor());
        }
        return builder;
    }

//    @Singleton
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS)
    OkHttpClient.Builder provideOkHttpsClientBuilder(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                                                     Context context) {
        OkHttpClient.Builder build = new OkHttpsBuilder(context).build();
        if (this.isLog) {
            build.addInterceptor(getHttpLoggingInterceptor());
        }
        return build;

    }

    @NonNull
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Singleton
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTP)
    ServiceGenerator provideHttpServiceGenerator(@ConnSecuritySpe(DiConfig.CONN_HTTP)
                                                 OkHttpClient.Builder builder,
                                                 @Named(DiConfig.CONFIG_PROPERTIES_NAME)
                                                 ConfigCache configCache) {
        return new ServiceGenerator(builder,configCache.get().get("baseUrl"));
    }

    @Singleton
    @Provides
    @ConnSecuritySpe(DiConfig.CONN_HTTPS)
    ServiceGenerator provideHttpsServiceGenerator(@ConnSecuritySpe(DiConfig.CONN_HTTPS)
                                                  OkHttpClient.Builder builder,
                                                  @Named(DiConfig.CONFIG_PROPERTIES_NAME)
                                                  ConfigCache configCache) {
        return new ServiceGenerator(builder,configCache.get().get("baseUrl"));
    }
}
