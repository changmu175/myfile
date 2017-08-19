package com.xdja.imp.data.cache;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:13:25</p>
 */
public interface ConfigCache {
    /**
     * 设置配置信息对象
     *
     * @param configCache 配置信息对象
     */
    void put(ConfigEntity configCache);

    /**
     * 获取配置信息对象
     *
     * @return 配置信息对象
     */
    ConfigEntity get();
}
