package com.xdja.presenter_mainframe.di.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xdja.dependence.annotations.BundleSpe;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.PerActivity;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;

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
@Component(
        modules = {ActivityModule.class, SubActivityModule.class}
)
public interface ActivityComponent {
    Activity activity();

    @ContextSpe(DiConfig.CONTEXT_SCOPE_ACTIVITY)
    Context context();

    Intent intent();

    @Nullable
    @BundleSpe(DiConfig.BUNDLE_ACTIVITY)
    Bundle bundle();
}
