package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;

/**
 * Created by ldy on 16/4/26.
 */
public interface VuRegistVerifyPhoneNumber extends VuVerifyPhoneNumber,VuLoginResult<VerifyPhoneNumberCommand>{
    void showChooseDialog();
}
