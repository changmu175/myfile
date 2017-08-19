package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by luopeipei on 2015/11/9.
 */
public interface DropMessageCommand extends Command {
    /**
     * 删除所有聊天记录
     */
    void dropMessage();

    /**
     * 删除所有通话记录
     */
    void clearCallLog();

    /**
     * 打开听筒模式
     *
     * @param isOn 打开
     */
    void openReceiverMode(boolean isOn);
}
