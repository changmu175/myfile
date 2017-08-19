package com.xdja.comm.https;

/**
 * Created by THZ on 2015/7/8.
 * 终端处理返回的错误信息体
 */
public class HttpErrorBean extends ErrorBean {

    /**
     * 状态码
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
