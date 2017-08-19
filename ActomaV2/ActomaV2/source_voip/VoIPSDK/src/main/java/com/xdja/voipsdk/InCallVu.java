package com.xdja.voipsdk;


import com.csipsimple.api.MediaState;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

import webrelay.bean.CallSession;

/**
 * Created by guoyaxin on 2016/1/7.
 */
public interface InCallVu extends ActivityVu<InCallCommand> {


     void setNameAndAvatar(CallSession callSession);

    /**
     * 主叫界面setVisibility
     */
      void showCallerViews(CallSession callSession);

    /**
     * 被叫界面
     */
      void showCalleeViews(CallSession callSession);

    /**
     * 通话结束展示界面
     */
      void showViewsAfterDisconnected(CallSession callSession);

    /**
     * 正在挂断界面
     */
     void showViewsOnDisconnecting(CallSession callSession);


    /**
     * 电话接通后显示的界面
     */
      void showViewsAfterConnect(CallSession callSession);



      void showViewsAfterCallSuccess(CallSession callSession) ;


    /**
     * 更新静音、蓝牙、扩音器等状态
     * @param mediaState
     */
     void updateMedia(MediaState mediaState);


    /**
     * 显示错误信息
     */
    void showError(CallSession callSession);

}
