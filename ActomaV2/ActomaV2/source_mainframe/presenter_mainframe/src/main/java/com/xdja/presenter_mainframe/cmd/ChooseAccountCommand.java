package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/18.
 */
public interface ChooseAccountCommand extends Command {
    void switchOther();
    void setAccountBySelf();
    void next();
}
