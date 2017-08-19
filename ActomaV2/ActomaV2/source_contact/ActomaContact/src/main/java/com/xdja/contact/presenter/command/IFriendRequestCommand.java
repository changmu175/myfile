package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/7/21.
 */
public interface IFriendRequestCommand extends Command {

    void  showFriendDetail(int position);

    void startSearch();

    void showErWeiMa();

    void backWithResult();
}
