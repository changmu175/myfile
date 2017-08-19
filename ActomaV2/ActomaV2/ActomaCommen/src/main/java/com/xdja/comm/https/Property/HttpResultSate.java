package com.xdja.comm.https.Property;

/**
 * Created by gbc on 2016/10/21.
 */
public enum HttpResultSate {
    /**
     *失败
     */
    FAIL(0),

    /**
     * 成功
     */
    SUCCESS(1);

    private int type;

    public int getType() {
        return type;
    }
    HttpResultSate(int type) {// modified by ycm for lint
        this.type = type;

    }
}
