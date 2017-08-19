package com.xdja.contact.presenter.command;

import com.xdja.contact.bean.Friend;
import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/7/20.
 */
public interface IFriendListCommand extends Command {

    void loadFriendData();

    void startDetailFriend(Friend friend);

    void updateFriendList();

    void updateFriendRequestList();
}
