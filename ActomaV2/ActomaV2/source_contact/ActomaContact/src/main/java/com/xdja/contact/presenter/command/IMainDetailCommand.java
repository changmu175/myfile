package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by yangpeng on 2015/7/18.
 */
public interface IMainDetailCommand extends Command {

    /**
     * 发送加密消息
     */
    void sendEncriptionMessage();

    /**
     * 打加密电话
     */
    void callWithEncription();

    /**
     * 快速开启第三方加密服务
     */
    void openEncriptionServe();

    /**
     * 拨打普通电话
     * @param phoneNumber 电话号
     */
    void call(String phoneNumber);

    void addFriend();

    int getType();

    void startRequestInfo();

    void startEditRemark();

    void deleteFriend();
}
