package com.xdja.imsdk.db.bean;

import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.State;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：msg_entry表实体类                 <br>
 * 创建时间：2016/11/26 17:22                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class MsgEntryDb {
    private Long id;
    private Long server_id;
    private String sender;
    private String receiver;
    private String card_id;
    private Integer type;
    private String content;
    private Integer state;
    private String session_flag;
    private Integer attr;
    private Integer life_time;
    private Long create_time;
    private Long sent_time;
    private Long sort_time;

    public MsgEntryDb() {
    }

    public MsgEntryDb(Long id, Long server_id, String sender,
                      String receiver, String card_id, Integer type,
                      String content, Integer state, String session_flag,
                      Integer attr, Integer life_time, Long create_time,
                      Long sent_time, Long sort_time) {
        this.id = id;
        this.server_id = server_id;
        this.sender = sender;
        this.receiver = receiver;
        this.card_id = card_id;
        this.type = type;
        this.content = content;
        this.state = state;
        this.session_flag = session_flag;
        this.attr = attr;
        this.life_time = life_time;
        this.create_time = create_time;
        this.sent_time = sent_time;
        this.sort_time = sort_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServer_id() {
        return server_id;
    }

    public void setServer_id(Long server_id) {
        this.server_id = server_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getSession_flag() {
        return session_flag;
    }

    public void setSession_flag(String session_flag) {
        this.session_flag = session_flag;
    }

    public Integer getAttr() {
        return attr;
    }

    public void setAttr(Integer attr) {
        this.attr = attr;
    }

    public Integer getLife_time() {
        return life_time;
    }

    public void setLife_time(Integer life_time) {
        this.life_time = life_time;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getSent_time() {
        return sent_time;
    }

    public void setSent_time(Long sent_time) {
        this.sent_time = sent_time;
    }

    public Long getSort_time() {
        return sort_time;
    }

    public void setSort_time(Long sort_time) {
        this.sort_time = sort_time;
    }

    /**
     * 消息是否本账号发送的
     * @return boolean
     */
    public boolean isSent() {
        if ((attr & Constant.MSG_DIRECTION) != Constant.MSG_DIRECTION) {
            return true;
        }
        return false;
    }

    /**
     * 消息是接收到的新消息
     * @return boolean
     */
    public boolean isNew() {
        return attr == Constant.MSG_REC_NEW;
    }

    /**
     * 消息是否是文件消息
     * @return boolean
     */
    public boolean isFile() {
        if ((type & MsgType.MSG_TYPE_FILE) == MsgType.MSG_TYPE_FILE) {
            return true;
        }
        return false;
    }

    /**
     * 消息是否是网页消息
     * @return boolean
     */
    public boolean isWeb() {
        if ((type & MsgType.MSG_TYPE_WEB) == MsgType.MSG_TYPE_WEB ) {
            return true;
        }
        return false;
    }

    /**
     * 是否被销毁
     * @return boolean
     */
    public boolean boomed() {
        return state == State.BOMB;
    }

    /**
     * 消息是否是文本消息
     * @return boolean
     */
    public boolean isText() {
        if ((type & MsgType.MSG_TYPE_TEXT) == MsgType.MSG_TYPE_TEXT) {
            return true;
        }
        return false;
    }

    /**
     * 消息是否是群组消息
     * @return boolean
     */
    public boolean isGroup() {
        if ((type & MsgType.MSG_TYPE_GROUP) == MsgType.MSG_TYPE_GROUP) {
            return true;
        }

        return false;
    }

    /**
     * 消息是否是自定义通知消息
     * @return boolean
     */
    public boolean isCustom() {
        if ((type & MsgType.MSG_TYPE_NOTICE) == MsgType.MSG_TYPE_NOTICE) {
            return true;
        }

        return false;
    }


    /**
     * 消息是否已加密
     * @return boolean
     */
    public boolean notEncrypt() {
        if (state == State.DEFAULT || state == State.ENCRYPT_FAIL) {
            return true;
        } else {
            return false;
        }
    }
}
