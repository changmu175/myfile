package com.xdja.imsdk.http.param;

import com.xdja.imsdk.http.bean.Condition;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/9 17:04                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class PullPara {
    private String user;// 目标用户帐号
    private String cid;// 接收方芯片卡id
    private String startid;// 要获取的消息起始ID，不填或者-1则为获取最新消息
    private String size;// 获取的消息条数，-1表示获取全部消息
    private String ticket;// 云平台标识符
    private Condition condition;

    public PullPara(String user, String cid, String startid, String size,
                          String ticket, Condition condition) {
        super();
        this.user = user;
        this.cid = cid;
        this.startid = startid;
        this.size = size;
        this.ticket = ticket;
        this.condition = condition;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getStartid() {
        return startid;
    }

    public void setStartid(String startid) {
        this.startid = startid;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    /**
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("GetimmsgParams : ").append("\n");
        buffer.append("[user="+user).append("\n");
        buffer.append("cid:"+cid).append("\n");
        buffer.append("startid:"+startid).append("\n");
        buffer.append("size:"+size).append("\n");
        buffer.append("ticket:"+ticket).append("\n");
        if (condition != null) {
            buffer.append("Condition : {"+condition.toString()+"}");
        }
        buffer.append("]").append("\n");
        return buffer.toString();
    }
}
