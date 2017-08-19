package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：local_state_msg表实体类           <br>
 * 创建时间：2016/11/26 17:22                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class LocalStateMsgDb {
    private Long id;
    private Long sendTime;
    private String content;

    public LocalStateMsgDb() {
    }

    public LocalStateMsgDb(Long id, Long sendTime, String content) {
        this.content = content;
        this.sendTime = sendTime;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
