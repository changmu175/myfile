package com.xdja.imp.data.persistent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

/**
 * <p>Summary:配置文件操作类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.persistent</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/16</p>
 * <p>Time:13:39</p>
 */
public class PropertyUtil {
    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 配置文件对象
     */
    private Properties properties;

    @Inject
    public PropertyUtil(@NonNull
                        @Scoped(value = DiConfig.CONTEXT_SCOPE_APP)
                        Context context) {
        this.context = context;
    }

    /**
     * 从某个文件中加载配置
     *
     * @param fileName 文件名称
     */
    public void load(@NonNull String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        try {
            InputStream is = this.context.getAssets().open(fileName);
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个配置的值
     *
     * @param name key
     * @return value
     */
    public String get(@NonNull String name) {
        if (properties != null && !TextUtils.isEmpty(name)) {
            return properties.getProperty(name);
        }
        return null;
    }

    /**
     * 获取一个配置的值
     *
     * @param name         key
     * @param defaultValue 默认值
     * @return value
     */
    public String get(@NonNull String name, @Nullable String defaultValue) {
        if (properties != null && !TextUtils.isEmpty(name)) {
            return properties.getProperty(name, defaultValue);
        }
        return null;
    }
}
