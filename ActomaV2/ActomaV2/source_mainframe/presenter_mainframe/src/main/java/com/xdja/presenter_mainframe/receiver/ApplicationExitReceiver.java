package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;

public class ApplicationExitReceiver extends BroadcastReceiver {
    public ApplicationExitReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogoutHelper logoutHelper = new LogoutHelper();
        if (!logoutHelper.logout(null)) {
            logoutHelper.forceLogout();
        }
        boolean exitForTFOut = intent.getBooleanExtra(BasePresenterActivity.EXIT_TF_OUT_KEY, false);
        LogUtil.getUtils().e("ApplicationExitReceiver exitForTFOut "+exitForTFOut);
        if(exitForTFOut == false) {
            logoutHelper.navigateToLoginWithExit(context); //modify by xnn for bug 9664 review by tangsha
        }else{
            ActivityStack.getInstanse().exitApp();
        }
    }
}
