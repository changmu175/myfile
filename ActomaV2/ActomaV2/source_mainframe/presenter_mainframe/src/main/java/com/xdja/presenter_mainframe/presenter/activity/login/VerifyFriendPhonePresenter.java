package com.xdja.presenter_mainframe.presenter.activity.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.deviceauth.CkmsForceAddDeviceUseCase;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyFriendPhoneCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.VerifyFriendPhoneView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyFriendPhone;
import com.xdja.presenter_mainframe.util.ErrorUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class VerifyFriendPhonePresenter extends PresenterActivity<VerifyFriendPhoneCommand, VuVerifyFriendPhone> implements VerifyFriendPhoneCommand {
    private String account;
    //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String digitalAccount;
    //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String innerAuthCode;
    private String password;
    private String phoneNumber;
    private String loginVerifyCode;
    private String loginInnerAuthCode;

    @Inject
    @InteractorSpe(value = DomainConfig.CHECK_FRIEND_MOBILES)
    Lazy<Ext3Interactor<String, String, List<String>, MultiResult<Object>>> checkFriendMobilesUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;

    /*[S]add by tangsha@20160712 for ckms force add devices*/
    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;
    /*[E]add by tangsha@20160712 for ckms force add devices*/

    @Inject
    @InteractorSpe(value = DomainConfig.LOGIN_MOBILE)
    Lazy<Ext3Interactor<String, String, String, MultiResult<Object>>> mobileLoginUseCase;

    @NonNull
    @Override
    protected Class<? extends VuVerifyFriendPhone> getVuClass() {
        return VerifyFriendPhoneView.class;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();

    }

    private void setDataFromIntent() {
        account = getIntent().getStringExtra(Navigator.ACCOUNT);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        digitalAccount = getIntent().getStringExtra(Navigator.DIGITAL_ACCOUNT);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        innerAuthCode = getIntent().getStringExtra(Navigator.INNER_AUTH_CODE);
        password = getIntent().getStringExtra(Navigator.PASSWORD);
        //[S]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
        phoneNumber = getIntent().getStringExtra(Navigator.PHONE_NUMBER);
        loginVerifyCode = getIntent().getStringExtra(Navigator.VERIFY_CODE);
        loginInnerAuthCode = innerAuthCode;
        if (account == null){
            account = phoneNumber;
        }
        //[E]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
    }

    @NonNull
    @Override
    protected VerifyFriendPhoneCommand getCommand() {
        return this;
    }

    @Override
    public void verify(final List<String> phoneNumbers) {
         /*[S]add by tangsha@20160712 for ckms force add devices*/
        executeInteractorNoRepeat(ckmsForceAddUseCase.get().fill(digitalAccount),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this){
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        if(result.getResultStatus() == CkmsForceAddDeviceUseCase.FORCE_ADD_DEV_OK) {
                            execCheckFriendUseCase(phoneNumbers);
                        }else{
                            getVu().showToast(RegisterVerifyPhoneNumberPresenter.AUTH_CODE_ERROR);
                        }
                    }
                }.registerLoadingMsg(getString(R.string.verify)));
        /*[E]add by tangsha@20160712 for ckms force add devices*/

    }

    /**
     * 成功
     */
    private static final int RESULT_STATUS_SUCCESS = 0;
    /**
     * 验证失败次数达到上线
     */
    private static final int RESULT_STATUS_LIMIT_TIMES = 3;
    public static final String SURPLUS_TIMES = "surplusTimes";
    public static final String TIMES = "times";

    private void execCheckFriendUseCase(List<String> phoneNumbers){
        executeInteractorNoRepeat(checkFriendMobilesUseCase.get().fill(account, innerAuthCode, phoneNumbers),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this) {
                    @SuppressWarnings("NumericCastThatLosesPrecision")
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        //[S]add by tangsha@20160825 for 2432&3355, review by self
                        int resultCode = result.getResultStatus();
                        Log.d("CkmsVerifyFriendPhoneP","checkFriendMobilesUseCase resultCode "+resultCode);
                        if(resultCode == RESULT_STATUS_SUCCESS) {
                            //[S]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
                            if (TextUtils.isEmpty(password)) {
                                LoginHelper.MobileVerifyAutoLogin(VerifyFriendPhonePresenter.this, mobileLoginUseCase, phoneNumber, loginVerifyCode, loginInnerAuthCode);
                                return;
                            }//[E]modify by xienana for bug 3673 @2016/09/06 [reviewed by tangasha]
                            LoginHelper.accountPwdAutoLogin(VerifyFriendPhonePresenter.this, accountPwdLoginUseCase, account, password);
                        }else if(resultCode == RESULT_STATUS_LIMIT_TIMES){
                            int surplusTimes = (int) (double) result.getInfo().get(SURPLUS_TIMES);
                            int times = (int) (double) result.getInfo().get(TIMES);
                            if (surplusTimes > 0) {
                                getVu().showAuthFailDialog(String.format(getString(R.string.verify_fail_times),surplusTimes));
                            } else {
                                getVu().showAuthFailDialog(String.format(getString(R.string.verify_fail_over),times));
                            }
                        }
                        //[E]add by tangsha@20160825 for 2432&3355, review by self
                    }
                }
                      //  .registUserMsg(ServerException.class, ServerException.MOBILE_AND_ACCOUNT_NOT_FRIEND, "未验证通过，请检查手机号是否正确填写")
                        .registerLoadingMsg(getString(R.string.verify)));
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        ErrorUtil.loginPop2First(okCode);
        return true;
    }
}
