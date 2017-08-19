package com.xdja.imsdk.model;

import com.xdja.imsdk.constant.IMSessionType;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：会话数据模型      <br>
 * 创建时间：2016/11/16 15:13  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class IMSession {
    /**
     * 会话id
     */
    private long id;

    /**
     * 聊天对象账号
     */
    private String imPartner;

    /**
     * 会话标识，一个会话的唯一性标志
     */
    private String sessionTag;

    /**
     * 会话类型
     * @see com.xdja.imsdk.constant.IMSessionType
     */
    private int sessionType;

    /**
     * 会话中新消息数量
     */
    private int remindCount;

    /**
     * 会话显示时间                              <br>
     * 新会话，无消息，为会话创建时间               <br>
     * 老会话，消息被清除，为清除前最后一条消息时间   <br>
     */
    private long displayTime;

    /**
     * 会话中最后一条消息
     * @see IMMessage
     */
    private IMMessage lastMessage;

    /**
     * 获取会话id
     * @return {@link #id}
     */
    public long getId() {
        return id;
    }

    /**
     * 设置会话id
     * @param id 会话id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取聊天对象
     * @return  {@link #imPartner}
     */
    public String getImPartner() {
        return imPartner;
    }

    /**
     * 设置聊天对象
     * @param imPartner 聊天对象
     */
    public void setImPartner(String imPartner) {
        this.imPartner = imPartner;
    }

    /**
     * 获取会话标识
     * @return {@link #sessionTag}
     */
    public String getSessionTag() {
        return sessionTag;
    }

    /**
     * 设置会话标识
     * @param sessionTag 会话标识
     */
    public void setSessionTag(String sessionTag) {
        this.sessionTag = sessionTag;
    }

    /**
     * 获取会话类型
     * @return {@link #sessionType}
     */
    public int getSessionType() {
        return sessionType;
    }

    /**
     * 设置会话类型
     * @param sessionType 会话类型
     */
    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    /**
     * 获取新消息数量
     * @return {@link #remindCount}
     */
    public int getRemindCount() {
        return remindCount;
    }

    /**
     * 设置新消息数量
     * @param count 新消息数量
     */
    public void setRemindCount(int count) {
        this.remindCount = count;
    }

    /**
     * 获取会话显示时间
     * @return {@link #displayTime}
     */
    public long getDisplayTime() {
        return displayTime;
    }

    /**
     * 设置会话显示时间
     * @param displayTime 会话显示时间
     */
    public void setDisplayTime(long displayTime) {
        this.displayTime = displayTime;
    }

    /**
     * 获取会话最后一条消息
     * @return {@link #lastMessage}
     */
    public IMMessage getLastMessage() {
        return lastMessage;
    }

    /**
     * 设置会话最后一条消息
     * @param message 消息
     */
    public void setLastMessage(IMMessage message) {
        this.lastMessage = message;
    }

    /**
     * 是否是群组会话
     * @return boolean
     */
    public boolean isGroup() {
        return sessionType == IMSessionType.SESSION_GROUP;
    }

    @Override
    public String toString() {
        return "IMSession{" +
                "id=" + id +
                ", imPartner='" + imPartner + '\'' +
                ", sessionTag='" + sessionTag + '\'' +
                ", sessionType=" + sessionType +
                ", remindCount=" + remindCount +
                ", displayTime=" + displayTime +
                ", lastMessage=" + lastMessage +
                '}';
    }
}
