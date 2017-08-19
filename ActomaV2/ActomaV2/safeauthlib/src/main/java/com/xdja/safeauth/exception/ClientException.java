package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/18.
 * 终端客户端处理的异常
 */
public class ClientException extends SafeAuthException {


    public ClientException(String errorCode, String message, String mark){
        super(errorCode, message, mark);
    }
}
