package com.xdja.presenter_mainframe.presenter.activity.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.deviceauth.CkmsForceAddDeviceUseCase;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.VerifyPhoneNumberView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyLoginVerifyPhoneNumber;
import com.xdja.presenter_mainframe.util.ErrorUtil;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by ldy on 16/4/13.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class VerifyPhoneNumberPresenter extends PresenterActivity<VerifyPhoneNumberCommand, VuVerifyLoginVerifyPhoneNumber> implements VerifyPhoneNumberCommand {
    private String account;
    //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String digitalAccount;
    //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private static String innerAuthCode;
    private String password;

    @Inject
    @InteractorSpe(value = DomainConfig.CHECK_MOBILE)
    Lazy<Ext4Interactor<String, String, String, String, Void>> checkMobileUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.OBTAION_DEVICE_AUTHRIZE_AUTHCODE)
    Lazy<Ext2Interactor<String, String, Map<String, String>>> obtainDeviceAuthrizeAuthcodeUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;
    private String phoneNumber;

    /*[S]add by tangsha@20160712 for ckms force add devices*/
    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;
    /*[E]add by tangsha@20160712 for ckms force add devices*/

    @NonNull
    @Override
    protected Class<? extends VuVerifyLoginVerifyPhoneNumber> getVuClass() {
        return VerifyPhoneNumberView.class;
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
        setDataFromIntent();
        getVu().setPhoneNumber(phoneNumber);
    }

    private void setDataFromIntent() {
        account = getIntent().getStringExtra(Navigator.ACCOUNT);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        digitalAccount = getIntent().getStringExtra(Navigator.DIGITAL_ACCOUNT);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        innerAuthCode = getIntent().getStringExtra(Navigator.INNER_AUTH_CODE);
        phoneNumber = getIntent().getStringExtra(Navigator.PHONE_NUMBER);
        password = getIntent().getStringExtra(Navigator.PASSWORD);
        loginVerifyCode = getIntent().getStringExtra(Navigator.VERIFY_CODE);
        loginInnerAuthCode = innerAuthCode;
        if (account == null){
            account = phoneNumber;
        }
    }

    @Override
    public void getVerifyCode(String phoneNumber) {
        addInteractor2Queue(obtainDeviceAuthrizeAuthcodeUseCase.get()).fill(account, phoneNumber)
                .execute(new PerSubscriber<Map<String, String>>(this) {
                            @Override
                            public void onNext(Map<String, String> stringStringMap) {
                                super.onNext(stringStringMap);
                                innerAuthCode = stringStringMap.get(Navigator.INNER_AUTH_CODE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                getVu().resetVerifyCode();

                            }
                        }
                                .registUserMsg(ServerException.class, ServerException.MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error))
                                .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, getString(R.string.phone_or_verifycode_error))
                                .registUserMsg(ServerException.class, ServerException.ACCOUNT_NOT_EXISTS, getString(R.string.unknow_reason_404))
                                .registUserMsg(ServerException.class, ServerException.ACCOUNT_MOBILE_NOT_BIND, getString(R.string.unknow_reason_406))

                );
    }
    String verifyCode;
    String loginVerifyCode;
    String loginInnerAuthCode;
    @Override
    public void complete(List<String> stringList) {
        if (innerAuthCode==null){
            getVu().showToast(RegisterVerifyPhoneNumberPresenter.AUTH_CODE_ERROR);
            return;
        }
        verifyCode = stringList.get(0);
        /*[S]add by tangsha@20160712 for ckms force add devices*/
        executeInteractorNoRepeat(ckmsForceAddUseCase.get().fill(digitalAccount),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this){
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        int status = result.getResultStatus();
                        if(status == CkmsForceAddDeviceUseCase.FORCE_ADD_DEV_OK) {
                            execCheckMobileUseCase();
                        }else{
                            getVu().showToast(getString(R.string.ckms_init_fail));
                        }
                    }

                }.registerLoadingMsg(getString(R.string.verify)));
        /*[E]add by tangsha@20160712 for ckms force add devices*/
    }

    @Inject
    @InteractorSpe(value = DomainConfig.LOGIN_MOBILE)
    Lazy<Ext3Interactor<String, String, String, MultiResult<Object>>> mobileLoginUseCase;

    private void execCheckMobileUseCase() {
        executeInteractorNoRepeat(checkMobileUseCase.get().fill(account, phoneNumber, verifyCode, innerAuthCode), new
                LoadingDialogSubscriber<Void>(this, this) {
            @Override
            public void onNext(Void aVoid) {
                super.onNext(aVoid);
                //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3660 . review by wangchao1. Start
                if (TextUtils.isEmpty(password)) {
                    LoginHelper.MobileVerifyAutoLogin(VerifyPhoneNumberPresenter.this, mobileLoginUseCase,
                            phoneNumber, loginVerifyCode, loginInnerAuthCode);
                    return;
                }
                //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3660 . review by wangchao1. End

                LoginHelper.accountPwdAutoLogin(VerifyPhoneNumberPresenter.this, accountPwdLoginUseCase, account,
                        password);
            }
        }.registerLoadingMsg(getString(R.string.verify)).registUserMsg(ServerException.class, ServerException
                .MOBILE_NOT_ACCORDANCE, getString(R.string.auth_code_error)));
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex,
                                     @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        ErrorUtil.loginPop2First(okCode);
        return true;
    }
}
