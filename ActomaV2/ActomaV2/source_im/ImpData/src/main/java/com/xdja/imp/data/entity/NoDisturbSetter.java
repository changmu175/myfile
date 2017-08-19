package com.xdja.imp.data.entity;

import com.xdja.imp.domain.model.ConstDef;

/**
 * <p>Summary:免打扰设置</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.entity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:10:36</p>
 */
public class NoDisturbSetter {
//    account”:””,//账号
//            “sessionId”:””, //会话ID，当类型为1时用对方安通帐号标识，当类型为2时用群ID标识
//            “sessionType”:””// 1-单人会话，2-群组会话
    /**
     * 账号
     */
    private String account;
    /**
     * 会话ID，当类型为1时用对方安通帐号标识，当类型为2时用群ID标识
     */
    private String sessionId;

    @ConstDef.NoDisturbSettingSessionType
    private int sessionType;

    private String sessionFlag;

    public String getSessionFlag() {
        return sessionFlag;
    }

    public void setSessionFlag(String sessionFlag) {
        this.sessionFlag = sessionFlag;
    }

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

    @ConstDef.NoDisturbSettingSessionType
    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(@ConstDef.NoDisturbSettingSessionType int sessionType) {
        this.sessionType = sessionType;
    }

    @Override
    public String toString() {
        return "NoDisturbConfig{" +
                "account='" + account + '\'' +
                ", sessionId=" + sessionId +
                ", sessionType=" + sessionType +
                ", sessionFlag='" + sessionFlag + '\'' +
                '}';
    }
}
