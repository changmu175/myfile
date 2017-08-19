package com.xdja.frame.data.net;

import android.support.annotation.NonNull;

import com.xdja.dependence.exeptions.NetworkException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>Summary:创建REST请求适配的类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.data.net</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/13</p>
 * <p>Time:20:04</p>
 */
public class ServiceGenerator {

    //public static final String API_BASE_URL = "https://192.168.68.100:8091/account-web/";
//    public static final String API_BASE_URL = "https://11.12.109.25:9443/account/api/v1/";
//    public final String API_BASE_URL = "https://11.12.110.150:6699/account/api/v1/";
    public String API_BASE_URL;

    private Retrofit.Builder retrofitBuilder;

    private OkHttpClient.Builder okHttpBuilder;

    public ServiceGenerator(@NonNull OkHttpClient.Builder builder,@NonNull String baseUrl) {
        this.okHttpBuilder = builder;
        this.API_BASE_URL = baseUrl;
    }

    public void resetService(@NonNull String baseUrl){
        this.retrofitBuilder = null;
        this.API_BASE_URL = baseUrl;
    }

    public <S> S createService(Class<S> serviceClass, String baseUrl) {

        if (this.retrofitBuilder == null) {
            if (this.okHttpBuilder == null) {
                throw new NetworkException("OkhttpBuilder is null");
            }
            OkHttpClient client = this.okHttpBuilder.build();
            this.retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());
        } else {
            this.retrofitBuilder.baseUrl(baseUrl);
        }

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, API_BASE_URL);
    }
}
