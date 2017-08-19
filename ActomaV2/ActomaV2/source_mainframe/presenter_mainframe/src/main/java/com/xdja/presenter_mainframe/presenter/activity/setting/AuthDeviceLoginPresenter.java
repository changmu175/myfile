package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AuthDeviceLoginCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.AuthDeviceLoginView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAuthDeviceLogin;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class AuthDeviceLoginPresenter extends PresenterActivity<AuthDeviceLoginCommand, VuAuthDeviceLogin> implements AuthDeviceLoginCommand {
    /**
     * 扫一扫requestcode
     */
    private static final int SCAN_TAG = 100;

    @NonNull
    @Override
    protected Class<? extends VuAuthDeviceLogin> getVuClass() {
        return AuthDeviceLoginView.class;
    }

    @NonNull
    @Override
    protected AuthDeviceLoginCommand getCommand() {
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
    public void scanQr() {
        Navigator.navigateToCaptureActivity();
    }

    @Override
    public void certain(String authorizeId) {
        authDevice(authorizeId);
    }

    private void authDevice(@NonNull String authorizeId) {
        if (NetworkUtil.isNetworkConnect(getApplicationContext())) {
            Navigator.navigateToAuthAccountLogin(authorizeId);
        }else {
            getVu().showToast(R.string.netNotWork);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_TAG) {
            if (Navigator.handleScanResultEvent(data) == Navigator.SCAN_ERROR_CODE_INVALID){
                XToast.show(ActomaApplication.getInstance(), getString(R.string.invalid_qrcode));
            }
        }
    }

}
