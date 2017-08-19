package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.VerifyFriendPhoneCommand;

/**
 * Created by ldy on 16/4/18.
 */
public interface VuVerifyFriendPhone extends ActivityVu<VerifyFriendPhoneCommand>,VuLoginResult<VerifyFriendPhoneCommand> {
    void showAuthFailDialog(String message);
}
