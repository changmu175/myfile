package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/15.
 */
public interface EmpowerDeviceLoginCommand extends Command {
    void generateAgainEmpower();
    void cannotEmpower();
}
