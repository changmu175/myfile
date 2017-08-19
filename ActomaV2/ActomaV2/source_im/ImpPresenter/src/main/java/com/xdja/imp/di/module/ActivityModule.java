package com.xdja.imp.di.module;

import android.app.Activity;
import android.content.Context;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.PerActivity;
import com.xdja.imp.data.di.annotation.Scoped;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:47</p>
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity provideActivity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    @Scoped(DiConfig.CONTEXT_SCOPE_ACTIVITY)
    Context provideContext(){
        return this.activity;
    }
}
