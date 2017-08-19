package com.xdja.imp.di.module;

import android.app.Application;
import android.content.Context;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:全局依赖提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:29</p>
 */
@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @Scoped(DiConfig.CONTEXT_SCOPE_APP)
    Context provideApplicationContext() {
        return this.application;
    }
}
