package com.xdja.imsdk.http.param;

import com.xdja.imsdk.http.bean.MsgBean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 16:56                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class MsgPara {
    private String user;
    private String ticket;
    private String flagid;
    private MsgBean content;

    public MsgPara(String user, String ticket, MsgBean content, String flagid) {
        super();
        this.user = user;
        this.ticket = ticket;
        this.content = content;
        this.flagid = flagid;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the content
     */
    public MsgBean getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(MsgBean content) {
        this.content = content;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    @Override
    public String toString() {
        return "SendImMsgParams{" +
                "user='" + user + '\'' +
                ", ticket='" + ticket + '\'' +
                ", flagid='" + flagid + '\'' +
                ", content=" + content +
                '}';
    }
}
