package com.xdja.frame.data.cache;

import android.support.annotation.Nullable;

import java.util.Map;

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
     * 获取配置信息对象
     *
     * @return 配置信息对象
     */
    @Nullable
    Map<String,String> get();
}
