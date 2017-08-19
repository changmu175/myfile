package com.xdja.domain_mainframe.model;

/**
 * Created by xdjaxa on 2016/12/6.
 */
public class SafeLockBean {
    private String account;
    private int safeLock;
    private int screenLock;
    private int backgroundLock;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getBackgroundLock() {
        return backgroundLock;
    }

    public void setBackgroundLock(int backgroundLock) {
        this.backgroundLock = backgroundLock;
    }

    public int getScreenLock() {
        return screenLock;
    }

    public void setScreenLock(int screenLock) {
        this.screenLock = screenLock;
    }

    public int getSafeLock() {
        return safeLock;
    }

    public void setSafeLock(int safeLock) {
        this.safeLock = safeLock;
    }

    @Override
    public String toString() {
        return "SafeLockBean{" +
                "account='" + account + '\'' +
                ", safeLock=" + safeLock +
                ", screenLock=" + screenLock +
                ", backgroundLock=" + backgroundLock +
                '}';
    }
}
