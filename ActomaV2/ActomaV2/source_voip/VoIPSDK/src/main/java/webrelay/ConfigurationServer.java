package webrelay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.xdja.dependence.uitls.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>Summary:读取配置信息的类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.atcommen</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/7</p>
 * <p>Time:13:38</p>
 */
public class ConfigurationServer {

    private static final String assetsConfigName = "default.properties";

    private static ConfigurationServer defaultConfig;

    private static ConfigurationServer assetsConfig;

    /**
     * 获取私有目录中config.properties文件的配置
     *
     * @param context 上下文句柄
     * @return 目标对象
     */
    public static ConfigurationServer getDefaultConfig(@NonNull Context context,String defaultConfigName) {
        if (defaultConfig == null) {
            try {
                String path = context.getFilesDir().getAbsolutePath()
                        + File.separator + defaultConfigName;
                File configFile = new File(path);
                InputStream is = new FileInputStream(configFile);
                defaultConfig = new ConfigurationServer(is);
            } catch (IOException ex) {
                LogUtil.getUtils().e(ex.getMessage());
                return null;
            }
        }
        return defaultConfig;
    }
    /**
     * 获取Assets目录中default.properties文件的配置
     *
     * @param context 上下文句柄
     * @return 目标对象
     */
    public static ConfigurationServer getAssetsConfig(@NonNull Context context,String assetsConfigName) {
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

    /**
     * 增加/更新配置信息
     *
     * @param configName 配置名称
     * @param value      目标值
     * @param <T>+++++++++++++++++++++++++++++++++++++++++++++++++
     * @return 是否增加/更新成功
     */
    public <T> boolean write(@Nullable String configName, @Nullable T value) {
        if (TextUtils.isEmpty(configName))
            return false;
        if (value == null)
            return false;
        properties.setProperty(configName, value.toString());
        return true;
    }

    public static class ConfigName {
        public static final String CONFIGRAGION_ISFULLVERSION = "IsWholeVersion";
    }
}
