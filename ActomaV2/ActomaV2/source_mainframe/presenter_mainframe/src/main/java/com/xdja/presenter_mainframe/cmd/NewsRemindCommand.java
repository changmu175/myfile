package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

public interface NewsRemindCommand extends Command {

    /**
     * 新消息通知
     *
     * @param isOn 是否开启通知
     */
    void newsRemind(boolean isOn);

    /**
     * 声音方式通知
     *
     * @param isOn 是否开启通知
     */
    void newsRemindByRing(boolean isOn);

    /**
     * 振动方式通知
     *
     * @param isOn 是否开启通知
     */
    void newsRemindByShake(boolean isOn);
}
