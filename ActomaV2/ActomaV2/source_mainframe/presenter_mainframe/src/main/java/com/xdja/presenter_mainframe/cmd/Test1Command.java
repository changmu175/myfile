package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface Test1Command extends Command {
    void onInputComplete(CharSequence inputContent);
}
