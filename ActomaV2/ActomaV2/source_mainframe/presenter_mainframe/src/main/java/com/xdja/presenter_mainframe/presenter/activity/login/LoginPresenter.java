package com.xdja.presenter_mainframe.presenter.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.cust.CustInfo;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.LoginCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.LoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuLogin;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import rx.Subscriber;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class LoginPresenter extends PresenterActivity<LoginCommand, VuLogin> implements LoginCommand {

    @Inject
    Map<String, Provider<String>> stringProviderMap;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.QUERY_ACCOUNT_AT_LOCAL)
    Lazy<Ext0Interactor<Account>> queryAccountAtLocalUseCase;
    private String account;

    /*[S]add by tangsha@20160707 for ckms*/
    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_CREATE_SEC)
    Lazy<Ext1Interactor<String,MultiResult<Object>>> ckmsCreateUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;

    //[S] add by licong for safeLock
    /*@Inject
    @InteractorSpe(DomainConfig.GET_SAFELOCK_CLOUD_SETTINGS)
    Lazy<Ext1Interactor<String,MultiResult<Object>>> getSafeLockSettings;*/
    //[E] add by licong for safeLock

    private String TAG = "anTongCkms LoginPresenter";
    private String password;
    /*[E]add by tangsha@20160707 for ckms*/


    @Inject
    Intent intent;

    @NonNull
    @Override
    protected Class<? extends VuLogin> getVuClass() {
        return LoginView.class;
    }

    @NonNull
    @Override
    protected LoginCommand getCommand() {
        return this;
    }

    private Account mPreAccount;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        LogUtil.getUtils().e("LoginPresenter onBindView");
        super.onBindView(savedInstanceState);
        //[S]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha
        if(((ActomaApplication)ActomaApplication.getInstance()).getCkmsInitOk() == false){
            LogUtil.getUtils().e("LoginPresenter onBindView ckms init false,enter LauncherPresenter！");
            Navigator.navigateToLauncher();
            finish();
            return;
        }
        //[E]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha
        this.getActivityPreUseCaseComponent().inject(this);
        String account = getIntent().getStringExtra(Navigator.ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            getVu().setAccount(account);
        }
        //界面会显示上次登录的信息
        if (stringProviderMap != null) {
            Provider<String> stringProvider = stringProviderMap.get(CacheModule.KEY_PRE_LOGIN_DATA);
            if (stringProvider != null && !TextUtils.isEmpty(stringProvider.get())) {
                getVu().setAccount(stringProvider.get());
            }
        }

        queryAccountAtLocal();

        //如果是退出登录进入到的登录界面，要把堆栈中的其他activity清空
        boolean isExit = intent.getBooleanExtra(Navigator.EXIT, false);
        if (isExit) {
            ActivityStack.getInstanse().pop2TopActivity(true);
        }

    }

    private void queryAvatarAtAccount() {
        if (mPreAccount == null) {
            LogUtil.getUtils().e("LoginPresenter  getVu().setAvatarId(null) 0");
            getVu().setAvatarId(null);
            return;
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-18 add. complete task 1634 . review by wangchao1. Start

        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3681 . review by wangchao1. Start
        String accountString = TextUtils.isEmpty(mPreAccount.getAlias()) ? mPreAccount.getAccount() : mPreAccount
                .getAlias();
        LogUtil.getUtils().e("LoginPresenter queryAvatarAtAccount accountString = " + accountString);
        if (!TextUtils.isEmpty(mPreAccount.getAvatarId()) && !TextUtils.isEmpty(accountString) && TextUtils.equals(accountString, getVu().getInputAccount
                ()) || mPreAccount.getMobiles() != null && !mPreAccount.getMobiles().isEmpty() && TextUtils.equals
                (mPreAccount.getMobiles().get(0), getVu().getInputAccount())) {
            LogUtil.getUtils().i("LoginPresenter mPreAccount.getAvatarId() = " + mPreAccount.getAvatarId());
            //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3681 . review by wangchao1.
            // End
            getVu().setAvatarId(mPreAccount.getAvatarId());
        } else {
            LogUtil.getUtils().e("LoginPresenter  getVu().setAvatarId(null) 1");
            getVu().setAvatarId(null);
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-18 add. complete task 1634 . review by wangchao1. End
    }

    private void queryAccountAtLocal(){
        queryAccountAtLocalUseCase.get().fill()
                .execute(new Subscriber<Account>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onNext(Account account) {
                        LogUtil.getUtils().i("LoginPresenter queryAccountAtLocal onNext");
                        if (account == null) {
                            getVu().setAvatarId(null);
                            return;
                        }
                        LogUtil.getUtils().i("LoginPresenter queryAccountAtLocal account = " + account.getAccount() +
                                " , mobile = " + account.getMobiles() + " , alias = " + account.getAlias());
                        mPreAccount = account;
                        queryAvatarAtAccount();
                    }
                });
    }

    private static final int REQ_PHONE_STATE_PERMISSION = 10;

    @Override
    public void login(final String account, final String password) {
        if (!TextUtil.isRulePassword(password)) {
            inputInfoError();
            return;
        }

        this.account = account;
        this.password = password;

        if (isMNC() && CustInfo.isTelcom()) {
            ArrayList<String> permission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE, "");
            if (permission != null && !permission.isEmpty()) {
                ActivityCompat.requestPermissions(this, permission.toArray(new String[]{}), REQ_PHONE_STATE_PERMISSION);
                return;
            }
        }
        /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
        execLoginUseCase();
        /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. End*/
    }


    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (isMNC()) {
            if (requestCode == REQ_PHONE_STATE_PERMISSION) {
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                   /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
                    execLoginUseCase();
         /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. End*/
                } else {
                    final CustomDialog customDialog = new CustomDialog(this);
                    customDialog.setTitle(getString(R.string.none_read_phone_permission)).setMessage(getString(R.string
                            .none_read_phone_permission_hint)).setNegativeButton(getString(com.xdja.imp.R.string.confirm)
                            , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                        }
                    }).show();
                }
            }
        }
    }

    /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
    @Inject
    LogoutHelper logoutHelper;
    /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
    private void execLoginUseCase(){
        executeInteractorNoRepeat(accountPwdLoginUseCase.get().fill(account, password),
                new LoadingDialogSubscriber<MultiResult<Object>>(this, this, true , 1) {
                    @SuppressWarnings("EmptyMethod")
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        objectMultiResult.getInfo().put(Navigator.ACCOUNT, account);
                        objectMultiResult.getInfo().put(Navigator.PASSWORD, password);
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-17 add. fix bug 3023 . review by wangchao1. Start
                        if (objectMultiResult.getResultStatus() == LoginHelper.RESULT_STATUS_MAX_LOGIN) {
                            setDismissDialogWhenCompleleted(true);
                        }
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-17 add. fix bug 3023 . review by wangchao1. End

                        /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
                        LoginHelper.execCkmsCreateUseCase(LoginPresenter.this,
                                ckmsCreateUseCase,ckmsForceAddUseCase,objectMultiResult,logoutHelper,
                                1);

                        /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. End*/
                    }
                }.registerLoadingMsg(getString(R.string.login))
                        .registUserMsg(ServerException.class, ServerException.ACCOUNT_NOT_EXISTS, "")
                        .registUserMsg(ServerException.class, ServerException.ACCOUNT_OR_PWD_ERROR, "")
        );
    }
    /*[E]add by tangsha@20160707 for ckms*/


    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        //modify by alh@xdja.com to fix bug: 527 2016-06-27 start (rummager : wangchao1)
        if (okCode.equals(ServerException.ACCOUNT_NOT_EXISTS) || okCode.equals(ServerException.ACCOUNT_OR_PWD_ERROR)) {
            inputInfoError();
            return false;
        }
        //modify by alh@xdja.com to fix bug: 527 2016-06-27 end (rummager : wangchao1)
        return true;
    }

    private void inputInfoError() {
        if (TextUtil.isRulePhoneNumber(account))
            getVu().showToast(getString(R.string.phone_or_pwd_error));
        else {
            getVu().showToast(getString(R.string.account_or_pwd_error));
        }
        getVu().clearPassword();
    }

    @Override
    public void messageVerifyLogin() {
        Navigator.navigateToMessageVerifyLogin();
    }

    @Override
    public void forgetPassword() {
        Navigator.navigateToResetPassword();
    }

    @Override
    public void register() {
        Navigator.navigateToWriteRegistrationInfo();
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-07 add. fix 3743 . review by wangchao1. Start
    @Override
    public void afterAccountChanged(boolean isEmpty) {
        if (isEmpty) {
            getVu().setAvatarId(null);
            return;
        }
        queryAvatarAtAccount();
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-07 add. fix 3743 . review by wangchao1. End

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().onDestroy();
    }
}
