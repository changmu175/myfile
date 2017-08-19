package com.xdja.contactcommon;

import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wal on 2016/7/22.
 */
public class ContactAccountLifeCycle implements AccountLifeCycle {

    private String TAG = "Actoma ContactAccountLifeCycle ";
    /**
     * 登录成功时调用
     */
    @Override
    public void login() {
        AccountBean accountBean = AccountServer.getAccount();
        if(accountBean == null) {
            LogUtil.getUtils().e(TAG+" login get accountBean is null");
            return;
        }
        if (ObjectUtil.stringIsEmpty(accountBean.getAccount())){
            LogUtil.getUtils().e(TAG+ "get account is null");
            return;
        }
        LogUtil.getUtils().d(TAG+"wallog ---contact-login in-----");
        ContactModuleProxy.initContactsModule();
    }

    /**
     * 登录成功后，如果新登录账号与原账号不同，调用
     */
    @Override
    public void accountChange() {
        LogUtil.getUtils().d(TAG+" wallog ---contact-login accountChange-----");
    }

    /**
     * 登出时调用
     */
    @Override
    public void logout() {
        LogUtil.getUtils().d(TAG+" wallog ---contact-login out-----");
        ContactUtils.contactLogoutAction();
    }
}
