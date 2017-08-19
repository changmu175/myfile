package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by geyao on 2015/7/7.
 */
public interface AboutActomaCommand extends Command {
    /**
     * 打开欢迎页
     */
    void openWelcomPage();

    /**
     * 打开功能介绍页
     */
    void openIntroduce();

    /**
     * 打开常见问题页
     */
    void openProblem();

    /**
     * 打开版本更新对话框
     */
    void openUpdate();

    /**
     * 打开使用条款和隐私政策页
     */
    void openTermPolicy();

    /**
     * 退出程序
     */
    void exit();
    /**
     * 发送软件有更新的广播
     */
    void sendUpdateEvent();
}
