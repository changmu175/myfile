package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.UpdateBindMobileEvent;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ModifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ModifyPhoneNumberView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuModifyPhoneNumber;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class ModifyPhoneNumberPresenter extends PresenterActivity<ModifyPhoneNumberCommand,VuModifyPhoneNumber> implements ModifyPhoneNumberCommand {

    @Inject
    @InteractorSpe(value = DomainConfig.AUTH_PASSWD)
    Lazy<Ext1Interactor<String,Void>> authPasswordUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.UNBIND_MOBILE)
    Lazy<Ext1Interactor<String,Void>> unbindMobileUseCase;
    private String phoneNumber;

    @Inject
    BusProvider busProvider;

    @NonNull
    @Override
    protected Class<? extends VuModifyPhoneNumber> getVuClass() {
        return ModifyPhoneNumberView.class;
    }

    @NonNull
    @Override
    protected ModifyPhoneNumberCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        phoneNumber = getIntent().getStringExtra(Navigator.PHONE_NUMBER);
        if (!TextUtils.isEmpty(phoneNumber)){
            getVu().setPhoneNumber(phoneNumber);
        }
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
            busProvider.register(this);
        }
    }

    /**
     * 校验密码是否正确
     * @param password 密码
     * @param type
     */
    @Override
    public void checkPassword(String password, final int type) {
        executeInteractorNoRepeat(authPasswordUseCase.get().fill(password),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        getVu().dismissDialog();

                        if (type == ModifyPhoneNumberView.MODIFY_MOBILE_TYPE){
                            Navigator.navigateToBindPhoneNumber(BindPhoneNumberPresenter.CURRENT_TYPE,
                                    BindPhoneNumberPresenter.MODIFY_PHONE_TYPE);
                        }else if (type == ModifyPhoneNumberView.UNBIND_MOBILE_TYPE){
                            getVu().showUnbindMobileDialog();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().clearPasswordWithDialog();
                    }
                }.registerLoadingMsg(getString(R.string.verify)));
    }



    @Override
    public void unbindPhone() {
        if (TextUtils.isEmpty(phoneNumber)){
            return;
        }
        executeInteractorNoRepeat(unbindMobileUseCase.get().fill(phoneNumber),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        unbindPhoneSuccess();
                        finish();
                    }

                    @SuppressWarnings("EmptyMethod")
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }.registerLoadingMsg(getString(R.string.unbind)));
    }

    /**
     * 解绑成功，用户详情界面需要更改绑定手机号内容
     */
    private void unbindPhoneSuccess() {
        //发送修改手机号码的事件
        UpdateBindMobileEvent event = new UpdateBindMobileEvent();
        event.setNewMobile("");
        busProvider.post(event);
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode==null)
            return true;
        if (okCode.equals(ServerException.ACCOUNT_OR_PWD_ERROR)){
            getVu().showToast(getString(R.string.pwd_error_retry_input));
        }
        if (okCode.equals(ServerException.ACCOUNT_MOBILE_NOT_BIND)){
            unbindPhoneSuccess();
        }
        return false;
    }

    /**
     * 修改绑定手机号码
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void updateMobile(UpdateBindMobileEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }
}
