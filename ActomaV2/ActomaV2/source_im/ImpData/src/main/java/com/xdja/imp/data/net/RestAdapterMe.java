package com.xdja.imp.data.net;

import com.xdja.imp.data.cache.ConfigCache;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by gbc on 2016/9/9.
 */
public class RestAdapterMe {

    private Retrofit.Builder retrofitBuilder;

    private OkHttpsClientMe okHttpsClient;

    private ConfigCache configCache;

    @Inject
    public RestAdapterMe(OkHttpsClientMe okHttpsClient,
                         ConfigCache configCache) {
        this.okHttpsClient = okHttpsClient;
        this.configCache = configCache;
    }

    public Retrofit getRetrofit() {
        if (null != retrofitBuilder) {
            retrofitBuilder.baseUrl(configCache.get().getMxsEndpoint());
        } else {
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(configCache.get().getMxsEndpoint())
                    .client(okHttpsClient.getBuilder().build())
                    .addConverterFactory(JsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        return retrofitBuilder.build();
    }
}
