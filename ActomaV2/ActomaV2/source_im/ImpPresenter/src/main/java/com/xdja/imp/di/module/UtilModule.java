package com.xdja.imp.di.module;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:16:10</p>
 */
@Module
public class UtilModule {
    @Singleton
    @Provides
    Gson provideGson(){
        return new Gson();
    }
}
