package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by geyao on 2015/11/5.
 */
public interface NewQuickOpenOtherAppCommand extends Command {
    /**
     * 点击快速开启第三方应用所在布局
     *
     * @param isOpen 是否开启
     */
    void clickQuickOpenOtherApp(boolean isOpen);
}
