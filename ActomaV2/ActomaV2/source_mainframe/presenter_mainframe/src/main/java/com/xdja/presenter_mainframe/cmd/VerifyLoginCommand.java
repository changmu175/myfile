package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/15.
 */
public interface VerifyLoginCommand extends Command {
    void phoneVerifyCodeVerify();
    void friendPhoneVerify();
}
