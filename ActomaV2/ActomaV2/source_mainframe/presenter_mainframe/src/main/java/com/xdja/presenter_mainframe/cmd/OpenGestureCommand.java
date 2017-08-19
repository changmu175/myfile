package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by licong on 2016/11/25.
 */
public interface OpenGestureCommand extends Command {
    void logOut(String type);

    void showDialog();
}
