package com.xdja.imp.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/12/7.
 */
public interface INewsRemindCommand extends Command {

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
