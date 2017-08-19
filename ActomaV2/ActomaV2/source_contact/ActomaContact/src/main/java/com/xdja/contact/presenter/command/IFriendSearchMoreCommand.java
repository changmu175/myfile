package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/10/23.
 */
public interface IFriendSearchMoreCommand extends Command {

    void startSearch(String keyword);
}
