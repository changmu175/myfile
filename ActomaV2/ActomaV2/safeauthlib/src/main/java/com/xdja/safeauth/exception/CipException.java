package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/18.
 * 安全芯片处理发生的错误异常
 */
public class CipException extends SafeAuthException {

    /**
     * 构造方法
     *
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    public CipException(int errorCode, String errorMsg) {
        super(errorCode + "", errorMsg, "");
    }
}
