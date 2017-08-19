package com.xdja.imp.data.repository.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.domain.model.KeyValuePair;

import java.util.List;

import rx.Observable;

/**
 * <p>Summary:硬盘存储接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:13:43</p>
 */
public interface DiskDataStore {
    /**
     * 保存用户设置
     *
     * @param settings 待保存的用户设置
     * @return 保存结果
     */
    Observable<Boolean> saveKeyValuePairs(@NonNull KeyValuePair<String, String> settings);

    /**
     * 保存用户设置
     *
     * @param settings 待保存的用户设置
     * @return 保存结果
     */
    Observable<Boolean> saveIntKeyValuePairs(@NonNull KeyValuePair<String, Integer> settings);

    /**
     * 从本地查询字符类型持久化数据
     *
     * @param key 目标键
     * @return 目标值
     */
    Observable<String> queryStringSharePref(@Nullable String key);
    /**
     * 从本地查询整型持久化数据
     *
     * @param key 目标键
     * @return 目标值
     */
    Observable<Integer> queryIntegerSharePref(@Nullable String key);

    /**
     * 从本地查询会话信息
     *
     * @return 会话信息集合
     */
    Observable<List<SessionParam>> getLocalSessionParams();

    /**
     * 从本地查询单个会话信息
     *
     * @param sessionFlag 会话标识
     * @return 会话信息
     */
    Observable<SessionParam> getLocalSingleSession(@NonNull String sessionFlag);

    /**
     * 更新本地单个会话信息
     *
     * @param sessionParam 会话对象
     * @return 操作结果
     */
    Observable<Boolean> saveOrUpdateLocalSingleSession(@NonNull SessionParam sessionParam);

    /**
     * 更新本地所有会话信息
     *
     * @param sessionParams 会话对象列表
     * @return 操作结果
     */
    Observable<Boolean> saveOrUpdateLocalSession(@NonNull List<SessionParam> sessionParams);

    /**
     * 删除本地单个会话信息
     *
     * @param sessionParam 会话对象
     * @return 操作结果
     */
    Observable<Boolean> deleteLocalSingleSession(@NonNull SessionParam sessionParam);

    /**
     * 释放本地存储
     * @return
     */
    Observable<Integer> releaseDiskDataStore();
}
