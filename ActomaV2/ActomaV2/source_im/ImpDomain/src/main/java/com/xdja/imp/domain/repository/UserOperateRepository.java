package com.xdja.imp.domain.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.SettingTopConfig;

import java.util.List;

import rx.Observable;

/**
 * <p>Summary:用户操作业务仓库</p>
 * <p>Description:</p>
 * <p>Package:com.imdo.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/2</p>
 * <p>Time:14:46</p>
 */
public interface UserOperateRepository {

    /**
     * 保存用户账号到本地
     * @param userAccount
     * @return
     */
    Observable<Boolean> saveUserAccount(@NonNull String userAccount);


    /**
     * 查询当前登录信息
     * @return
     */
    Observable<String> queryUserAccount();
    /**
     * 将漫游信息保存到云端
     *
     * @param state 漫游配置
     * @param time  漫游时长
     * @return 保存结果
     */
    Observable<Boolean> saveRoamSetting2Cloud(@ConstDef.RoamState int state, int time);

    /**
     * 从服务器上获取漫游信息配置
     *
     * @return 漫游信息
     */
    Observable<RoamConfig> getRoamSetttingAtCloud();

    /**
     * 将漫游信息保存到本地
     *
     * @param state 漫游配置
     * @param time  漫游时长
     * @return 保存结果
     */
    Observable<Boolean> saveRoamSetting2Local(@ConstDef.RoamState int state, int time);

    /**
     * 本地查询漫游信息配置
     *
     * @return 漫游信息
     */
    Observable<RoamConfig> queryRoamSettingAtLocal();

    /**
     * 增加会话免打扰到云端
     *
     * @param talkerId    联系人（聊天对象）ID
     * @param sessionType 会话类型
     * @return 保存结果
     */
    Observable<Boolean> addNoDisturb2Cloud(@NonNull String talkerId,
                                           @ConstDef.NoDisturbSettingSessionType int sessionType);

    /**
     * 增加会话免打扰到本地
     *
     * @param talkerId 会话标识
     * @return 保存结果
     */
    Observable<Boolean> addNoDisturb2Local(@NonNull String talkerId);

    /**
     * 从云端删除勿扰模式设置
     *
     * @param talkerId    联系人（聊天对象）ID
     * @param sessionType 会话类型
     * @return 删除结果
     */
    Observable<Boolean> deleteNoDisturbAtCloud(@NonNull String talkerId,
                                               @ConstDef.NoDisturbSettingSessionType int sessionType);

    /**
     * 从本地删除勿扰模式设置
     *
     * @param talkerId 会话标识
     * @return 删除结果
     */
    Observable<Boolean> deleteNoDisturbAtLocal(@NonNull String talkerId);




    /**
     * 将草稿保存到本地
     *
     * @param talkerId 会话对象
     * @param draft    草稿信息
     * @return 保存结果
     */
    Observable<Boolean> saveDraft2Local(@NonNull String talkerId, @Nullable String draft,
                                        long draftTime);


    /**
     * 从服务器查询勿扰模式设置
     *
     * @return
     */
    Observable<List<NoDisturbConfig>> queryNoDisturbSettingsAtCloud();

    /**
     * 查询本地会话相关数据库状态
     *
     * @return 数据库是否为最新数据
     */
    Observable<Boolean> queryLocalSessionState();

    /**
     * 设置本地数据库标记
     * @param isDone 待设置标记
     * @return 设置结果
     */
    Observable<Boolean> setLocalSessionState(boolean isDone);

    /**
     * 保存勿扰模式设置到本地数据库
     * @param configs 待保存数据
     * @return 保存结果
     */
    Observable<List<SessionConfig>> saveNoDisturb2Local(List<NoDisturbConfig> configs);

    /**
     * 根据会话对象查询会话相关设置
     *
     * @param talkerId 会话对象
     * @return 会话设置
     */
    Observable<SessionConfig> querySingleSessionSettingAtLocal(@NonNull String talkerId);

    /**
     * 删除所有会话的草稿
     */
    Observable<Boolean> deleteAllDraft();

    /**
     * 删除一条会话设置信息
     */
    Observable<Boolean> deleteSingleSessionSettingAtLocal(@NonNull String talkFlag);

    /**
     * 从本地查询会话设置
     *
     * @return 查询结果
     */
    Observable<List<SessionConfig>> querySessionSettingsAtLocal();

    /**
     * 增加会话置顶到云端
     *
     * @param talkerId    联系人（聊天对象）ID
     * @param sessionType 会话类型
     * @return 保存结果
     */
    Observable<Boolean> addSettingTop2Cloud(@NonNull String talkerId,
                                           boolean sessionType);

    /**
     * 增加会话置顶到本地
     *
     * @param talkerId 会话标识
     * @return 保存结果
     */
    Observable<Boolean> addSettingTop2Local(@NonNull String talkerId);

    /**
     * 从云端删除置顶模式设置
     *
     * @param talkerId    联系人（聊天对象）ID
     * @param sessionType 会话类型
     * @return 删除结果
     */
    Observable<Boolean> deleteSettingTopAtCloud(@NonNull String talkerId,
                                               boolean sessionType);

    /**
     * 从本地删除置顶模式设置
     *
     * @param talkerId 会话标识
     * @return 删除结果
     */
    Observable<Boolean> deleteSettingTopAtLocal(@NonNull String talkerId);


    Observable<List<SettingTopConfig>> querySettingTopSettingsAtCloud();

    /**
     * 保存置顶模式设置到本地数据库
     * @param configs 待保存数据
     * @return 保存结果
     */
    Observable<List<SessionConfig>> saveSettingTop2Local(List<SettingTopConfig> configs);


    /**
     * 将会话的置顶设置保存到本地
     *
     * @param talkerId 会话对象
     * @param isTop    是否置顶
     * @return 保存结果
     */
    Observable<Boolean> saveSessionTopSetting2Local(@NonNull String talkerId, boolean isTop);


    /**
     * 将所有回话设置，保存到本地
     * @param configs 回话配置对象
     * @return 回话配置对象
     */
    Observable<List<SessionConfig>> saveSettingTopAndNodisturb2Local(List<SessionConfig> configs);

    Observable<Integer> releaseRepository();
}
