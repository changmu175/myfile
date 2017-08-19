package com.xdja.presenter_mainframe.bean;

/**
 * Created by ALH on 2016/8/12.
 */
public class SelectUploadImageBean {
    /**
     * 类型
     */
    private int type;
    /**
     * 参数
     */
    private Object object;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "SelectUploadImageBean{" +
                "type=" + type +
                ", object=" + object +
                '}';
    }
}
