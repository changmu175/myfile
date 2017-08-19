package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.event.UpdateBindMobileEvent;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.BindPhoneNumberCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.BindPhoneNumberView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuBindPhoneNumber;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class BindPhoneNumberPresenter extends PresenterActivity<BindPhoneNumberCommand, VuBindPhoneNumber> implements BindPhoneNumberCommand {

    //绑定手机号
    @Inject
    @InteractorSpe(value = DomainConfig.TICKE_BIND_MOBILE)
    Lazy<Ext2Interactor<String, String, Void>> tickBindMobile;

    //获得当前账号
    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.BIND_AUTHOCODE_OBTAIN)
    Lazy<Ext1Interactor<String,Void>> authCoudeBindMobile;


    //更换手机号
    @Inject
    @InteractorSpe(value = DomainConfig.MODIFY_AUTHOCODE_OBTAIN)
    Lazy<Ext1Interactor<String,Void>> ticketModifyMobile;

    @Inject
    @InteractorSpe(value = DomainConfig.MODIFY_MOBILE)
    Lazy<Ext2Interactor<String,String,Void>> modifyMobile;



    //强制绑定
    @Inject
    @InteractorSpe(value = DomainConfig.TICKE_FORCE_BIND_MOBILE)
    Lazy<Ext1Interactor<String, Void>> tickForceBindMobile;


    @Inject
    BusProvider busProvider;


    /**
     * 进入到当前页面的操作业务类型
     */
    public static final String BIND_PHONE_TYPE = "bindPhoneType";
    public static final String MODIFY_PHONE_TYPE = "modifyPhoneType";

    public static final String CURRENT_TYPE = "currenttype";


    private String phoneNumber;

    private String authCode;

    //默认本页面操作类型为绑定手机号
    private String currentType = BIND_PHONE_TYPE;

    @NonNull
    @Override
    protected Class<? extends VuBindPhoneNumber> getVuClass() {
        return BindPhoneNumberView.class;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
        currentType = getIntent().getStringExtra(CURRENT_TYPE);
    }

    @NonNull
    @Override
    protected BindPhoneNumberCommand getCommand() {
        return this;
    }


    @Override
    public void getVerifyCode(String phoneNumber) {
        if (currentType.equals(BIND_PHONE_TYPE)){
            addInteractor2Queue(authCoudeBindMobile.get()).fill(phoneNumber).
                    execute(new PerSubscriber<Void>(this){
                        //// TODO: 代写截取短信直接填写验证码
                        @Override
                        public void onNext(Void aVoid) {
                            super.onNext(aVoid);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            getVu().resetVerifyCode();
                        }
                    }.registUserMsg(ServerException.class, ServerException.MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error))
                    .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, getString(R.string.phone_or_verifycode_error)));
        }else if (currentType.equals(MODIFY_PHONE_TYPE)){
            addInteractor2Queue(ticketModifyMobile.get()).fill(phoneNumber).execute(
                    new PerSubscriber<Void>(this){
                        @Override
                        public void onNext(Void aVoid) {
                            super.onNext(aVoid);
                        }
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            getVu().resetVerifyCode();
                        }
                    }.registUserMsg(ServerException.class, ServerException.MOBILE_NOT_ACCORDANCE, getString(R.string.phone_or_verifycode_error))
                            .registUserMsg(ServerException.class, ServerException.AUTH_CODE_ERROR, getString(R.string.phone_or_verifycode_error))
            );
        }

    }

    @Override
    public void complete(List<String> stringList) {
        final String phoneNumber = stringList.get(0);
        String authCode = stringList.get(1);
        this.phoneNumber = phoneNumber;

        if (currentType.equals(BIND_PHONE_TYPE)){
            executeInteractorNoRepeat(tickBindMobile.get().fill(authCode,phoneNumber),
                    new LoadingDialogSubscriber<Void>(this,this){
                        @Override
                        public void onNext(Void aVoid) {
                            super.onNext(aVoid);
                            getVu().showToast(getString(R.string.bind_phone_number_succeed));
                            //发送修改手机号码的事件
                            UpdateBindMobileEvent event = new UpdateBindMobileEvent();
                            event.setNewMobile(phoneNumber);
                            busProvider.post(event);

                            finish();
                        }
                    }.registerLoadingMsg(getString(R.string.bind)));
        }else if (currentType.equals(MODIFY_PHONE_TYPE)){
            executeInteractorNoRepeat(modifyMobile.get().fill(phoneNumber,authCode),
                    new LoadingDialogSubscriber<Void>(this,this) {
                        @Override
                        public void onNext(Void aVoid) {
                            super.onNext(aVoid);
                            getVu().showToast(getString(R.string.change_phone_number_succeed));

                            //发送修改手机号码的事件
                            UpdateBindMobileEvent event = new UpdateBindMobileEvent();
                            event.setNewMobile(phoneNumber);
                            busProvider.post(event);

                            finish();
                        }
                    }.registerLoadingMsg(getString(R.string.change)));
        }

    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null)return true;
        if (okCode.equals(ServerException.MOBILE_ALREADY_REGISTER)){
            getVu().showChooseDialog();
            return false;
            //modify by alh@xdja.com to fix bug: 562 2016-06-29 start (rummager : wangchao1)
        }else if (okCode.equals(ServerException.ACCOUNT_ALREADY_BIND_MOBILE)){
            getVu().showSameMobileDialog();
            return false;
        }
           //modify by alh@xdja.com to fix bug: 562 2016-06-29 end (rummager : wangchao1)
        return true;
    }

    @Override
    public void forceBind() {
        executeInteractorNoRepeat(tickForceBindMobile.get().fill(phoneNumber),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        getVu().showToast(getString(R.string.phone_number_bind_succeed));

                        //发送修改手机号码的事件
                        UpdateBindMobileEvent event = new UpdateBindMobileEvent();
                        event.setNewMobile(phoneNumber);
                        busProvider.post(event);

                        finish();
                    }
                }.registerLoadingMsg(getString(R.string.bind)));
    }
}
