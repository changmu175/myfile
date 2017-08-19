package com.xdja.imp.data.di;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DiConfig {

    /**
     * <p>Summary:</p>
     * <p>Description:</p>
     * <p>Package:com.xdja.dev.di</p>
     * <p>Author:fanjiandong</p>
     * <p>Date:2015/10/28</p>
     * <p>Time:10:10</p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @StringDef(value = {CONTEXT_SCOPE_APP,CONTEXT_SCOPE_ACTIVITY})
    public @interface ContextScope{}

    /**
     * 全局Context标签
     */
    public static final String CONTEXT_SCOPE_APP = "application";
    /**
     * Activity Context标签
     */
    public static final String CONTEXT_SCOPE_ACTIVITY = "activity";

    @Retention(RetentionPolicy.RUNTIME)
    @StringDef(value = {CONN_HTTP,CONN_HTTPS})
    public @interface ConnType{}
    /**
     * http链接
     */
    public static final String CONN_HTTP = "http";
    /**
     * https链接
     */
    public static final String CONN_HTTPS = "https";


}
