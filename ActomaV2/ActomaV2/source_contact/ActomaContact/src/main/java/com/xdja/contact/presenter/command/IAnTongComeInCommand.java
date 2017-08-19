package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by yangpeng on 2015/8/10.
 */
public interface IAnTongComeInCommand extends Command {

    void startAtChat();

    void callPhone(String phone);
}
