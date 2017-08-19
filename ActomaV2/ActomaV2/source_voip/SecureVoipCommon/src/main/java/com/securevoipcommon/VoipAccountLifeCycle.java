package com.securevoipcommon;

import android.content.Context;

import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by gbc on 2016/7/21.
 */
public class VoipAccountLifeCycle implements AccountLifeCycle {
    /**
     * 登录成功时调用
     */
    @Override
    public void login() {
        Context context = ActomaController.getApp().getApplicationContext();
        AccountBean accountBean = AccountServer.getAccount();
        if(accountBean == null) {
            LogUtil.getUtils("VoipAccountLifeCycle").d("get accountBean is null");
            return;
        }

        String account = accountBean.getAccount();
        String ticket = PreferencesServer.getWrapper(context).gPrefStringValue("ticket");
        VoipFunction.getInstance().initAccount(context, account,ticket);
        VoipFunction.getInstance().initPush(context);
    }

    /**
     * 登录成功后，如果新登录账号与原账号不同，调用
     */
    @Override
    public void accountChange() {

    }

    /**
     * 登出时调用
     */
    @Override
    public void logout() {

    }
}
