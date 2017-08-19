package com.xdja.presenter_mainframe.presenter.activity.resetPassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ResetPasswordVerifyPhoneView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyPhoneNumber;
import com.xdja.presenter_mainframe.util.ErrorUtil;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by ldy on 16/4/18.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ResetPasswordVerifyPhonePresenter extends PresenterActivity<VerifyPhoneNumberCommand, VuVerifyPhoneNumber> implements VerifyPhoneNumberCommand {
    private static String innerAuthCode;

    @Inject
    @InteractorSpe(value = DomainConfig.RESET_AUTHCODE_OBTAIN)
    Lazy<Ext1Interactor<String, String>> resetPasswordAuthCodeObtainUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.CHECK_RESTPWD_AUTHCODE)
    Lazy<Ext3Interactor<String, String, String, Map<String, String>>> checkResetPwdAuthCode;

    @NonNull
    @Override
    protected Class<? extends VuVerifyPhoneNumber> getVuClass() {
        return ResetPasswordVerifyPhoneView.class;
    }

    @NonNull
    @Override
    protected VerifyPhoneNumberCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);

    }

    @Override
    public void getVerifyCode(String phoneNumber) {
        addInteractor2Queue(resetPasswordAuthCodeObtainUseCase.get()).fill(phoneNumber)
                .execute(new PerSubscriber<String>(this) {
                    @Override
                    public void onNext(String s) {
                        super.onNext(s);
                        innerAuthCode = s;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().resetVerifyCode();
                    }
                });
    }

    @Override
    public void complete(final List<String> stringList) {
        final String phoneNumber = stringList.get(0);
        String verifyCode = stringList.get(1);
        if (innerAuthCode == null) {
            getVu().showToast(RegisterVerifyPhoneNumberPresenter.AUTH_CODE_ERROR);
            return;
        }
        executeInteractorNoRepeat(checkResetPwdAuthCode.get().fill(phoneNumber, verifyCode, innerAuthCode), new
                LoadingDialogSubscriber<Map<String, String>>(this, this) {
            @Override
            public void onNext(Map<String, String> stringStringMap) {
                super.onNext(stringStringMap);
                innerAuthCode = stringStringMap.get(Navigator.INNER_AUTH_CODE);
                Navigator.navigateToResetPwdInputNewPasswordByMyPhone(phoneNumber, innerAuthCode);
            }
        }.registerLoadingMsg(getString(R.string.verify)));
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null){
            return true;
        }
        if (okCode.equals(ServerException.MOBILE_NOT_ACCORDANCE)||okCode.equals(ServerException.AUTH_CODE_ERROR)){
            getVu().showToast(getString(R.string.phone_or_verifycode_error));
        }
        ErrorUtil.resetPasswordPop2First(okCode);
        return true;
    }
}
