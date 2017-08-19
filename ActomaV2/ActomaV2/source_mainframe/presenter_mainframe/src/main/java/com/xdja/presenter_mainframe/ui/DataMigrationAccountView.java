package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.DataMigrationAccountCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDataMigrationAccount;
import com.xdja.presenter_mainframe.util.TextUtil;

import butterknife.Bind;

/**
 * Created by ALH on 2016/8/30.
 */
@ContentView(R.layout.activity_data_migration_account)
public class DataMigrationAccountView extends ActivityView<DataMigrationAccountCommand> implements
        VuDataMigrationAccount {
    @Bind(R.id.account)
    TextView account;
    @Bind(R.id.warn)
    TextView warn;
    @Bind(R.id.at_account)
    TextView at_account;
    @Bind(R.id.next)
    Button next;

    @Override
    public void onCreated() {
        super.onCreated();
        warn.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG_RED, 0, 0, 0,
                getStringRes(R.string.old_account_prompt)));
        at_account.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0, 0, 0,
                getStringRes(R.string.at_account)));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().complete();
            }
        });
    }

    @Override
    public void setAccount(String acc) {
        account.setText(acc);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_DEFAULT;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
