package com.xdja.imp.di.component;

import android.app.Activity;
import android.content.Context;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.PerActivity;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.di.module.ActivityModule;

import dagger.Component;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.component</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:51</p>
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {
    Activity activity();

    @Scoped(DiConfig.CONTEXT_SCOPE_ACTIVITY)
    Context context();
}
