package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuVerifyPhoneNumber extends ActivityVu<VerifyPhoneNumberCommand> {
    String getPhoneInputText();
    String getVerifyCodeInputText();


    /**
     * 重置获取验证码按钮的状态
     */
    void resetVerifyCode();
}
