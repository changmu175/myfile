package com.xdja.presenter_mainframe.presenter.activity.resetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.cmd.ResetPwdInputNewPasswordCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.InputNewPasswordView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuInputNewPassword;
import com.xdja.presenter_mainframe.util.ErrorUtil;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ResetPwdInputNewPasswordPresenter extends PresenterActivity<FillMessageCommand, VuInputNewPassword> implements ResetPwdInputNewPasswordCommand {
    private int resetPasswdType = Navigator.RESET_PASSWORD_TYPE_ERROR;
    private String myMobile;
    private String innerAuthCode;
    private String account;

    @Inject
    @InteractorSpe(value = DomainConfig.RESTPWD_BYAUTHCODE)
    Lazy<Ext3Interactor<String, String, String, Void>> resetPwdByAuthCodeUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.RESTPWD_BYFRIENDMOBILES)
    Lazy<Ext3Interactor<String, String, String, Void>> resetPwdByFriendMobilesUseCase;

    @NonNull
    @Override
    protected Class<? extends VuInputNewPassword> getVuClass() {
        return InputNewPasswordView.class;
    }

    @NonNull
    @Override
    protected ResetPwdInputNewPasswordCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();
    }

    private void setDataFromIntent() {
        Intent intent = getIntent();
        resetPasswdType = intent.getIntExtra(Navigator.RESET_PASSWD_TYPE, Navigator.RESET_PASSWORD_TYPE_ERROR);
        switch (resetPasswdType) {
            case Navigator.RESET_PASSWORD_TYPE_AUTH_CODE:
                myMobile = intent.getStringExtra(Navigator.MOBILE);
                innerAuthCode = intent.getStringExtra(Navigator.INNER_AUTH_CODE);
                break;
            case Navigator.RESET_PASSWORD_TYPE_FRIEND_PHONE:
                account = intent.getStringExtra(Navigator.ACCOUNT);
                innerAuthCode = intent.getStringExtra(Navigator.INNER_AUTH_CODE);
                break;
        }
    }



    @Override
    public void complete(List<String> stringList) {
        String password = stringList.get(0);
        String passwordAgain = null;
        if (stringList.size() > 1) {
            passwordAgain = stringList.get(1);
            if (!password.equals(passwordAgain)) {
                getVu().showToast(getString(R.string.different_two_password));
                return;
            }
        }
        if (!TextUtil.isRulePassword(password)) {
            getVu().showToast(getString(R.string.password_format));
        } else {
            switch (resetPasswdType) {
                case Navigator.RESET_PASSWORD_TYPE_AUTH_CODE:
                    resetPasswdByAuthCode(password);
                    break;
                case Navigator.RESET_PASSWORD_TYPE_FRIEND_PHONE:
                    resetPasswdByFriendPhone(password);
                    break;

            }
        }
    }

    private void resetPasswdByAuthCode(String password) {
        if (TextUtils.isEmpty(innerAuthCode)){
            getVu().showToast(getString(R.string.get_verify_code_prompt));
            return;
        }
        executeInteractorNoRepeat(resetPwdByAuthCodeUseCase.get().fill(myMobile, innerAuthCode, password),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        setPasswordSuccess();
                    }
                }.registerLoadingMsg(getString(R.string.submit)));
    }


    private void resetPasswdByFriendPhone(String password) {
        executeInteractorNoRepeat(resetPwdByFriendMobilesUseCase.get().fill(account, innerAuthCode, password),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        setPasswordSuccess();
                    }
                }.registerLoadingMsg(getString(R.string.submit)));
    }


    private void setPasswordSuccess(){
        getVu().showToast(getString(R.string.set_succeed));
        Navigator.navigateToLoginByFinish();
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode==null){
            return true;
        }
        if (okCode.equals(ServerException.ACCOUNT_NOT_EXISTS)){
            getVu().showToast(getString(R.string.unknow_reason_404));
        }
        if (okCode.equals(ServerException.MOBILE_NOT_ACCORDANCE)){
            getVu().showToast(getString(R.string.unknow_reason_406));
        }
        ErrorUtil.resetPasswordPop2First(okCode);
        return true;
    }

    @Override
    public boolean isHasMobile() {
        return getIntent()!= null && !TextUtils.isEmpty(getIntent().getStringExtra(Navigator.MOBILE));
    }
}
