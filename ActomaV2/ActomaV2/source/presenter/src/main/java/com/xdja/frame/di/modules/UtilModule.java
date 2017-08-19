package com.xdja.frame.di.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.cache.ConfigCacheImp;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.data.ckms.CkmsManager;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.data.remedy.RemedyCache;
import com.xdja.frame.data.remedy.RemedyCacheImp;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:16:10</p>
 */
@Module
public class UtilModule {

    public static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";


    public static final String PN_PROPERTIES_FILE_NAME = "pn.ipport.properties";


    @Singleton
    @Provides
    Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    @Named(DiConfig.CONFIG_PROPERTIES_NAME)
    ConfigCache provideDefaultConfigCache(ConfigCacheImp imp) {
        imp.setPropertyName(CONFIG_PROPERTIES_FILE_NAME);
        return imp;
    }

    @Singleton
    @Provides
    @Named(DiConfig.PN_PROPERTIES_NAME)
    ConfigCache providePnConfigCache(ConfigCacheImp imp) {
        imp.setPropertyName(PN_PROPERTIES_FILE_NAME);
        return imp;
    }

    @Singleton
    @Provides
    RemedyCache provideRemedyCache(
            @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
            Gson gson) {
        PreferencesUtil util = new PreferencesUtil(context, "remedies");
        RemedyCacheImp imp = new RemedyCacheImp(util,gson);
        return imp;
    }

    @Singleton
    @Provides
    TFCardManager provideTFCardManager(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context){
        return new TFCardManager(context);
    }

    /*[S]add by tangsha@20160629 for ckms*/
    @Singleton
    @Provides
    CkmsManager provideCkmsManager(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context){
        return new CkmsManager(context);
    }
    /*[E]add by tangsha@20160629 for ckms*/
}
