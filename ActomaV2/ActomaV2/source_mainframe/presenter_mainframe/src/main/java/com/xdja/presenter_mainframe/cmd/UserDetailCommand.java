package com.xdja.presenter_mainframe.cmd;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by geyao on 2015/7/7.
 */
public interface UserDetailCommand extends Command {
    /**
     * 修改头像
     */
    void openUpdateUserImg();

    /**
     * 打开修改昵称页面
     */
    void openUpdateNickPage();

    /**
     * 显示二维码大图
     */
    void showBigQrImg();

    /**
     * 修改手机号
     */
    void modifyMobile();
    /**
     * 设置安通+帐号
     */
    void setActomaAccount();
}
