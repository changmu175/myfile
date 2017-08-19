package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.OpenGestureCommand;
import com.xdja.presenter_mainframe.widget.LockPatternView;

/**
 * Created by licong on 2016/11/25.
 */
public interface OpenGestureVu extends ActivityVu<OpenGestureCommand> {
    /**
     * 设置头像
     *
     * @param avatarId
     * @param thumbnailId
     */
    void setImage(String avatarId, String thumbnailId);

    /**
     * 设置需要显示的TextView
     */
    void setText(String type,int retry);

    /**
     * 获取LockPatternView
     * @return
     */
    LockPatternView getLockPatternView();
}
