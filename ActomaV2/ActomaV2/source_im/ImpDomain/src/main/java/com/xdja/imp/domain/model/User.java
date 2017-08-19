package com.xdja.imp.domain.model;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/18</p>
 * <p>Time:13:33</p>
 */
public class User {
    /*
	 * 帐号
	 */
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                '}';
    }
}
