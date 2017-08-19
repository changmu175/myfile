package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportLoginCount {
    /**
     * 安通+帐号
     */
    private String account;
    /**
     * 安全卡 Id
     */
    private String deviceid;
    /**
     * 上报时间
      */
    private long time;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
