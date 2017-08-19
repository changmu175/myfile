package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext5Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AuthAccountLoginCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.AuthAccountLoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAuthAccountLogin;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class AuthAccountLoginPresenter extends PresenterActivity<AuthAccountLoginCommand, VuAuthAccountLogin> implements AuthAccountLoginCommand {
    private String authorizeId;
    /*[S]add by tangsha@20160714 for ckms auth add device*/
    private String digtalAccount;
    private String ckmsAuthId;
    private String cardNo;
    /*[S]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    private String sn;
    /*[E]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    private String TAG = "anTongAuthAccountLoginPresenter";
    /*[E]add by tangsha@20160714 for ckms auth add device*/

    /*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
    @Inject
    @InteractorSpe(value = DomainConfig.GET_AUTH_INFO)
    Lazy<Ext2Interactor<String,String,Map<String,Object>>> getAuthInfoUseCase;
    /*[E]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
    /*[S]add by tangsha@20160714 for ckms auth add device*/
    @Inject
    @InteractorSpe(value = DomainConfig.AUTH_DEVICE)
    Lazy<Ext5Interactor<String, String, String, String, String, Void>> authDeviceUsecase;
    /*[E]add by tangsha@20160714 for ckms auth add device*/
    /*[E]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/

    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;


    @NonNull
    @Override
    protected Class<? extends VuAuthAccountLogin> getVuClass() {
        return AuthAccountLoginView.class;
    }

    @NonNull
    @Override
    protected AuthAccountLoginCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }
        /*[S]modify by tangsha for CKMS auth device*/
        /*[S]modify by xienana @2016/08/08 to fix bug 2202 [review by] tangsha*/
        final String orgAuthInfo = getIntent().getStringExtra(Navigator.AUTHORIZE_ID);
        Log.d(TAG, "orgAuthInfo " + orgAuthInfo);

        /*[S]add by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        String index = String.valueOf(orgAuthInfo.charAt(0));
        Log.i(TAG,"index ="+index);
        if (!index.equals("c") && !index.equals("C") && !index.equals("a") && !index.equals("A")) {
            Log.i(TAG,"auth info index is not c || a ");
            showAuthInfoInvailToast();
			return;
        }/*[E]add by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/

        int ckmsAuthIndex = orgAuthInfo.indexOf("C");
        if (ckmsAuthIndex == -1) {
            ckmsAuthIndex = orgAuthInfo.indexOf("c");
        }
        int anTongStartIndex = orgAuthInfo.indexOf("A");
        if (anTongStartIndex == -1) {
            anTongStartIndex = orgAuthInfo.indexOf("a");
        }
        int anTongAuthEnd = orgAuthInfo.length();
        if (ckmsAuthIndex != -1) {
            if (ckmsAuthIndex + 1 < anTongStartIndex && ckmsAuthIndex + 1 < anTongAuthEnd) {
                ckmsAuthId = orgAuthInfo.substring(ckmsAuthIndex + 1, anTongStartIndex);
            }
        }
        //modify by alh@xdja.com to fix bug: 2006 2016-07-26 start (rummager : tangsha)
        if (anTongStartIndex != -1) {
            if (anTongStartIndex + 1 < anTongAuthEnd) {
                authorizeId = orgAuthInfo.substring(anTongStartIndex + 1, anTongAuthEnd);
            }
        }
        /*[E]modify by xienana @2016/08/08 to fix bug 2202 [review by] tangsha*/
        //modify by alh@xdja.com to fix bug: 2006 2016-07-26 end (rummager : tangsha)
        Log.d(TAG,"onBindView ckmsAuthId "+ckmsAuthId+" authorizeId "+authorizeId);
        /*[E]modify by tangsha for CKMS auth device*/

        /*[S]add by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        if (!TextUtils.isEmpty(authorizeId)) {
            executeInteractorNoRepeat(getAuthInfoUseCase.get().fill(authorizeId,ckmsAuthId),
                    new LoadingDialogSubscriber<Map<String, Object>>(this,this) {
                        @Override
                        public void onNext(Map<String, Object> stringStringMap) {
                            super.onNext(stringStringMap);
                            if(stringStringMap != null){
                                String cardNo = (String) stringStringMap.get("cardNo");
                                String sn = (String) stringStringMap.get("sn");
                                String strAccount = (String) stringStringMap.get("strAccount");
                                String aliasAccount = (String) stringStringMap.get("aliasAccount");
                                Log.i(TAG,"getAuthInfoUseCase " + "cardNo =" +cardNo +"  sn ="+sn+" strAccount"+strAccount
                                        +" aliasAccount ="+aliasAccount);

                                if(CkmsGpEnDecryptManager.getCkmsIsOpen()){
                                    boolean isCkmsAuthed= (boolean) stringStringMap.get("isCkmsAuthed");
                                    Log.i(TAG,"getAuthInfoUseCase isCkmsAuthed="+isCkmsAuthed);
                                    if(isCkmsAuthed){
                                        if(orgAuthInfo.contains("c") || orgAuthInfo.contains("C")){
                                            showAuthInfoInvailToast();
											return;
                                        }
                                    }
                                }

                                AuthAccountLoginPresenter.this.sn = sn;
                                AuthAccountLoginPresenter.this.cardNo = cardNo;
                                digtalAccount = strAccount;
                                if (!TextUtils.isEmpty(aliasAccount)){
                                    strAccount = aliasAccount;
                                }
                                getVu().setAccountAndCardNo(strAccount, cardNo);
                            }else{
                                Log.d(TAG,"getAuthInfoUseCase.get().fill stringStringMap is null");
                                 showAuthInfoInvailToast();
                            }
                        }
                        //modify by alh@xdja.com to fix bug: 895 2016-06-29 start (rummager : wangchao1)
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            if (e instanceof SocketTimeoutException){
                                finish();
                                getVu().showToast(getString(R.string.timeout));
                            }
                        }
                        //modify by alh@xdja.com to fix bug: 895 2016-06-29 end (rummager : wangchao1)
                    }.registerLoadingMsg(getString(R.string.handle)));
        }else{
            showAuthInfoInvailToast();
        }
    }/*[E]add by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/

    @Override
    public void certain() {
        if (TextUtils.isEmpty(cardNo)|| TextUtils.isEmpty(authorizeId)){
            return;
        }
		 /*[S]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
        /*[S]modify by tangsha for CKMS auth device*/
        executeInteractorNoRepeat(authDeviceUsecase.get().fill(digtalAccount, ckmsAuthId, authorizeId, cardNo, sn),
        /*[E]modify by tangsha for CKMS auth device*/
		/*[E]modify by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
                new LoadingDialogSubscriber<Void>(this,this){
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        authSuccess();
                    }
                }.registerLoadingMsg(getString(R.string.empower)));
    }

    /**
     * 授信成功
     */
    private void authSuccess() {
        getVu().showToast(R.string.auth_success);
    }

    private void haveTrust() {
        getVu().showToast(R.string.have_trust);
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        //modify by alh@xdja.com to fix bug: 559 2016-06-27 start (rummager : wangchao1)
        if (okCode.equals(ServerException.ACCOUNT_AUTHORIZELD_NOT_RELATION)
                || okCode.equals(ServerException.AUTHORIZELD_INVALID)) {
            //alh@xdja.com<mailto://alh@xdja.com> 2016-08-04 add. fix bug 2455 . review by wangchao1. Start
            userMsg = getString(R.string.authorizeld_invalid);  //add by ysp ,fix bug #6907
            getVu().showToast(userMsg);
            //alh@xdja.com<mailto://alh@xdja.com> 2016-08-04 add. fix bug 2455 . review by wangchao1. End
            //modify by alh@xdja.com to fix bug: 545 2016-06-30 start (rummager : wangchao1)
            finish();
            //modify by alh@xdja.com to fix bug: 545 2016-06-30 end (rummager : wangchao1)
            return false;
        }
        if (okCode.equals(ServerException.DEVICE_ACCOUT_ALREADY_AUTHORIZELD)) {
            authSuccess();
            return false;
        }
        //modify by alh@xdja.com to fix bug: 10752 2017-04-07 start (rummager : gbc)
        if (okCode.equals(ServerException.DEVICE_ACCOUT_ALREADY_AUTHORIZE)) {
            haveTrust();
            return false;
        }
        //modify by alh@xdja.com to fix bug: 10752 2017-04-07 end (rummager : gbc)

        if (okCode.equals(ServerException.CARD_NO_NOT_ACCORDANCE)) {
            getVu().showToast(R.string.unknown_reason_401);
            finish();
            return false;
        }
         /*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        if(okCode.equals(CkmsException.CODE_CKMS_AUTH_INFO_INVALID)){
            showAuthInfoInvailToast();
            return false;
        }/*[E]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        //modify by alh@xdja.com to fix bug: 559 2016-06-27 end (rummager : wangchao1)
        return true;
    }

    private void showAuthInfoInvailToast(){
        getVu().showToast(R.string.auth_info_invalid);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
