package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.ResetPasswordVerifyFriendPhoneCommand;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuResetPasswordVerifyFriendPhone extends ActivityVu<ResetPasswordVerifyFriendPhoneCommand> {
    void showAuthFailDialog(String message);
}
