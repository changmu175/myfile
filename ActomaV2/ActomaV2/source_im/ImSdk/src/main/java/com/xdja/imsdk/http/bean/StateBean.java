package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  状态消息内容结构                                <br>
 * 创建时间：2016/11/27 下午5:10                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class StateBean {
    /**
     * 状态消息内容
     * 是状态消息对应的普通消息的服务器id
     */
    private String c;

    /**
     * 状态消息对应的普通消息的发送者
     */
    private String f;

    /**
     * 状态消息对应的普通消息的接收者
     */
    private String to;

    /**
     * 状态消息对应的普通消息的状态
     */
    private int stat;

    /**
     * 状态消息类型
     */
    private int t;

    public StateBean() {

    }

    public StateBean(String f, String to, String c, int stat) {
        this.f = f;
        this.to = to;
        this.c = c;
        this.stat = stat;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
