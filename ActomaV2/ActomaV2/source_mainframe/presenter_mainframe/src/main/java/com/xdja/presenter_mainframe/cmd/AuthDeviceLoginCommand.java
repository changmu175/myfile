package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/29.
 */
public interface AuthDeviceLoginCommand extends Command{
    void scanQr();
    void certain(String authorizeId);
}
