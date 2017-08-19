package com.xdja.imsdk.model;

import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.model.body.IMMessageBody;

/**
 * 项目名称：ImSdk              <br>
 * 类描述  ：消息体数据模型       <br>
 * 创建时间：2016/11/16 15:13   <br>
 * 修改记录：                   <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class IMMessage {
    /**
     * 消息体的id
     */
    private long messageId;

    /**
     * 消息发送方的身份标识, 例如AT+账号
     */
    private String from;

    /**
     * 消息发送方的芯片卡id
     */
    private String cardId;

    /**
     * 消息接收方的身份标识, 例如AT+账号
     */
    private String to;

    /**
     * 消息的类型，为32位整数:XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX     <br>
     * 从右至左开始，依次为0~31位，默认为0。                            <br>
     * ImSdk中已使用的各bit位的规定如下：                              <br>
     * 第一位：文本类型                                              <br>
     * 第二位：文件类型                                              <br>
     * 第三位：群组类型                                              <br>
     * 第四位：闪信类型                                              <br>
     * 第五位：状态类型                                              <br>
     * 剩余位为保留位，暂未使用，用户可自定义类型                        <br>
     * 在使用过程中，保留位需设置为0。
     * @see MsgType
     */
    private int type;

    /**
     * 消息状态
     * @see com.xdja.imsdk.constant.MsgState
     */
    private int state;

    /**
     * 消息的展示时间
     */
    private long messageTime;

    /**
     * 消息生存期                                                        <br>
     * 消息为闪信类型时，表示消息被查看后的timeToLive时间之后要销毁，单位是毫秒  <br>
     * 消息为非闪信类型时，默认为0
     */
    private int timeToLive;

    /**
     * 消息失败原因错误码
     * @see com.xdja.imsdk.constant.IMFailCode
     */
    private int failCode;

    /**
     * 消息内容
     */
    private IMMessageBody messageBody;

    /**
     * 构造方法
     */
    public IMMessage() {
    }

    /**
     * 构造方法
     * @param to 接收方账号
     * @param type 消息类型
     * @param messageBody 消息内容
     */
    public IMMessage(String to, int type, IMMessageBody messageBody) {
        this.to = to;
        this.type = type;
        this.messageBody = messageBody;
    }

    /**
     * 获取消息的id
     * @return {@link #messageId}
     */
    public long getIMMessageId() {
        return messageId;
    }

    /**
     * 设置消息的id
     * @param messageId 消息id
     */
    public void setIMMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取消息发送方
     * @return {@link #from}
     */
    public String getFrom() {
        return from;
    }

    /**
     * 设置消息发送方
     * @param from 发送方账号
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 获得发送方芯片卡Id
     * @return {@link #cardId}
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * 设置发送方芯片卡Id
     * @param cardId 芯片卡Id
     */
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    /**
     * 获取消息接收方
     * @return {@link #to}
     */
    public String getTo() {
        return to;
    }

    /**
     * 设置消息接收方
     * @param to 接收方账号
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 获取消息类型
     * @return {@link #type}
     */
    public int getType() {
        return this.type;
    }

    /**
     * 设置消息类型
     * @param type 消息类型
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取消息状态
     * @return {@link #state}
     * @see com.xdja.imsdk.constant.MsgState
     */
    public int getState() {
        return state;
    }

    /**
     * 设置消息状态
     * @param state 消息状态
     * @see com.xdja.imsdk.constant.MsgState
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获取消息时间
     * @return {@link #messageTime}
     */
    public long getIMMessageTime() {
        return messageTime;
    }

    /**
     * 设置消息时间
     * @param messageTime 消息时间
     */
    public void setIMMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * 获取消息存活时间
     * @return {@link #timeToLive}
     */
    public int getTimeToLive() {
        return timeToLive;
    }

    /**
     * 设置消息存活时间
     * @param timeToLive 存活时间
     */
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * 获取消息失败原因的错误码
     * @return 错误码
     */
    public int getFailCode() {
        return failCode;
    }

    /**
     * 设置消息失败原因的错误码
     * @param failCode 错误码
     */
    public void setFailCode(int failCode) {
        this.failCode = failCode;
    }

    /**
     * 获取消息内容
     * @return 消息内容
     * @see IMMessageBody
     */
    public IMMessageBody getMessageBody() {
        return messageBody;
    }

    /**
     * 设置消息内容
     * @param messageBody 消息内容
     * @see IMMessageBody
     */
    public void setMessageBody(IMMessageBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * 是否已销毁
     * @return boolean
     */
    public boolean isBomb() {
        return state == MsgState.MSG_STATE_BOMB;
    }

    /**
     * 是否已阅读
     * @return boolean
     */
    public boolean isRead() {
        return state == MsgState.MSG_STATE_READ;
    }

    /**
     * 判断是否是文本类型
     * @return boolean                                       <br>
     *         true: 是文本类型消息                            <br>
     *         false: 不是文本类型消息
     */
    public boolean isTextIMMessage() {
        if ((type & MsgType.MSG_TYPE_TEXT) == MsgType.MSG_TYPE_TEXT) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是文件类型
     * @return boolean                                       <br>
     *         true: 是文件类型消息                            <br>
     *         false: 不是文件类型消息
     */
    public boolean isFileIMMessage() {
        if ((type & MsgType.MSG_TYPE_FILE) == MsgType.MSG_TYPE_FILE) {
            return true;
        }
        return false;
    }
	
 	/**
     * 判断是否是网页类型
     * @return boolean                                       <br>
     *         true: 是网页类型消息                            <br>
     *         false: 不是网页类型消息
     */
    public boolean isWebIMMessage() {
        if ((type & MsgType.MSG_TYPE_WEB) == MsgType.MSG_TYPE_WEB) {
            return true;
        }
        return false;
    }

    /**
     * 消息是否是群组类型
     * @return boolean                                       <br>
     *         true: 是群组类型消息                            <br>
     *         false: 不是群组类型消息
     */
    public boolean isGroupIMMessage() {
        if ((type & MsgType.MSG_TYPE_GROUP) == MsgType.MSG_TYPE_GROUP) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是闪信类型
     * @return boolean                                       <br>
     *         true: 是闪信                                   <br>
     *         false: 不是闪信
     */
    public boolean isBombIMMessage() {
        if ((type & MsgType.MSG_TYPE_BOMB) == MsgType.MSG_TYPE_BOMB) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是已阅读的闪信
     * @return boolean
     */
    public boolean isReadBomb() {
        return isRead() && isBombIMMessage();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "messageId=" + messageId +
                ", from='" + from + '\'' +
                ", cardId='" + cardId + '\'' +
                ", to='" + to + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", messageTime=" + messageTime +
                ", timeToLive=" + timeToLive +
                ", failCode=" + failCode +
                ", messageBody=" + messageBody +
                '}';
    }
}
