package com.xdja.imp.data.net;

import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.UserCache;

import javax.inject.Inject;

/**
 * Created by gbc on 2016/9/9.
 */
public class ApiFactoryMe {

    private RestAdapterMe restAdapterMe;

    private UserSettingApi userSettingApi;

    @Inject
    public ApiFactoryMe(RestAdapterMe restAdapterMe,
                      ConfigCache configCache,
                      UserCache userCache){
        this.restAdapterMe = restAdapterMe;
    }

    /**
     * 获取用户设置相关网络接口操作对象
     * @return  目标对象
     */
    public UserSettingApi getUserSettingApi(){
        //if (userSettingApi == null) {
            userSettingApi = restAdapterMe.getRetrofit().create(UserSettingApi.class);
        //}
        return userSettingApi;
    }
}
