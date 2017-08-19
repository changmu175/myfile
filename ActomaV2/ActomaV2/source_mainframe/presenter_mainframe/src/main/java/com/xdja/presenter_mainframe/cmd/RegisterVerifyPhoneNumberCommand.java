package com.xdja.presenter_mainframe.cmd;

/**
 * Created by ldy on 16/4/26.
 */
public interface RegisterVerifyPhoneNumberCommand extends VerifyPhoneNumberCommand{
    void forceBind();
    void skip();
    boolean isNoBackKey();
}
