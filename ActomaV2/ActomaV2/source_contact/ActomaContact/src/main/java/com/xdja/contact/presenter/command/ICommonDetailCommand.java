package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/10/23.
 */
public interface ICommonDetailCommand extends Command {

    void startRequestInfo();

    void sendEncryptionMessage();

    void callWithEncryption();

    //添加好友
    void toolBarAddFriend();
    //编辑
    void editRemark();
    //删除好友
    void deleteFriend();

    void callPhone(String phone);

    void searchFromWeb(String account);

    boolean isFirstComeIn();

    String getAccount();
}
