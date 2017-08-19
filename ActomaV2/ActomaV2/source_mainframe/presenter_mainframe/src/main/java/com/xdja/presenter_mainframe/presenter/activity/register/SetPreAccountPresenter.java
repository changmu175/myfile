package com.xdja.presenter_mainframe.presenter.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetAccountCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.SetAccountView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetAccount;
import com.xdja.presenter_mainframe.util.ErrorUtil;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class SetPreAccountPresenter extends PresenterActivity<SetAccountCommand, VuSetAccount> implements SetAccountCommand {
    public static final int MODIFY_ACCOUNT_SUCCESS = 60;

    @Inject
    @InteractorSpe(DomainConfig.ACCOUNT_CUSTOM)
    Lazy<Ext3Interactor<String, String, String, Void>> customAccountUseCase;

    @Inject
    Intent intent;

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
        getActivityPreUseCaseComponent().inject(this);

    }

    @Override
    public void complete(List<String> stringList) {
        final String newAccount = stringList.get(0);
        if (!TextUtil.isRuleAccount(newAccount)) {
            //modify by alh@xdja.com to fix bug: 537 2016-06-22 start (rummager : guobinchang)
            getVu().showToast(getString(R.string.account_error));
            //modify by alh@xdja.com to fix bug: 537 2016-06-22 end (rummager : guobinchang)
            return;
        }
        executeInteractorNoRepeat(customAccountUseCase.get().fill(
                intent.getStringExtra(Navigator.ACCOUNT),
                intent.getStringExtra(Navigator.INNER_AUTH_CODE),
                newAccount
        ),new LoadingDialogSubscriber<Void>(this,this) {
            @Override
            public void onNext(Void aVoid) {
                super.onNext(aVoid);
                Intent intent = new Intent();
                intent.putExtra(Navigator.ACCOUNT, newAccount);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        ErrorUtil.registerPop2First(okCode);
        return true;
    }
}
