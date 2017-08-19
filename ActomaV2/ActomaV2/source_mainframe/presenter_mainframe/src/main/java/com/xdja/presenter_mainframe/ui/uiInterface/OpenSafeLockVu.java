package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.OpenSafeLockCommand;

/**
 * Created by licong on 2016/11/24.
 */
public interface OpenSafeLockVu extends ActivityVu<OpenSafeLockCommand> {

    /**
     *  设置安全锁是否开启
     * @param isOn 是否开启通知
     */
    void setSafeLock(boolean isOn);

    /**
     * 设置锁屏锁定是否开启
     *
     * @param isOn 是否开启通知
     */
    void setLockSreen(boolean isOn);

    /**
     * 设置后台运行锁定是否开启
     *
     * @param isOn 是否开启通知
     */
    void setBackgroundLock(boolean isOn);

}
