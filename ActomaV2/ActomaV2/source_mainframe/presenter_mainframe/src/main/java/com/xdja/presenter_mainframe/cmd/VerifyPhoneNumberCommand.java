package com.xdja.presenter_mainframe.cmd;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VerifyPhoneNumberCommand extends FillMessageCommand {
    void getVerifyCode(String phoneNumber);
}
