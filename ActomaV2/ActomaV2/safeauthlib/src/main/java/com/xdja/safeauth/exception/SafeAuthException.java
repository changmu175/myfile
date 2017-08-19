package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/18.
 */
public class SafeAuthException extends RuntimeException {
    /**
     * 错误状态码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 业务
     */
    private String mark;

    /**
     * 构造方法
     * @param errorCode
     * @param errorMsg
     */
    public SafeAuthException(String errorCode, String errorMsg, String mark) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.mark = mark;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getMark() {
        return mark;
    }

    public String toString() {
        return "{ errorCode : " + errorCode + " errorMsg :" + errorMsg + " mark :" + mark + "}\n";
    }
}
