package com.xdja.presenter_mainframe.bean;

/**
 * Created by ldy on 16/5/3.
 */
public class DeviceInfoBean {
    private String cardNo;
    private String deviceName;
    private String status;
    /*[S]add by tangsha for ckms relieve device by ckms deviceId*/
    private String snNo;
    /*[E]add by tangsha for ckms relieve device by ckms deviceId*/

    public DeviceInfoBean(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceInfoBean(String cardNo, String deviceName, String snNo) {
        this.cardNo = cardNo;
        this.deviceName = deviceName;
        this.snNo = snNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getSnNo(){
        return snNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
