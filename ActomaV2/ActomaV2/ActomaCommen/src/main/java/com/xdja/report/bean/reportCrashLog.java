package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportCrashLog {
    /**
     * 帐号
     */
    private String account;
    /**
     * 安全卡ID
     */
    private String cardid;
    /**
     * 设备名称，ACE,EH880 等
     */
    private String devicename;
    /**
     * 设备IMEI
     */
    private String imei;
    /**
     * 操作系统版本
     */
    private String osversion;
    /**
     * 操作系统类型
     * 1 Android
     */
    private String ostype;
    /**
     * 应用版本
     */
    private String appversion;
    /**
     * 应用标识，如：AT(安通+)
     */
    private String appid;
    /**
     * 出现异常时间
     */
    private long crashtime;
    /**
     * 异常调用栈
     */
    private String content;

    public void setAccount(String account) {
        this.account = account;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public void setOstype(String ostype) {
        this.ostype = ostype;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setCrashtime(long crashtime) {
        this.crashtime = crashtime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public String getCardid() {
        return cardid;
    }

    public String getDevicename() {
        return devicename;
    }

    public String getImei() {
        return imei;
    }

    public String getOsversion() {
        return osversion;
    }

    public String getOstype() {
        return ostype;
    }

    public String getAppversion() {
        return appversion;
    }

    public String getAppid() {
        return appid;
    }

    public long getCrashtime() {
        return crashtime;
    }

    public String getContent() {
        return content;
    }
}
