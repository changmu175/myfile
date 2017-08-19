package com.xdja.dependence.exeptions;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 客户端本地异常定义，如：数据库异常、Json转换异常等
 */
public class ClientException extends OkException {

    public static final String CODE_UNKOWN_EXCEPTION = "unknowException";

    public ClientException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }

    public ClientException(String okCode) {
        super(okCode);
    }

    public ClientException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public ClientException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }
}
