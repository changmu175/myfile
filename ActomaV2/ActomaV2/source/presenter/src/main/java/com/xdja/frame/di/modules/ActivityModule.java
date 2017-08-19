package com.xdja.frame.di.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xdja.dependence.annotations.BundleSpe;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.PerActivity;

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
    @ContextSpe(DiConfig.CONTEXT_SCOPE_ACTIVITY)
    Context provideContext() {
        return this.activity;
    }

    @PerActivity
    @Provides
    Intent provideIntent() {
        return this.activity.getIntent();
    }

    @Provides
    @PerActivity
    @Nullable
    @BundleSpe(DiConfig.BUNDLE_ACTIVITY)
    Bundle provideBundle(Intent intent) {
        return intent.getExtras();
    }
}
