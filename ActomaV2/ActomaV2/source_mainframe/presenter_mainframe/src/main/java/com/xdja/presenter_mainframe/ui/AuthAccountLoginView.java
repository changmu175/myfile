package com.xdja.presenter_mainframe.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AuthAccountLoginCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAuthAccountLogin;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/5/12.
 */
@ContentView(R.layout.activity_auth_account_login)
public class AuthAccountLoginView extends ActivityView<AuthAccountLoginCommand> implements VuAuthAccountLogin {
    @Bind(R.id.tv_auth_account_login_first_text)
    TextView tvAuthAccountLoginFirstText;
    @Bind(R.id.tv_auth_account_login_second_text)
    TextView tvAuthAccountLoginSecondText;
    @Bind(R.id.btn_auth_account_login_certain)
    Button btnAuthAccountLoginCertain;

    @Override
    public void onCreated() {
        super.onCreated();
        if (btnAuthAccountLoginCertain != null)
            btnAuthAccountLoginCertain.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setAccountAndCardNo(String account, String cardNo) {
        if (TextUtils.isEmpty(account)||TextUtils.isEmpty(cardNo)){
            return;
        }
        //modify by alh@xdja.com to fix bug: 545 2016-06-30 start (rummager : wangchao1)
        if (btnAuthAccountLoginCertain != null)
            btnAuthAccountLoginCertain.setVisibility(View.VISIBLE);
        //modify by alh@xdja.com to fix bug: 545 2016-06-30 end (rummager : wangchao1)
        tvAuthAccountLoginFirstText.setText(String.format(getStringRes(R.string.authorization_login) , account , cardNo));
        tvAuthAccountLoginSecondText.setText(String.format(getStringRes(R.string.authorization_login_content) , cardNo));
    }

    @OnClick(R.id.btn_auth_account_login_certain)
    void certain(){
        getCommand().certain();
    }

    @Override
    protected int getToolbarType() {
        return ActivityView.ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_auth_account_Login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
