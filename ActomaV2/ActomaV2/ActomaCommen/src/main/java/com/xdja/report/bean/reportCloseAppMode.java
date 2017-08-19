package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportCloseAppMode {
    /**
     * 帐号
     */
    private String account;
    /**
     * 安全卡ID
     */
    private String deviceid;
    /**
     * 是否接收IM消息标识
     */
    private int imflag;


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

    public int getImflag() {
        return imflag;
    }

    public void setImflag(int imflag) {
        this.imflag = imflag;
    }
}
