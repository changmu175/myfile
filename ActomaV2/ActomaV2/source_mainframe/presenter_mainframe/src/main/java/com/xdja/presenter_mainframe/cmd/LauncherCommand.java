package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/18.
 */
public interface LauncherCommand extends Command {
    void register();
    void login();
    void again();
    void oldLogin();
    void setNavBtnType(int type);
    int getNavBtnType();
    //[S]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
    void downloadCkms();
    //[E]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
}
