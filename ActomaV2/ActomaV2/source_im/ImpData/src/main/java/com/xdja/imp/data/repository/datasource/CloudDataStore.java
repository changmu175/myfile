package com.xdja.imp.data.repository.datasource;

import android.support.annotation.NonNull;

import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionTopSetter;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;
import com.xdja.imp.domain.model.SettingTopConfig;

import java.util.List;

import rx.Observable;

/**
 * <p>Summary:网络存储接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:17:22</p>
 */
public interface CloudDataStore {
    /**
     * 保存漫游信息到云端
     *
     * @param setter 漫游设置
     * @return 保存结果
     */
    Observable<Boolean> saveRoamSetting2Cloud(@NonNull RoamSetter setter);

    /**
     * 从云端获取漫游信息
     *
     * @param account 帐号
     * @param cardId  卡ID
     * @return 漫游信息
     */
    Observable<RoamConfig> getRoamSettingAtCloud(@NonNull String account,@NonNull String cardId);

    /**
     * 保存勿扰模式设置到云端
     * @param noDisturbSetter 勿扰模式设置
     * @return  保存结果
     */
    Observable<Boolean> addNoDisturb2Cloud(@NonNull NoDisturbSetter noDisturbSetter);

    /**
     * 从云端删除指定的勿扰模式设置
     * @param noDisturbSetter 勿扰模式设置
     * @return 删除结果
     */
    Observable<Boolean> deleteNoDisturbAtCloud(@NonNull NoDisturbSetter noDisturbSetter);

    /**
     * 从云端查询勿扰模式设置
     * @param account 帐号
     * @return 获取到的勿扰模式设置
     */
    Observable<List<NoDisturbConfig>> getNoDisturbSettingsAtCloud(@NonNull String account);

    /**
     * 保存置顶模式设置到云端
     * @param sessionTopSetter 置顶模式设置
     * @return  保存结果
     */
    Observable<Boolean> addSettingTop2Cloud(@NonNull SessionTopSetter sessionTopSetter);

    /**
     * 从云端删除指定的置顶模式设置
     * @param sessionTopSetter 置顶模式设置
     * @return 删除结果
     */
    Observable<Boolean> deleteSettingTopAtCloud(@NonNull SessionTopSetter sessionTopSetter);


    /**
     * 从云端查询置顶模式设置
     * @param account 帐号
     * @return 获取到的勿扰模式设置
     */

    Observable<List<SettingTopConfig>> getSettingTopSettingsAtCloud(@NonNull String account);
}
