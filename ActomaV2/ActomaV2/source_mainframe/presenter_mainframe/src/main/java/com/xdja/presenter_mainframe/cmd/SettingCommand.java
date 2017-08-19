package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

public interface SettingCommand extends Command {

    /**
     * 查看个人二维码信息
     */
    void viewZxing();

    /**
     * 个人信息详情
     */
    void userDetail();

    /**
     * 三方应用加密服务
     *
     */
    void thirdpartService();

    /**
     * 新消息提醒
     */
    void newsRemind();

    /**
     * 勿扰模式
     */
    void noDistrub();

    /**
     * 聊天通话
     */
    void dropMessage();

    /**
     * 修改安全口令
     */
    void changePassword();

    /**
     * 我的设备
     */
    void allDevices();

    /**
     * 关于安通+
     */
    void aboutSoft();

    /**
     * 关于芯片
     */
    void aboutChip();

    /**
     * 账号与安全
     */
    void accountSafe();

    /**
     * 退出到登录界面 type:退出登录或关闭
     */
    void exitToLogin(int type);

    /**
     * 选择多语言
     */
    void choiceLanguage();
}
