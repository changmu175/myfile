package com.xdja.comm.event;

/**
 * 描述当前类的作用
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-23 16:13
 */
public class UpdateAccountEvent {
    private String newAccount;

    public String getNewAccount() {
        return newAccount;
    }

    public void setNewAccount(String newAccount) {
        this.newAccount = newAccount;
    }
}
