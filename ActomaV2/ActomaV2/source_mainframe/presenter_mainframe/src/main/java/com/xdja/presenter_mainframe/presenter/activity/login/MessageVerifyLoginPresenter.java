package com.xdja.presenter_mainframe.presenter.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.cust.CustInfo;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.MessageVerifyLoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuMessageLogin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by ldy on 16/4/13.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class MessageVerifyLoginPresenter extends PresenterActivity<VerifyPhoneNumberCommand, VuMessageLogin> implements VerifyPhoneNumberCommand {
    private static String innerAuthCode;

    @Inject
    @InteractorSpe(value = DomainConfig.LOGIN_AUTHCODE_OBTAIN)
    Lazy<Ext1Interactor<String, String>> loginAuthCodeObtainUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.LOGIN_MOBILE)
    Lazy<Ext3Interactor<String, String, String, MultiResult<Object>>> mobileLoginUseCase;

    /*[S]modify by tangsha@20160708 for ckms*/
    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_CREATE_SEC)
    Lazy<Ext1Interactor<String,MultiResult<Object>>> ckmsCreateUseCase;

    @Inject
    LogoutHelper logoutHelper;

    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;
    private String TAG = "anTongCkms MessageVerifyLoginPresenter";
    /*[E]modify by tangsha@20160708 for ckms*/

    //[S] add by licong for safeLock
    /*@Inject
    @InteractorSpe(DomainConfig.GET_SAFELOCK_CLOUD_SETTINGS)
    Lazy<Ext1Interactor<String,MultiResult<Object>>> getSafeLockSettings;*/
    //[E] add by licong for safeLock


    @NonNull
    @Override
    protected Class<? extends VuMessageLogin> getVuClass() {
        return MessageVerifyLoginView.class;
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
        addInteractor2Queue(loginAuthCodeObtainUseCase.get()).fill(phoneNumber)
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
                }
                        .registUserMsg(ServerException.class, ServerException.MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error))
                        .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, getString(R.string.phone_or_verifycode_error)));
    }

    String phoneNumber = "";
    String verifyCode = "";
    private static final int REQ_PHONE_STATE_PERMISSION = 11;

    @Override
    public void complete(List<String> stringList) {
        if (innerAuthCode == null) {
            getVu().showToast(RegisterVerifyPhoneNumberPresenter.AUTH_CODE_ERROR);
            return;
        }
        phoneNumber = stringList.get(0);
        verifyCode = stringList.get(1);
        if (isMNC() && CustInfo.isTelcom()) {
            ArrayList<String> permission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE, "");
            if (permission != null && !permission.isEmpty()) {
                ActivityCompat.requestPermissions(this, permission.toArray(new String[]{}), REQ_PHONE_STATE_PERMISSION);
                return;
            }
        }
        execMobileLoginUseCase();
    }

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
        executeInteractorNoRepeat(mobileLoginUseCase.get().fill(phoneNumber, verifyCode, innerAuthCode),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this) {
                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        objectMultiResult.getInfo().put(Navigator.PHONE_NUMBER, phoneNumber);
                        objectMultiResult.getInfo().put(Navigator.INNER_AUTH_CODE, innerAuthCode);
                        objectMultiResult.getInfo().put(Navigator.VERIFY_CODE, verifyCode);
                        //tangsha@xdja.com 2016-08-04 modify. ckms fail login success need logout, log in other result need as fail. review by self. Start
                        LoginHelper.execCkmsCreateUseCase(MessageVerifyLoginPresenter.this,
                                ckmsCreateUseCase,ckmsForceAddUseCase,objectMultiResult,logoutHelper);
                        //tangsha@xdja.com 2016-08-04 modify. ckms fail login success need logout, log in other result need as fail. review by self. Start
                    }
                }
                 .registerLoadingMsg(getString(R.string.login)));
    }
}
