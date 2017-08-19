package com.xdja.imp.domain.interactor.def;

import android.support.annotation.NonNull;

/**
 * <p>Summary:保存置顶设置业务接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:11:35</p>
 */
public interface SaveTopSetting extends Interactor<Boolean> {
    /**
     * 保存置顶设置
     *
     * @param talkerId 会话对象ID
     * @param isTop    是否置顶
     * @return 业务接口
     */
    SaveTopSetting save(@NonNull String talkerId, boolean isTop);
}
