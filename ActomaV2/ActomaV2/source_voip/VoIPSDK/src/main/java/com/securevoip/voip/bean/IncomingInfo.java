package com.securevoip.voip.bean;

public class IncomingInfo {

    /**
     * 主叫方的账户标识
     * 因为暂时不支持多账户，所以在构造bean时，这个是固定值1
     * UserId
     */
    private String userId;
    /**
     * 被叫方
     * 电话号码
     */
    private String phoneNum;

    public IncomingInfo() {

    }

    public IncomingInfo(String phoneNum) {
        this.phoneNum = phoneNum;
        this.userId = "1";
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}