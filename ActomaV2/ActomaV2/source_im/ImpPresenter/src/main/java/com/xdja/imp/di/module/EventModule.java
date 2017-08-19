package com.xdja.imp.di.module;

import com.xdja.imp.data.eventbus.BusProvider;

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
public class EventModule {
    @Singleton
    @Provides
    BusProvider providerEventBus(){
        return new BusProvider();
    }
}
