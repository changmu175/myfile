package com.xdja.frame.data.remedy;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
public interface RemedyCache {
    /**
     * 注册一个补救点
     *
     * @param _id 补救点标识
     * @param t   补救相关对象
     * @param <T> 对象类型
     * @return 注册结果
     */
    <T> boolean cache(String _id, T t);

    /**
     * 注册一个简单的补救点
     *
     * @param _id 补救点标识
     * @return 注册结果
     */
    boolean simpleCache(String _id);

    /**
     * 读取一个简单的补救点
     *
     * @param _id 补救点标识
     * @return 是否存在该补救点
     */
    boolean readSimpleCache(String _id);

    /**
     * 读取一个补救点
     *
     * @param _id 补救点标识
     * @param <T> 对象类型
     * @return 补救点相关的业务数据
     */
    @Nullable
    <T> T readCache(String _id, Class<T> cls);

    /**
     * 删除一个补救点
     *
     * @param _id 补救点标识
     */
    void removeCache(String _id);
}
