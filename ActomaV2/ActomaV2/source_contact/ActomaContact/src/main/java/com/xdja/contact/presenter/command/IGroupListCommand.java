package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by XDJA_XA on 2015/7/17.
 */
public interface IGroupListCommand extends Command {
    /**
     * 进入群聊天界面
     */
    void startChatActivity(int position);

    //void updateGroupIncrement();
}
