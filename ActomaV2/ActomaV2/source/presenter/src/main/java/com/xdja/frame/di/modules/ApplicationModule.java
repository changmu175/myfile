package com.xdja.frame.di.modules;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.frame.AndroidApplication;
import com.xdja.frame.ApplicationLifeCycle;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:Application依赖提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:29</p>
 */
@Module
public class ApplicationModule {

    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    List<ApplicationLifeCycle> provideApplicationLifeCycles(){
        return this.application.initApplicationLifeCycle();
    }

    @Provides
    @Singleton
    LayoutInflater provideLayoutInflater(){
        return LayoutInflater.from(this.application);
    }

    @Singleton
    @Provides
    ActivityManager provideActivityManager(){
        return ((ActivityManager) this.application.getSystemService(Context.ACTIVITY_SERVICE));
    }

    @Singleton
    @Provides
    AlarmManager provideAlarmManager(){
        return ((AlarmManager) this.application.getSystemService(Context.ALARM_SERVICE));
    }

    @Singleton
    @Provides
    NotificationManager provideNotificationManager(){
        return ((NotificationManager) this.application.getSystemService(Context.NOTIFICATION_SERVICE));
    }
}
