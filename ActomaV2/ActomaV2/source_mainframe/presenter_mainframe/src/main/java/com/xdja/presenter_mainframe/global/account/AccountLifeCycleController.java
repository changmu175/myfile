package com.xdja.presenter_mainframe.global.account;

import com.securevoipcommon.VoipAccountLifeCycle;
import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.blade.accountLifeCycle.CommonAccountLifeCycle;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contactcommon.ContactAccountLifeCycle;
import com.xdja.imp.IMAccountLifeCycle;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by ldy on 16/6/2.
 */
public class AccountLifeCycleController implements AccountLifeCycle {
    private Set<AccountLifeCycle> accountLifeCycles;

    @Inject
    public AccountLifeCycleController() {
        initAccountLifeCycles();
    }

    private void initAccountLifeCycles(){
        if (accountLifeCycles == null) {
            accountLifeCycles = new HashSet<>();
        }else {
            accountLifeCycles.clear();
        }
        //[S]add by tangsha@20161121 for 6120
        GroupInternalService.setInstanceToEmpty();
        //[E]add by tangsha@20161121 for 6120
        accountLifeCycles.add(new CommonAccountLifeCycle());
        accountLifeCycles.add(new ContactAccountLifeCycle());//add by wal@xdja.com 初始化联系人
        accountLifeCycles.add(new IMAccountLifeCycle());
        accountLifeCycles.add(new VoipAccountLifeCycle());
    }

    /**
     * 登录成功时调用
     */
    @Override
    public void login() {
        if (isNullAccountLifeCycles()) {
            return;
        }
        for (AccountLifeCycle accountLifeCycle : accountLifeCycles) {
            accountLifeCycle.login();
        }
    }

    /**
     * 登录成功后，如果新登录账号与原账号不同，调用
     */
    @Override
    public void accountChange() {
        if (isNullAccountLifeCycles()) {
            return;
        }
        for (AccountLifeCycle accountLifeCycle : accountLifeCycles) {
            accountLifeCycle.accountChange();
        }
    }

    /**
     * 登出时调用
     */
    @Override
    public void logout() {
        if (isNullAccountLifeCycles()) {
            return;
        }
        for (AccountLifeCycle accountLifeCycle : accountLifeCycles) {
            accountLifeCycle.logout();
        }
    }

    private boolean isNullAccountLifeCycles(){
        if (accountLifeCycles==null||accountLifeCycles.isEmpty()){
            return true;
        }else
            return false;
    }
}
