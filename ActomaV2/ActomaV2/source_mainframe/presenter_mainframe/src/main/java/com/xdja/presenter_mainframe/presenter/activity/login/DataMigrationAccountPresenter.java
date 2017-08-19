package com.xdja.presenter_mainframe.presenter.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.Account;
import com.xdja.presenter_mainframe.cmd.DataMigrationAccountCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.DataMigrationAccountView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDataMigrationAccount;

/**
 * Created by ALH on 2016/8/30.
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class DataMigrationAccountPresenter extends PresenterActivity<DataMigrationAccountCommand,
        VuDataMigrationAccount> implements DataMigrationAccountCommand {

    private String mAccount;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        mAccount = getIntent().getStringExtra(Account.ACCOUNT);
        getVu().setAccount(mAccount);
    }

    @NonNull
    @Override
    protected Class<? extends VuDataMigrationAccount> getVuClass() {
        return DataMigrationAccountView.class;
    }

    @NonNull
    @Override
    protected DataMigrationAccountCommand getCommand() {
        return this;
    }

    @Override
    public void complete() {
        try {
            Intent intent = new Intent(this, DataMigrationFixPwdPresenter.class);
            intent.putExtra(Account.ACCOUNT, mAccount);
            startActivity(intent);
        } finally {
            finish();
        }
    }
}
