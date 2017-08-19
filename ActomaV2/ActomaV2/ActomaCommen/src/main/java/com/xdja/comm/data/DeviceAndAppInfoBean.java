package com.xdja.comm.data;

/**
 * Created by XURJ on 2015/12/24.
 */
public class DeviceAndAppInfoBean {
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

}
