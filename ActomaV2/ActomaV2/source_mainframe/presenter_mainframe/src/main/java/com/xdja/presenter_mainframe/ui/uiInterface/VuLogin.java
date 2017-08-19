package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.presenter_mainframe.cmd.LoginCommand;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuLogin extends VuLoginResult<LoginCommand> {
    void clearPassword();
    void setAccount(String account);
    void setAvatarId(String avatarId);
    String getInputAccount();
}
