package com.xdja.frame;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.di.components.ApplicationComponent;
import com.xdja.frame.di.components.DaggerApplicationComponent;
import com.xdja.frame.di.modules.ApplicationModule;
import com.xdja.frame.di.modules.EventModule;
import com.xdja.frame.di.modules.ExecutorModule;
import com.xdja.frame.di.modules.NetworkModule;
import com.xdja.frame.di.modules.UtilModule;

import java.util.List;

/**
 * <p>Summary:Application基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:11:38</p>
 */
public abstract class AndroidApplication extends Application {

    private boolean loggable = true;

    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }

    public boolean isLoggable() {
        return loggable;
    }

    private List<ApplicationLifeCycle> applicationLifeCycles;

    public List<ApplicationLifeCycle> getApplicationLifeCycles() {
        return applicationLifeCycles;
    }

    public List<ApplicationLifeCycle> initApplicationLifeCycle() {
        return null;
    }

    private ApplicationComponent applicationComponent;

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public void releaseApplicationComponent() {
        this.applicationComponent = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setLogFlag(loggable);
        this.applicationComponent =
                DaggerApplicationComponent
                        .builder()
                        .applicationModule(new ApplicationModule(this))
                        .utilModule(new UtilModule())
                        .networkModule(new NetworkModule(loggable))
                        .executorModule(new ExecutorModule())
                        .eventModule(new EventModule())
                        .build();
    }

    /**
     * 用于执行ApplicationLifeCycle的create方法，该方法可以根据不同情况灵活调用
     * @throws IllegalStateException 该方法只能被调用一次，否则将报错
     */
    public void createApplication() throws IllegalStateException{

        if (this.applicationLifeCycles != null) {
            throw new IllegalStateException("applicationLifeCycles can init only once");
        }

        this.applicationLifeCycles = applicationComponent.applicationLifeCycles();
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles) cycle.onCreate(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles) cycle.onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles) cycle.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles) cycle.onTrimMemory(level);
        }
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.registerComponentCallbacks(callback);
        }
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.unregisterComponentCallbacks(callback);
        }
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.registerActivityLifecycleCallbacks(callback);
        }
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.unregisterActivityLifecycleCallbacks(callback);
        }
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        super.registerOnProvideAssistDataListener(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.registerOnProvideAssistDataListener(callback);
        }
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        super.unregisterOnProvideAssistDataListener(callback);
        if (this.applicationLifeCycles != null && !this.applicationLifeCycles.isEmpty()) {
            for (ApplicationLifeCycle cycle : applicationLifeCycles)
                cycle.unregisterOnProvideAssistDataListener(callback);
        }
    }
}
