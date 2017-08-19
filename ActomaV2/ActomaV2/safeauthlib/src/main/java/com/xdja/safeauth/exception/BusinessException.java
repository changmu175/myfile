package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/18.
 * 业务处理异常
 */
public class BusinessException extends SafeAuthException {

    /**
     * 构造方法
     * @param errorCode
     * @param errorMsg
     */
    public BusinessException(String errorCode, String errorMsg, String mark) {
        super(errorCode, errorMsg, mark);
    }
}
