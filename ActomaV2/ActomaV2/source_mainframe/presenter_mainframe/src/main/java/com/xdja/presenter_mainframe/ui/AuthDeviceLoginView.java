package com.xdja.presenter_mainframe.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AuthDeviceLoginCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAuthDeviceLogin;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.inputView.ButtonTextInputView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_auth_device_login)
public class AuthDeviceLoginView extends ActivityView<AuthDeviceLoginCommand> implements VuAuthDeviceLogin {

    @Bind(R.id.inputView_auth_device_login_serial_number)
    ButtonTextInputView inputViewAuthDeviceLoginSerialNumber;
    @Bind(R.id.btn_auth_device_login_certain)
    Button btnAuthDeviceLoginCertain;
    @Bind(R.id.btn_auth_device_login_scan)
    Button btnAuthDeviceLoginScan;

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void onCreated() {
        super.onCreated();
        btnAuthDeviceLoginCertain.setEnabled(false);
        inputViewAuthDeviceLoginSerialNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isRuleAuthorizeId(s.toString())) {
                    btnAuthDeviceLoginCertain.setEnabled(true);
                }else {
                    btnAuthDeviceLoginCertain.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.btn_auth_device_login_certain)
    public void certain() {
        getCommand().certain(inputViewAuthDeviceLoginSerialNumber.getInputText());
    }

    @OnClick(R.id.btn_auth_device_login_scan)
    public void scan() {
        getCommand().scanQr();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_auth_device_login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
