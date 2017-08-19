package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.Test1Command;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuTest1 extends ActivityVu<Test1Command> {
    void showCovertedMsg(String msg);
}
