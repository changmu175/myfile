package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by licong on 2016/11/24.
 */
public interface OpenSafeLockCommand extends Command {
    /**
     *  安全锁是否开启
     * @param isOn 是否开启通知
     */
    void safeLock(boolean isOn);

    /**
     * 锁屏锁定是否开启
     *
     * @param isOn 是否开启通知
     */
    void lockSreen(boolean isOn);

    /**
     * 后台运行锁定是否开启
     *
     * @param isOn 是否开启通知
     */
    void backgroundLock(boolean isOn);

    /**
     * 修改手势图案
     */
    void modifiedGesture();
}
