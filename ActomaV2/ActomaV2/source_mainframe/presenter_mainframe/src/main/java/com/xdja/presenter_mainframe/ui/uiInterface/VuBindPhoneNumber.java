package com.xdja.presenter_mainframe.ui.uiInterface;

/**
 * Created by ldy on 16/5/10.
 */
public interface VuBindPhoneNumber extends VuVerifyPhoneNumber{
    /**
     * 强制绑定手机号提示框
     */
    void showChooseDialog();

    /**
     * 相同手机号更换提示框
     */
    void showSameMobileDialog();
}
