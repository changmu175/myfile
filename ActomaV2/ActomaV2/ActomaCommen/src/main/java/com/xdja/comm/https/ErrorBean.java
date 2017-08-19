package com.xdja.comm.https;

/**
 * Created by THZ on 2015/7/9.
 * 服务返回错误信息机构体
 */
public class ErrorBean {
    /**
     * 主机id
     */
    private String hostId;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误信息
     */
    private String message;


    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
