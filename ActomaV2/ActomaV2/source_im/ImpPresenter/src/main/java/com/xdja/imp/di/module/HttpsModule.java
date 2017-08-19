package com.xdja.imp.di.module;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.ConnSecurity;
import com.xdja.imp.data.net.OkHttpsClientMe;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/6</p>
 * <p>Time:11:30</p>
 */
@Module
public class HttpsModule {
    @Singleton
    @Provides
    @ConnSecurity(DiConfig.CONN_HTTPS)
    OkHttpsClientMe provideOkHttpsClientMe(OkHttpsClientMe okHttpsClientMe){
        return okHttpsClientMe;
    }
}
