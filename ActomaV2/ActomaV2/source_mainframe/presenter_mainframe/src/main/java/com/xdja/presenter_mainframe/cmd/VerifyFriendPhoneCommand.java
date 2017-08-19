package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

import java.util.List;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VerifyFriendPhoneCommand extends Command {
    void verify(List<String> phoneNumbers);
}
