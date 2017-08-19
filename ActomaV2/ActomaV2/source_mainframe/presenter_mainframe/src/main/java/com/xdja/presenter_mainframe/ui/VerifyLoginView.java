package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.Button;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.VerifyLoginCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuVerifyLogin;
import com.xdja.presenter_mainframe.widget.PartClickTextView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/15.
 */
@ContentView(R.layout.activity_verify_login)
public class VerifyLoginView extends ActivityView<VerifyLoginCommand> implements VuVerifyLogin {

    @Bind(R.id.pctv_verify_login_not_mobile)
    PartClickTextView pctvVerifyLoginNotMobile;
    @Bind(R.id.btn_verify_login_verify)
    Button btnVerifyLoginVerify;
    private boolean isHaveMobile;

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void isHaveMobile(boolean isHaveMobile) {
        this.isHaveMobile = isHaveMobile;
        if (isHaveMobile) {
            pctvVerifyLoginNotMobile.setVisibility(View.VISIBLE);
            btnVerifyLoginVerify.setText(getStringRes(R.string.verify_login_verify_code));
        }else {
            pctvVerifyLoginNotMobile.setVisibility(View.GONE);
            btnVerifyLoginVerify.setText(getStringRes(R.string.verify_login_verify_friend_phone));
        }
    }

    @OnClick(R.id.btn_verify_login_verify)
    void verify(){
        if (isHaveMobile){
            getCommand().phoneVerifyCodeVerify();
        }else {
            getCommand().friendPhoneVerify();
        }
    }

    @OnClick(R.id.pctv_verify_login_not_mobile)
    void notMobile(){
        getCommand().friendPhoneVerify();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_verify_login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
