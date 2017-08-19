package com.xdja.comm.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xrj on 2015-12-23 14:06:50
 * 日志上传请求参数对象
 */
public class UpLoadLogRequestBean {

    /**
     * 应用标识
     */
    private String appId = "";

    /**
     * 应用版本
     */
    private String appVersion = "";

    /**
     * 操作系统类型
     * 1 Android
     */
    private int osType = 1;

    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 设备IMEI
     */
    private String imei = "";

    /**
     * 操作系统版本
     */
    private String osVersion = "";

    /**
     * 帐号
     */
    private String account = "";

    /**
     * 硬件ID
     */
    private String cardId = "";


    private List<LogInfoBean> logBeans;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public List<LogInfoBean> getLogList() {
        return logBeans;
    }

    public void setLogList(List<LogInfoBean> logList) {
        this.logBeans = logList;
    }

    public void addLogInfo(LogInfoBean logInfoBean){
        if(logBeans == null){
            logBeans = new ArrayList<>();
        }

        logBeans.add(logInfoBean);
    }

    public void setDeviceAndAppInfoBean(DeviceAndAppInfoBean deviceAndAppInfoBean){
        appId = deviceAndAppInfoBean.getAppId();
        appVersion = deviceAndAppInfoBean.getAppVersion();
        osType = deviceAndAppInfoBean.getOsType();
        deviceName = deviceAndAppInfoBean.getDeviceName();
        imei = deviceAndAppInfoBean.getImei();
        osVersion = deviceAndAppInfoBean.getOsVersion();
    }
}

