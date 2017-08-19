package com.xdja.safeauth.okhttp;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by THZ on 2016/5/26.
 * okhttp的配置过滤器
 */
public class OkHttpInterceptor {

    /**
     * okhttp的打印日志级别
     * @return HttpLoggingInterceptor
     */
    public static HttpLoggingInterceptor getOkHttpLogLevel() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}
