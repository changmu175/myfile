package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by cxp on 2015/8/10.
 */
public interface IAnTongTeamDetailCommand extends Command {

    /**
     * 获得网络状态
     * @return
     */
    boolean getNetWorkState();


    /**
     * 关闭当前界面
     */
    void finishCurrentActivity();

    /**
     * 重新加载URL
     */
    void refreshUrl();

}
