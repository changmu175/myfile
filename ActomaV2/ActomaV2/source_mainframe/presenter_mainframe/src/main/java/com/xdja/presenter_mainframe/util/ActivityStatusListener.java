package com.xdja.presenter_mainframe.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.presenter_mainframe.ActomaApplication;

import javax.inject.Inject;

/**
 * Created by ldy on 16/5/25.
 */
@AppScope
public class ActivityStatusListener {
    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_BACKGROUND = 0;
    public static final int STATUS_FRONT = 1;

    private final Context context;
    private int status = STATUS_UNKNOWN;

    @Inject
    ActivityStatusListener(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        this.context = context;
        registerCallback();
    }

    private void registerCallback() {
        ((ActomaApplication)context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                status = STATUS_FRONT;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                status = STATUS_BACKGROUND;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public int getStatus(){
        return status;
    }

}
