package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.DropMessageCommand;

/**
 * Created by luopeipei on 2015/11/9.
 */
public interface DropMessageVu extends ActivityVu<DropMessageCommand> {

    /**
     * 弹出删除聊天记录复选框
     * @param msg
     * @param type
     */
    void showDialog(int msg, final int type);

    /**
     * 设置听筒模式开关
     *
     * @param isOn
     */
    void setReceiverMode(boolean isOn);
}
