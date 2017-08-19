package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/29.
 */
public interface AccountSafeCommand extends Command{
    void setActomaAccount();
    void setPhoneNumber();
    void loginDeviceManager();

    /**
     * 检测原密码
     * @param password 密码
     */
    void checkPassword(String password);

    /**
     * 设置安全锁
     */
    void setSafeLock();
}
