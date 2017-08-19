package com.xdja.presenter_mainframe.presenter.activity.register;

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
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.RegisterVerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.LoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.RegisterVerifyPhoneNumberView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuRegistVerifyPhoneNumber;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by ldy on 16/4/13.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class RegisterVerifyPhoneNumberPresenter extends PresenterActivity<RegisterVerifyPhoneNumberCommand, VuRegistVerifyPhoneNumber>
        implements RegisterVerifyPhoneNumberCommand {
    /**
     * 成功
     */
    private final static int RESULT_STATUS_SUCCESS = 0;
    /**
     * 手机号已注册
     */
    private final static int RESULT_STATUS_REGISTERED = 1;
    public static final String AUTH_CODE_ERROR = ActomaController.getApp().getString(R.string.verify_code_error);

    private String account;
    /**
     * 由于内部验证码只能在获取验证码时拿到，则用户退出界面再进来时，如果内部验证码不是静态则会导致其为空，导致注册失败
     */
    private static String innerAuthCode;
    private String phoneNumber;
    private String password;

    @Inject
    Intent intent;

    @Inject
    @InteractorSpe(value = DomainConfig.BINDMODBILE)
    Lazy<Ext4Interactor<String, String, String, String, MultiResult<String>>> bindMobileUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.FORCE_BINDMODBILE)
    Lazy<Ext3Interactor<String, String, String, Void>> forceBindMobileUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.BINDMODBILE_AUTHCODE_OBTAIN)
    Lazy<Ext2Interactor<String, String, String>> bindMobileAuthCodeObtain;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;

    /*[S]modify by tangsha@20160708 for ckms*/
    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_CREATE_SEC)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsCreateUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;
    private String TAG = "anTongCkms RegisterVerifyPhoneNumberPresenter";
    /*[E]modify by tangsha@20160708 for ckms*/

    @NonNull
    @Override
    protected Class<? extends VuRegistVerifyPhoneNumber> getVuClass() {
        return RegisterVerifyPhoneNumberView.class;
    }

    @NonNull
    @Override
    protected RegisterVerifyPhoneNumberCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();
    }

    private void setDataFromIntent() {
        this.account = this.intent.getStringExtra(Navigator.ACCOUNT);
        //无需上个页面传递的内部验证码
//        this.innerAuthCode = this.intent.getStringExtra(Navigator.INNER_AUTH_CODE);
        this.password = this.intent.getStringExtra(Navigator.PASSWORD);
    }


    private void registered(MultiResult<String> stringMultiResult) {
        this.innerAuthCode = stringMultiResult.getInfo().get(Navigator.INNER_AUTH_CODE);
        getVu().showChooseDialog();
    }

    @Override
    public void getVerifyCode(String phoneNumber) {
        addInteractor2Queue(bindMobileAuthCodeObtain.get()).fill(account, phoneNumber)
                .execute(new PerSubscriber<String>(this) {
                    @Override
                    public void onNext(String s) {
                        super.onNext(s);
                        if (!TextUtils.isEmpty(s)) {
                            RegisterVerifyPhoneNumberPresenter.this.innerAuthCode = s;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().resetVerifyCode();
                    }
                });
    }

    @Override
    public boolean isNoBackKey() {
        return getIntent().getBooleanExtra(Navigator.TOOLBAR_TYPE, false);
    }

    @Override
    public void forceBind() {
        executeInteractorNoRepeat(forceBindMobileUseCase.get().fill(account, innerAuthCode, phoneNumber), new
                LoadingDialogSubscriber<Void>(this, this, true) {
            @Override
            public void onNext(Void aVoid) {
                super.onNext(aVoid);
                login(this);
                //add by gbc for statistics skip bind mobile. 2016-11-30 begin
                SharePreferceUtil.getPreferceUtil(getApplicationContext()).setIsSkipBindMobile(false);
                //add by gbc for statistics skip bind mobile. 2016-11-30 end
            }
        }.registerLoadingMsg(getString(R.string.force_bind)).registUserMsg(ServerException.class, ServerException
                .MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error)));
    }

    @Override
    public void skip() {
        login(null);
        //绑定手机号码“跳过”,日志上报，wxf@xdja.com
        //add by gbc for statistics skip bind mobile. 2016-11-30 begin
        SharePreferceUtil.getPreferceUtil(getApplicationContext()).setIsSkipBindMobile(true);
        //add by gbc for statistics skip bind mobile. 2016-11-30 end
    }

    @Override
    public void complete(final List<String> stringList) {
        if (innerAuthCode == null) {
            getVu().showToast(AUTH_CODE_ERROR);
            return;
        }
        String phoneNumber = stringList.get(0);
        String verifyCode = stringList.get(1);
        this.phoneNumber = phoneNumber;
        setBtnState(false);
        executeInteractorNoRepeat(bindMobileUseCase.get().fill(account, verifyCode, innerAuthCode, phoneNumber),
                new LoadingDialogSubscriber<MultiResult<String>>(this, this, true) {
                    @Override
                    public void onNext(MultiResult<String> stringMultiResult) {
                        super.onNext(stringMultiResult);
                        switch (stringMultiResult.getResultStatus()) {
                            case RESULT_STATUS_SUCCESS:
                                login(this);
                                //add by gbc for statistics skip bind mobile. 2016-11-30 begin
                                SharePreferceUtil.getPreferceUtil(getApplicationContext()).setIsSkipBindMobile(false);
                                //add by gbc for statistics skip bind mobile. 2016-11-30 end
                                break;
                            case RESULT_STATUS_REGISTERED:
                                setDismissDialogWhenCompleleted(true);
                                registered(stringMultiResult);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setBtnState(true);
                    }
                }.registUserMsg(ServerException.class, ServerException.MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error))
                        .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, getString(R.string.phone_or_verifycode_error))
                        .registUserMsg(ServerException.class, ServerException.ACCOUNT_NOT_EXISTS, getString(R.string.unknow_reason_404))
                        .registUserMsg(ServerException.class, ServerException.ACCOUNT_MOBILE_NOT_BIND, getString(R.string.bind)));
    }

    //[Start]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-18 add. fix bug #2348 . review by wangchao1. Start
    public void setBtnState(boolean state) {
        if (getVu() instanceof RegisterVerifyPhoneNumberView) {
            ((RegisterVerifyPhoneNumberView) getVu()).setComBtnState(state);
        }
    }
    //[End]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-18 add. fix bug #2348 . review by wangchao1

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        if (okCode.equals(ServerException.ACCOUNT_NOT_EXISTS)) {
            getVu().showToast(getString(R.string.unknow_reason_404));
        }
        return true;
    }

    /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
    @Inject
    LogoutHelper logoutHelper;
    private static final int REQ_PHONE_STATE_PERMISSION = 12;

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (isMNC()) {
            if (requestCode == REQ_PHONE_STATE_PERMISSION) {
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    execMobileLoginUseCase();
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

    private void execMobileLoginUseCase(){
        executeInteractorNoRepeat(accountPwdLoginUseCase.get().fill(account, password),
                new LoadingDialogSubscriber<MultiResult<Object>>(this, this, true) {
                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        objectMultiResult.getInfo().put(Navigator.ACCOUNT, account);
                        objectMultiResult.getInfo().put(Navigator.PASSWORD, password);
                        LoginHelper.execCkmsCreateUseCase(RegisterVerifyPhoneNumberPresenter.this,
                                ckmsCreateUseCase,
                                ckmsForceAddUseCase,
                                objectMultiResult,
                                logoutHelper);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-18 add. fix bug 6117 . review by wangchao1. Start
                        super.onError(e);
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-18 add. fix bug 6117 . review by wangchao1. End
                        LogUtil.getUtils().e(e);
                        //alh@xdja.com<mailto://alh@xdja.com> 2017-02-03 add. fix bug 8282 . review by wangchao1. Start
                        ActivityStack activityStack = ActivityStack.getInstanse();
                        if (activityStack.getAllActivities().contains(activityStack.getActivityByClass(LoginPresenter.class))){
                            activityStack.popActivitiesUntil(LoginPresenter.class, true);
                        }else {
                            Intent intent = Navigator.generateIntent(LoginPresenter.class);
                            intent.putExtra(Navigator.EXIT, true);
                            intent.putExtra(Navigator.ACCOUNT , account);
                            startActivity(intent);
                        }
                        //alh@xdja.com<mailto://alh@xdja.com> 2017-02-03 add. fix bug 8282 . review by wangchao1. End
                    }
                }.registerLoadingMsg(getString(R.string.login)));
    }

    private void login(LoadingDialogSubscriber subscriber) {
        if (isMNC() && CustInfo.isTelcom()) {
            ArrayList<String> permission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE, "");
            if (permission != null && !permission.isEmpty()) {
                if (subscriber != null)subscriber.setDismissDialogWhenCompleleted(true);
                ActivityCompat.requestPermissions(this, permission.toArray(new String[]{}), REQ_PHONE_STATE_PERMISSION);
                return;
            }
        }
        execMobileLoginUseCase();
         /*tangsha@xdja.com @2016-08-04. for account not unify.review by self. Start*/
//        addInteractor2Queue(accountPwdLoginUseCase.get()).fill(account, password)
//                .execute(new PerSubscriber<MultiResult<Object>>(this) {
//
//                    @Override
//                    public void onNext(MultiResult<Object> objectMultiResult) {
//                        super.onNext(objectMultiResult);
//                        objectMultiResult.getInfo().put(Navigator.ACCOUNT, account);
//                        objectMultiResult.getInfo().put(Navigator.PASSWORD, password);
//                        LoginHelper.loginResultHandler(RegisterVerifyPhoneNumberPresenter.this, objectMultiResult, getVu());
//                    }
//                }.registUserMsg(ServerException.class, ServerException.ACCOUNT_NOT_EXISTS, "未知原因，操作失败（404）")
//                        .registUserMsg(ServerException.class, ServerException.ACCOUNT_OR_PWD_ERROR, "未知原因，操作失败（401）")
//                        .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, AUTH_CODE_ERROR));
    }
}
