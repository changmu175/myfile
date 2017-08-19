package com.xdja.contact.presenter.command;

import com.xdja.contact.bean.Friend;
import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/7/23.
 */
public interface IFriendRemarkCommand extends Command {

    Friend getFriend();

    void saveRemark();

}
