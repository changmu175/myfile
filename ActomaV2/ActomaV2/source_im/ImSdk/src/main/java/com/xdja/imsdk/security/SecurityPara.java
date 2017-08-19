package com.xdja.imsdk.security;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：加解密参数                        <br>
 * 创建时间：2016/11/28 17:36                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SecurityPara {
    /**
     * 消息id
     */
    private long msgId;
    /**
     * 当前账号
     */
    private String user;
    /**
     * 聊天的对方账号，群聊为群号
     */
    private String person;
    /**
     * 是否是群组
     */
    private boolean isGroup;

    public SecurityPara() {
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
