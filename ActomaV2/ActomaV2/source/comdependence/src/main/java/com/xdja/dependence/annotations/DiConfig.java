package com.xdja.dependence.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/28</p>
 * <p>Time:10:10</p>
 */
public final class DiConfig {
    @Retention(RetentionPolicy.RUNTIME)
    @StringDef(value = {CONTEXT_SCOPE_APP,CONTEXT_SCOPE_ACTIVITY})
    public @interface ContextScopeType{}

    /**
     * 全局Context标签
     */
    public static final String CONTEXT_SCOPE_APP = "application";
    /**
     * Activity Context标签
     */
    public static final String CONTEXT_SCOPE_ACTIVITY = "activity";

    @Retention(RetentionPolicy.RUNTIME)
    @StringDef(value = {CONN_HTTP,CONN_HTTPS,CONN_HTTP_DEF,CONN_HTTPS_DEF,CONN_HTTP_TICKET,CONN_HTTPS_TICKET})
    public @interface ConnType{}
    /**
     * http链接
     */
    public static final String CONN_HTTP = "http";
    /**
     * https链接
     */
    public static final String CONN_HTTPS = "https";


    public static final String CONN_HTTP_DEF = "http_def";

    public static final String CONN_HTTPS_DEF = "https_def";

    public static final String CONN_HTTP_TICKET = "httpTicket";

    public static final String CONN_HTTPS_TICKET = "httpsTicket";

    @StringDef(value = {BUNDLE_ACTIVITY,BUNDLE_FRAGMENT})
    public @interface BundleType{}

    public static final String BUNDLE_ACTIVITY = "activity";

    public static final String BUNDLE_FRAGMENT = "fragment";

    @StringDef(value = {
            TYPE_CLOUD,
            TYPE_DISK,
            TYPE_MEMORY
    })
    public @interface StoreType{}

    public static final String TYPE_DISK = "disk";

    public static final String TYPE_CLOUD = "cloud";

    public static final String TYPE_MEMORY = "memory";



    public static final String CONFIG_PROPERTIES_NAME = "config";

    public static final String PN_PROPERTIES_NAME = "pn";
}
