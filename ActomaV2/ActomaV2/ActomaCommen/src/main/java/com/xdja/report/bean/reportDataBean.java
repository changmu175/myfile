package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportDataBean<T> {
    /* version
       type
       subtype
       content
    */

    //客户端版本号
    private String version;

    //消息类型
    private int type;

    //消息子类型
    private int subtype;

    //消息内容（json格式）
    private T message;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
