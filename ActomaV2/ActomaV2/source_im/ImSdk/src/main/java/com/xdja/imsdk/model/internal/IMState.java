package com.xdja.imsdk.model.internal;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：状态消息消息体                     <br>
 * 创建时间：2016/12/3 19:06                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class IMState {
    private long id;
    private String content;
    private long sendTime;

    public IMState() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "IMState{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
