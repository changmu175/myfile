package com.xdja.presenter_mainframe.presenter.activity.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.EmpowerDeviceLoginCommand;
import com.xdja.presenter_mainframe.di.modules.AppModule;
import com.xdja.presenter_mainframe.global.obs.Observable;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.EmpowerDeviceLoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuEmpowerDeviceLogin;
import com.xdja.presenter_mainframe.util.ErrorUtil;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Lazy;
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class EmpowerDeviceLoginPresenter extends
        PresenterActivity<EmpowerDeviceLoginCommand, VuEmpowerDeviceLogin> implements EmpowerDeviceLoginCommand {
    private static final String NEW_AUTHORIZE_ID = "newAuthorizeId";

    private String account;
    //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String digitalAccount;
    //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
    private String password;
    private String innerAuthCode;
    private String authorizeId;
    /*[S]add by tangsha@20160711 for ckms*/
    private String ckmsAddReqId;
    private String TAG = "anTong EmpowerDeviceLoginPresenter";
    /*[E]add by tangsha@20160711 for ckms*/

    @Inject
    @InteractorSpe(value = DomainConfig.REOBTATION_AUTHINF)
    Lazy<Ext3Interactor<String, String, String, Map<String, String>>> reObtainAuthCodeUseCase;
    private String phoneNumber;

    @Inject
    @Named(AppModule.OBSERBABLE_BINDDEVICE)
    Observable bindDeviceObservable;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.LOGIN_MOBILE)
    Lazy<Ext3Interactor<String, String, String, MultiResult<Object>>> mobileLoginUseCase;
    private PerSubscriber<Void> bindLoginSubscriber;
    private String verifyCode;

    @NonNull
    @Override
    protected Class<? extends VuEmpowerDeviceLogin> getVuClass() {
        return EmpowerDeviceLoginView.class;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();
        /*[S]modify by tangsha@20160711 for ckms*/
        generateAuthStr();
        /*[E]modify by tangsha@20160711 for ckms*/
        bindLoginSubscriber = new PerSubscriber<Void>(null) {
            @Override
            public void onNext(Void result) {
                if (!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(password)){
                    LoginHelper.accountPwdAutoLogin(EmpowerDeviceLoginPresenter.this,accountPwdLoginUseCase,account,password);
                }else if (!TextUtils.isEmpty(phoneNumber)&&!TextUtils.isEmpty(verifyCode)&&!TextUtils.isEmpty(innerAuthCode)){
                    LoginHelper.MobileVerifyAutoLogin(EmpowerDeviceLoginPresenter.this,mobileLoginUseCase,phoneNumber,verifyCode,innerAuthCode);
                }
            }

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        };
        bindDeviceObservable.subscribe(bindLoginSubscriber);
    }

    /*[S]modify by tangsha@20160711 for ckms*/
    /*[S]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    private void generateAuthStr(){
        String authStr = "";
        if(ckmsAddReqId != null && ckmsAddReqId.isEmpty() == false){
            authStr = authStr+"C"+ckmsAddReqId;
        }
        if (authorizeId == null){
            LogUtil.getUtils().e("authorizeId为空");
        }else {
            authStr = authStr+"A"+authorizeId;
        }
        Log.i(TAG,"authStr = "+authStr);
        if(authStr.isEmpty() == false){
            getVu().setAuthorizeId(authStr);
        }else{
            LogUtil.getUtils().e(TAG+"authStr is empty ",null);
        }
    }
    /*[E]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    /*[E]modify by tangsha@20160711 for ckms*/

    private void setDataFromIntent() {
        account = getIntent().getStringExtra(Navigator.ACCOUNT);
        //[S]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        digitalAccount = getIntent().getStringExtra(Navigator.DIGITAL_ACCOUNT);
        //[E]tangsha@xdja.com 2016-08-12 add. for ckms use digital account. review by self.
        password = getIntent().getStringExtra(Navigator.PASSWORD);
        innerAuthCode = getIntent().getStringExtra(Navigator.INNER_AUTH_CODE);
        authorizeId = getIntent().getStringExtra(Navigator.AUTHORIZE_ID);
        phoneNumber = getIntent().getStringExtra(Navigator.PHONE_NUMBER);
        verifyCode = getIntent().getStringExtra(Navigator.VERIFY_CODE);
        ckmsAddReqId = getIntent().getStringExtra(Navigator.CKMS_VERIFY_CODE);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-20 add. fix bug 4252 . review by wangchao1. Start
        if(TextUtils.isEmpty(account)){
            account = phoneNumber;
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-20 add. fix bug 4252 . review by wangchao1. End

        LogUtil.getUtils().w(TAG+"authorizeId "+authorizeId+" ckmsAddReqId "+ckmsAddReqId);
    }

    @NonNull
    @Override
    protected EmpowerDeviceLoginCommand getCommand() {
        return this;
    }

    @Override
    public void generateAgainEmpower() {
        executeInteractorNoRepeat(reObtainAuthCodeUseCase.get().fill(account, innerAuthCode, authorizeId)
        ,new LoadingDialogSubscriber<Map<String, String>>(this,this) {
                    @Override
                    public void onNext(Map<String, String> stringStringMap) {
                        super.onNext(stringStringMap);
                        if (stringStringMap == null || stringStringMap.isEmpty()) {
                            onError(new CheckException(getString(R.string.return_map_not_empty)));
                        }
                        authorizeId = stringStringMap.get(NEW_AUTHORIZE_ID);
                        if (authorizeId == null) {
                            onError(new CheckException(getString(R.string.return_parameter_error)));
                        }
                        /*[S]modify by tangsha@20160714 for ckms*/
                        generateAuthStr();
                        /*[E]modify by tangsha@20160714 for ckms*/
                    }
                }.registerLoadingMsg(getString(R.string.create)));
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        ErrorUtil.loginPop2First(okCode);
        return true;
    }

    @Override
    public void cannotEmpower() {
        Navigator.navigateToVerifyLogin(account,digitalAccount,verifyCode ,innerAuthCode,phoneNumber,password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bindDeviceObservable != null) {
            bindDeviceObservable.unSubscribe(bindLoginSubscriber);
        }
        getVu().onDestroy();
    }
}
