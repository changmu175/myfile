package com.xdja.comm.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件的类
 * 2016/10/13
 */
public class ConfigurationServer {

    private static final String assetsConfigName = "default.properties";

    private static ConfigurationServer assetsConfig;

    /**
     * 获取Assets目录中default.properties文件的配置
     *
     * @param context 上下文句柄
     * @return 目标对象
     */
    public static ConfigurationServer getAssetsConfig(@NonNull Context context) {
        if (assetsConfig == null) {
            try {
                InputStream is = context.getAssets().open(assetsConfigName);
                assetsConfig = new ConfigurationServer(is);
            } catch (IOException e) {
                LogUtil.getUtils().i(e.getMessage());
                return null;
            }
        }
        return assetsConfig;
    }


    private Properties properties;

    public ConfigurationServer(@NonNull InputStream inputStream) {
        try {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException ex) {
            LogUtil.getUtils().e(ex.getMessage());
        }
    }

    /**
     * 读取配置信息
     *
     * @param configName   配置名称
     * @param defaultValue 默认值
     * @param cls          值类型
     * @param <T>
     * @return 读取到的配置
     */
    public <T> T read(@Nullable String configName, @NonNull T defaultValue, @NonNull Class<T> cls) {
        if (TextUtils.isEmpty(configName)) {
            return defaultValue;
        }
        try {
            String value = properties.getProperty(configName);
            if (cls == String.class)
                return cls.cast(value);
            else if (cls == Integer.class)
                return cls.cast(Integer.valueOf(value));
            else if (cls == Boolean.class)
                return cls.cast(Boolean.valueOf(value));
            else if (cls == Long.class)
                return cls.cast(Long.valueOf(value));
            else
                return defaultValue;
        } catch (ClassCastException ex) {
            LogUtil.getUtils().e(ex.getMessage());
            return defaultValue;
        }
    }

}
