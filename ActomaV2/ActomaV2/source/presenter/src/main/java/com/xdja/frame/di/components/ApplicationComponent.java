package com.xdja.frame.di.components;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;

import com.google.gson.Gson;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.event.BusProvider;
import com.xdja.frame.ApplicationLifeCycle;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.net.ServiceGenerator;
import com.xdja.frame.data.remedy.RemedyCache;
import com.xdja.frame.di.modules.ApplicationModule;
import com.xdja.frame.di.modules.EventModule;
import com.xdja.frame.di.modules.ExecutorModule;
import com.xdja.frame.di.modules.NetworkModule;
import com.xdja.frame.di.modules.UtilModule;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by xdja-fanjiandong on 2016/3/17.
 */
@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                EventModule.class,
                ExecutorModule.class,
                NetworkModule.class,
                UtilModule.class
        }
)
public interface ApplicationComponent {

    @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
    Context context();

    List<ApplicationLifeCycle> applicationLifeCycles();

    LayoutInflater layoutInflater();

    ActivityManager activityManager();

    AlarmManager alarmManager();

    NotificationManager notificationmanager();

    BusProvider busProvider();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    @ConnSecuritySpe(DiConfig.CONN_HTTP)
    OkHttpClient.Builder okHttpClient();

    @ConnSecuritySpe(DiConfig.CONN_HTTPS)
    OkHttpClient.Builder okHttpsClient();

    @ConnSecuritySpe(DiConfig.CONN_HTTP)
    ServiceGenerator httpService();

    @ConnSecuritySpe(DiConfig.CONN_HTTPS)
    ServiceGenerator httpsService();

    Gson gson();

    @Named(DiConfig.PN_PROPERTIES_NAME)
    ConfigCache pnConfigCache();

    @Named(DiConfig.CONFIG_PROPERTIES_NAME)
    ConfigCache defaultConfigCache();

    RemedyCache remedyCache();
}
