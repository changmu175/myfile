package com.xdja.comm.blade.accountLifeCycle;

/**
 * Created by ldy on 16/6/2.
 */
public class CommonAccountLifeCycle implements AccountLifeCycle {
    /**
     * 登录成功时调用
     */
    @Override
    public void login() {

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
