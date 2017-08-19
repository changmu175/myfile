package com.xdja.presenter_mainframe.ui;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyLoginCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuResetPassword;

import butterknife.OnClick;

/**
 * Created by ldy on 16/4/18.
 */
@ContentView(R.layout.activity_reset_password)
public class ResetPasswordView extends ActivityView<VerifyLoginCommand> implements VuResetPassword {

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @OnClick(R.id.btn_reset_password_verify_code)
    void verifyCode(){
        getCommand().phoneVerifyCodeVerify();
    }
    @OnClick(R.id.btn_reset_password_friend_phone)
    void friendPhone(){
        getCommand().friendPhoneVerify();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_reset_password);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
