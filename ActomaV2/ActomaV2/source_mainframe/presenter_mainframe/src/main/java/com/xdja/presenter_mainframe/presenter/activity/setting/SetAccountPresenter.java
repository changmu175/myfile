package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.event.UpdateAccountEvent;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetAccountCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.SetAccountView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetAccount;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SetAccountPresenter extends PresenterActivity<SetAccountCommand,VuSetAccount> implements SetAccountCommand {
    public static final int MODIFY_ACCOUNT_SUCCESS = 60;
    @Inject
    @InteractorSpe(value = DomainConfig.TICKE_CUSTOM_ACCOUNT)
    Lazy<Ext1Interactor<String, Void>> ticketCustomAccountUseCase;

    @Inject
    BusProvider busProvider;

    @NonNull
    @Override
    protected Class<? extends VuSetAccount> getVuClass() {
        return SetAccountView.class;
    }

    @NonNull
    @Override
    protected SetAccountCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
    }

    @Override
    public void complete(List<String> stringList) {
        final String newAccount = stringList.get(0);
        if (!TextUtil.isRuleAccount(newAccount)){
            //modify by alh@xdja.com to fix bug: 537 2016-06-22 start (rummager : guobinchang)
            getVu().showToast(getString(R.string.set_account_bottom_text));
            //modify by alh@xdja.com to fix bug: 537 2016-06-22 end (rummager : guobinchang)
            return;
        }

        executeInteractorNoRepeat(ticketCustomAccountUseCase.get().fill(newAccount),
                new LoadingDialogSubscriber<Void>(this,this){
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);

                        //把新的账号广播出去，让需要改变的界面进行更改
                        UpdateAccountEvent updateAccountEvent = new UpdateAccountEvent();
                        updateAccountEvent.setNewAccount(newAccount);
                        busProvider.post(updateAccountEvent);

                        getVu().showToast(getString(R.string.set_account_succeed));
                        finish();
                    }

                    @SuppressWarnings("EmptyMethod")
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }.registerLoadingMsg(getString(R.string.submit)));
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode==null){
            return true;
        }
        return true;
    }

//    /**
//     * 更新账号的事件
//     */
//    public static class UpdateAccountEvent {
//        private String newAccount;
//
//        public String getNewAccount() {
//            return newAccount;
//        }
//
//        public void setNewAccount(String newAccount) {
//            this.newAccount = newAccount;
//        }
//    }
}
