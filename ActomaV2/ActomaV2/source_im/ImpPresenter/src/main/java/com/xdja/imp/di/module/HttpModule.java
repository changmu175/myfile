package com.xdja.imp.di.module;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.ConnSecurity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/6</p>
 * <p>Time:11:30</p>
 */
@Module
public class HttpModule {

    @Singleton
    @Provides
    @ConnSecurity(DiConfig.CONN_HTTP)
    OkHttpClient provideOkHttpClient(){
        return new OkHttpClient();
    }
}
