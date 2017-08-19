package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * <p>Summary:主界面业务接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.command</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:17:02</p>
 */
public interface MainFrameCommand extends Command {
    /**
     * 跳转到Setting页面
     */
    void setting();

    void scan();

    /**
     * 搜索
     */
    void search();

    /**
     * 添加好友
     */
    void addUser();

    /**
     * 创建群组
     */
    void createGroup();

    /**
     * 打开三方通道
     */
    void openThirdTransfer();
}
