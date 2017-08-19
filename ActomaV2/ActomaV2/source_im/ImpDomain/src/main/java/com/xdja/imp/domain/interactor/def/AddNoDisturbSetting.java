package com.xdja.imp.domain.interactor.def;

import android.support.annotation.Nullable;

import com.xdja.imp.domain.model.ConstDef;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:15:50</p>
 */
public interface AddNoDisturbSetting extends Interactor<Boolean>{
    /**
     * 增加勿扰模式接口定义
     * @param talkerId 聊天对象
     * @param sessionType
     * @return
     */
    AddNoDisturbSetting add(@Nullable String talkerId,
                            @ConstDef.NoDisturbSettingSessionType int sessionType);
}
