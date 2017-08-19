package com.xdja.presenter_mainframe.presenter.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.presenter.activity.register.WriteRegistrationInfoPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.DataMigrationFixPwdView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDataMigrationFixPwd;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by ALH on 2016/8/9.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class DataMigrationFixPwdPresenter extends PresenterActivity<FillMessageCommand, VuDataMigrationFixPwd>
        implements FillMessageCommand {
    private String mAccount;

    @Inject
    @InteractorSpe(DomainConfig.DATA_MIGRATION_FINISH)
    Lazy<Ext3Interactor<String,String,String, MultiResult<Object>>> dataMigrationAddPwdUserCase;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_PWD_LOGIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> accountPwdLoginUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_CREATE_SEC)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsCreateUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.CKMS_FORCE_ADD_DEV)
    Lazy<Ext1Interactor<String, MultiResult<Object>>> ckmsForceAddUseCase;



    @NonNull
    @Override
    protected Class<? extends VuDataMigrationFixPwd> getVuClass() {
        return DataMigrationFixPwdView.class;
    }

    @NonNull
    @Override
    protected FillMessageCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        LogUtil.getUtils().e("DataMigrationFixPwdPresenter onBindView");
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        mAccount = getIntent().getStringExtra(Account.ACCOUNT);
    }

    @Inject
    LogoutHelper logoutHelper;

    private void login() {
        executeInteractorNoRepeat(accountPwdLoginUseCase.get().fill(mAccount, password),
                new LoadingDialogSubscriber<MultiResult<Object>>(this, this, true) {
                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        objectMultiResult.getInfo().put(Navigator.ACCOUNT, mAccount);
                        objectMultiResult.getInfo().put(Navigator.PASSWORD, password);
                        LoginHelper.execCkmsCreateUseCase(DataMigrationFixPwdPresenter.this,
                                ckmsCreateUseCase,
                                ckmsForceAddUseCase,
                                objectMultiResult,
                                logoutHelper);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                        //modify by alh@xdja.com to fix bug: 1822 2016-07-22 start (rummager : self)
                        if (ActomaApplication.getInstance() != null && e != null && !TextUtils.isEmpty(e.getMessage())) {
                            XToast.show(ActomaApplication.getInstance(), e.getMessage());
                        }
                        //modify by alh@xdja.com to fix bug: 1822 2016-07-22 end (rummager : self)

                        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-05 add. To complete the task  1823 . review by wangchao1. Start
                        Intent intent = Navigator.generateIntent(LoginPresenter.class);
                        if (intent != null) {
                            intent.putExtra(Navigator.EXIT, true);
                            intent.putExtra(Navigator.ACCOUNT, mAccount);
                            startActivity(intent);
                        }
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-05 add. To complete the task  1823 . review by wangchao1. End
                    }
                }.registerLoadingMsg(getString(R.string.login)));
    }

    private String password;
    private String passwordAgain;

    @Override
    public void complete(List<String> stringList) {
        password = stringList.get(0);
        passwordAgain = stringList.get(1);
        executeInteractorNoRepeat(dataMigrationAddPwdUserCase.get().fill(mAccount,password,passwordAgain) , new LoadingDialogSubscriber<MultiResult<Object>>(this,this,true){

            @Override
            public void onNext(MultiResult<Object> stringMultiResult) {
                super.onNext(stringMultiResult);
                String toastMsg = null;
                switch (stringMultiResult.getResultStatus()) {
                    case WriteRegistrationInfoPresenter.RESULT_STATUS_SUCCESS:
                        if (TextUtils.isEmpty((String) stringMultiResult.getInfo().get("mobile"))) {
                            setDismissDialogWhenCompleleted(true);
                            Navigator.navigateToRegisterVerifyPhoneNumber(mAccount, (String) stringMultiResult
                                    .getInfo().get("innerAuthCode"), password, true);
                            finish();
                            return;
                        }
                        login();
                        break;
                    case WriteRegistrationInfoPresenter.RESULT_STATUS_LIMIT_DAY:

                        toastMsg = getString(R.string.register_count) + stringMultiResult.getInfo().get(WriteRegistrationInfoPresenter.DAY_COUNT) + getString(R.string.count);
                        break;
                    case WriteRegistrationInfoPresenter.RESULT_STATUS_LIMIT_MONTH:
                        toastMsg = getString(R.string.register_count_month)+ stringMultiResult.getInfo().get(WriteRegistrationInfoPresenter.MONTH_COUNT) + getString(R.string.count);
                        break;
                    case WriteRegistrationInfoPresenter.RESULT_STATUS_LIMIT_YEAR:
                        toastMsg = getString(R.string.register_count_year) + stringMultiResult.getInfo().get(WriteRegistrationInfoPresenter.YEAR_COUNT) + getString(R.string.count);
                        break;
                }
                if (toastMsg != null)
                    getVu().showToast(toastMsg);
            }

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }.registerLoadingMsg(getString(R.string.register)));
    }
}
