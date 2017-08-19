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
public interface DeleteNoDisturbSetting extends Interactor<Boolean>{
    DeleteNoDisturbSetting delete(@Nullable String talkerId,
                                  @ConstDef.NoDisturbSettingSessionType int sessionType);
}
