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
 * <p>Time:14:29</p>
 */
public interface ApplicationLifeCycle {

    void onCreate(Application application);

    void onTerminate();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    void registerComponentCallbacks(ComponentCallbacks callback);

    void unregisterComponentCallbacks(ComponentCallbacks callback);

    void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback);

    void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback);

    void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback);

    void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback);
}
