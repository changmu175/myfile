package com.xdja.imp.di.component;

import android.content.Context;

import com.google.gson.Gson;
import com.xdja.contactopproxy.ContactCallBack;
import com.xdja.contactopproxy.ContactService;
import com.xdja.imp.data.net.OkHttpsClientMe;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.ImApplication;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.ConnSecurity;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.di.module.ApplicationModule;
import com.xdja.imp.di.module.CacheModule;
import com.xdja.imp.di.module.ContactProxyModule;
import com.xdja.imp.di.module.EventModule;
import com.xdja.imp.di.module.ExecutorModule;
import com.xdja.imp.di.module.HttpsModule;
import com.xdja.imp.di.module.UtilModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * <p>Summary:Application级别的位图</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.component</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:38</p>
 */
@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                CacheModule.class,
                EventModule.class,
                ExecutorModule.class,
                HttpsModule.class,
                UtilModule.class,
                ContactProxyModule.class
        })
public interface ApplicationComponent {
    void inject(ImApplication application);

    @Scoped(DiConfig.CONTEXT_SCOPE_APP)
    Context context();



    UserCache userCache();

    CardCache cardCache();

    ConfigCache configCache();

    BusProvider busProvider();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    ContactCallBack contactCallBack();

    //@ConnSecurity(DiConfig.CONN_HTTP) OkHttpClient okHttpClient();

    //@ConnSecurity(DiConfig.CONN_HTTP) OkClient okClient();

    @ConnSecurity(DiConfig.CONN_HTTPS) OkHttpsClientMe okHttpsClientMe();

    Gson gson();

    ContactService contactService();
}
