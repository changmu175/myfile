package com.xdja.imp.data.cache;

import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.imp.data.persistent.PropertyUtil;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:13:33</p>
 */
public class ConfigImp implements ConfigCache {

    public final String TAG_MXENDPOINT = "mxEndpoint";

    public final String PRONAME = "config.properties";

    private ConfigEntity configEntity;

    private PropertyUtil propertyUtil;

    @Inject
    public ConfigImp(PropertyUtil util){
        this.propertyUtil = util;
    }

    @Override
    public void put(ConfigEntity configEntity) {
        this.configEntity = configEntity;
    }

    @Override
    public ConfigEntity get() {
        if (configEntity == null) {
            String s;
            this.configEntity = new ConfigEntity();
            s = PreferencesServer.getWrapper(ActomaController.getApp())
                    .gPrefStringValue("mxUrl");
            if (TextUtils.isEmpty(s)) {
                this.propertyUtil.load(PRONAME);
                s = this.propertyUtil.get(TAG_MXENDPOINT);
            }
            this.configEntity.setMxsEndpoint(s);
        }
        return configEntity;
    }
}
