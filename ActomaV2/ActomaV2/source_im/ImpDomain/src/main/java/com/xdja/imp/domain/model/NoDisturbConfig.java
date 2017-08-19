package com.xdja.imp.domain.model;

/**
 * <p>Summary:免打扰模式配置信息类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/10</p>
 * <p>Time:14:42</p>
 */
public class NoDisturbConfig {

//    sessionId”:””, //会话ID，当类型为1时用对方安通帐号标识，当类型为2时用群ID标识
//            “sessionType”:””// 1-单人会话，2-群组会话

    private String sessionId;

    private int sessionType;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }
}
