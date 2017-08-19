package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;

/**
 * Created by ldy on 16/5/13.
 */
public interface VuVerifyLoginVerifyPhoneNumber extends VuVerifyPhoneNumber,VuLoginResult<VerifyPhoneNumberCommand> {
    void setPhoneNumber(String phoneNumber);
}
