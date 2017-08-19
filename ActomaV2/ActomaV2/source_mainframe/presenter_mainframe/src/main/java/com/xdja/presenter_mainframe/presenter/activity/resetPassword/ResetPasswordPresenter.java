package com.xdja.presenter_mainframe.presenter.activity.resetPassword;

import android.support.annotation.NonNull;

import com.xdja.presenter_mainframe.cmd.VerifyLoginCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ResetPasswordView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuResetPassword;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ResetPasswordPresenter extends
        PresenterActivity<VerifyLoginCommand, VuResetPassword> implements VerifyLoginCommand {

    @NonNull
    @Override
    protected Class<? extends VuResetPassword> getVuClass() {
        return ResetPasswordView.class;
    }

    @NonNull
    @Override
    protected VerifyLoginCommand getCommand() {
        return this;
    }

    @Override
    public void phoneVerifyCodeVerify() {
        Navigator.navigateToResetPasswordVerifyPhone();
    }

    @Override
    public void friendPhoneVerify() {
        Navigator.navigateToResetPasswordVerifyFriendPhone();
    }
}
