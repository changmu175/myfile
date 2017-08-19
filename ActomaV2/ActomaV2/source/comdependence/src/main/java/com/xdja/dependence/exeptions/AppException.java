package com.xdja.dependence.exeptions;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 终端异常基类
 */
public class AppException extends RuntimeException {
    public AppException() {
    }

    public AppException(String detailMessage) {
        super(detailMessage);
    }

    public AppException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AppException(Throwable throwable) {
        super(throwable);
    }
}
