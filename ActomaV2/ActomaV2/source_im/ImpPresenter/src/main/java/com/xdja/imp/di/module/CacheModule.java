package com.xdja.imp.di.module;

import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.CardCacheImp;
import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.ConfigImp;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.cache.UserCacheImp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:16:07</p>
 */
@Module
public class CacheModule {
    @Singleton
    @Provides
    UserCache provideUserCache(UserCacheImp imp){
        return imp;
    }

    @Singleton
    @Provides
    CardCache provideCardCache(CardCacheImp cardCacheImp){
        return cardCacheImp;
    }

    @Singleton
    @Provides
    ConfigCache provideConfigCache(ConfigImp configImp){
        return configImp;
    }
}
