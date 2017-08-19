package com.xdja.comm.blade.accountLifeCycle;

/**
 * Created by ldy on 16/6/2.
 * 账号生命周期
 */

// TODO: 2016/6/4 add by fjd 账户相关的生命周期是否应该放在业务代码中而不是主框架中？
public interface AccountLifeCycle {
    /**
     * 登录成功时调用
     */
    void login();

    /**
     * 登录成功后，如果新登录账号与原账号不同，调用
     */
    void accountChange();

    /**
     * 登出时调用
     */
    void logout();
}
