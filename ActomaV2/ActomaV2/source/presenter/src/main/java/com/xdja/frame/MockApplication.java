package com.xdja.frame;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/18</p>
 * <p>Time:15:01</p>
 */
public abstract class MockApplication implements ApplicationLifeCycle{

    @Override
    public void onCreate(Application application) {

    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {

    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {

    }

    @Override
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }

    @Override
    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }

    @Override
    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }

    @Override
    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }
}
