package com.xdja.contact.presenter.command;


import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by wanghao on 2015/7/23.
 */
public interface ILocalSearchCommand extends Command {

    void startSearch(String keyoword);

}
