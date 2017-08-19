package com.xdja.frame.data.cache;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.data.persistent.PropertyUtil;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:13:33</p>
 */
public class ConfigCacheImp implements ConfigCache {
    private String propertyName = "config.properties";

    private Map<String, String> configEntity;

    private PropertyUtil propertyUtil;

    private PreferencesUtil spUtil;

    @Inject
    public ConfigCacheImp(@NonNull PropertyUtil util,
                          PreferencesUtil spUtil) {
        this.propertyUtil = util;
        this.spUtil = spUtil;

    }

    public void setPropertyName(@NonNull String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    @Nullable
    public Map<String, String> get() {
        if (this.configEntity == null) {
            try {
                this.propertyUtil.load(propertyName);
                this.configEntity = this.propertyUtil.getAll();
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. config url . review by wangchao1. Start
                if (!TextUtils.isEmpty(configEntity.get("baseUrl")) && TextUtils.isEmpty(spUtil.gPrefStringValue("baseUrl"))) {
                    spUtil.setPreferenceStringValue("baseUrl", configEntity.get("baseUrl"));
                }
                //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. config url . review by wangchao1. End
            } catch (IOException e) {
                LogUtil.getUtils().e(e.getMessage());
                this.configEntity = null;
            }
        }

        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. config url . review by wangchao1. Start
        if(!TextUtils.isEmpty(spUtil.gPrefStringValue("baseUrl"))){
            configEntity.put("baseUrl", spUtil.gPrefStringValue("baseUrl"));
        }
        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. config url . review by wangchao1. End
        return this.configEntity;
    }
}
