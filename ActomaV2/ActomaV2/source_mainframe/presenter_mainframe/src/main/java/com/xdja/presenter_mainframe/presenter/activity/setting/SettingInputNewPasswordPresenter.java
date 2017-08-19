package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.InputNewPasswordView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuInputNewPassword;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SettingInputNewPasswordPresenter extends PresenterActivity<FillMessageCommand, VuInputNewPassword> implements FillMessageCommand {

    @Inject
    @InteractorSpe(value = DomainConfig.MODIFY_PASSWD)
    Lazy<Ext1Interactor<String, Void>> resetPwdByLastPwd;

    @NonNull
    @Override
    protected Class<? extends VuInputNewPassword> getVuClass() {
        return InputNewPasswordView.class;
    }

    @NonNull
    @Override
    protected FillMessageCommand getCommand() {
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
        String password = stringList.get(0);
        String passwordAgain = stringList.get(1);
        if (!TextUtil.isRulePassword(password)) {
            getVu().showToast(getString(R.string.password_format));
        } else if (!password.equals(passwordAgain)) {
            getVu().showToast(getString(R.string.different_two_password));

        } else {
            resetPasswdByLastPasswd(password);
        }
    }


    private void resetPasswdByLastPasswd(String password) {
        executeInteractorNoRepeat(resetPwdByLastPwd.get().fill(password),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        getVu().showToast(getString(R.string.password_modify_success));
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }.registerLoadingMsg(getString(R.string.submit)));
    }
}
