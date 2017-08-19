package com.xdja.presenter_mainframe.presenter.activity.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.presenter_mainframe.cmd.VerifyLoginCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.VerifyLoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyLogin;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class VerifyLoginPresenter extends
        PresenterActivity<VerifyLoginCommand, VuVerifyLogin> implements VerifyLoginCommand {
    private String account;
    //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String digitalAccount;
    //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String verifyCode;
    private String innerAuthCode;
    private String phoneNumber;
    private String password;

    @NonNull
    @Override
    protected Class<? extends VuVerifyLogin> getVuClass() {
        return VerifyLoginView.class;
    }

    @NonNull
    @Override
    protected VerifyLoginCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();
        getVu().isHaveMobile(!TextUtils.isEmpty(phoneNumber));
    }

    private void setDataFromIntent() {
        account = getIntent().getStringExtra(Navigator.ACCOUNT);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        digitalAccount = getIntent().getStringExtra(Navigator.DIGITAL_ACCOUNT);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        innerAuthCode = getIntent().getStringExtra(Navigator.INNER_AUTH_CODE);
        phoneNumber = getIntent().getStringExtra(Navigator.PHONE_NUMBER);
        password = getIntent().getStringExtra(Navigator.PASSWORD);
        verifyCode = getIntent().getStringExtra(Navigator.VERIFY_CODE);
    }

    @Override
    public void phoneVerifyCodeVerify() {
        Navigator.navigateToVerifyPhoneNumber(account, digitalAccount, verifyCode ,innerAuthCode, phoneNumber,password);
    }

    //[S]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
    @Override
    public void friendPhoneVerify() {
        Navigator.navigateToVerifyFriendPhone(account, digitalAccount,verifyCode,innerAuthCode,phoneNumber,password);
    }//[E]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
}
