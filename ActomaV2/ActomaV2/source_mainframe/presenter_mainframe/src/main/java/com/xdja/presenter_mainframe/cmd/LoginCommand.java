package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface LoginCommand extends Command {
    void login(String account, String password);
    void messageVerifyLogin();
    void forgetPassword();
    void register();
    void afterAccountChanged(boolean isEmpty);
}
