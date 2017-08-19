package com.xdja.imp.data.entity;

/**
 * <p>Summary:置顶设置</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.entity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:10:36</p>
 */
public class SessionTopSetter {

    /**
     * 账号
     */
    private String account;

    private String sessionId;



    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "SessionTopSetter{" +
                "account='" + account + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
