package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public interface IFriendDetailCommand extends Command {

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

    void startEditRemark();

    void deleteFriend();

}
