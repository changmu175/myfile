package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/15.
 */
public interface WriteRegistrationInfoCommand extends Command {
    void next(String nickName, String password, String passwordAgain);
    void atTerms();
    void avatarClicked();
}
