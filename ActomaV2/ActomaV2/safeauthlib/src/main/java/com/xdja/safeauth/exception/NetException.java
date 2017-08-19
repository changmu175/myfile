package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/19.
 * 网络处理异常
 */
public class NetException extends SafeAuthException {


    /**
     * 构造方法
     *
     * @param errorCode
     * @param errorMsg
     */
    public NetException(String errorCode, String errorMsg, String mark) {
        super(errorCode, errorMsg, mark);
    }
}
