package com.xdja.dependence.exeptions;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 网络异常定义
 */
public class NetworkException extends OkException {

    public static final String CODE_SSLHANDLE_FAILD = "sslHandleFaild";
    public static final String CODE_NETWORK_CONN_FAILD = "networkConnFaild";
    //modify by xnn@xdja.com to fix bug: 1277 2016-07-08 start (rummager : wangchao1)
    public static final String CODE_UNEXPECTED_END_OF_STREAM = "unexpectedEndOfStream";
    private int resId = -1;

    public NetworkException(String okCode) {
        super(okCode);
        resId = -1;
    }

    public int getResId(){
        return resId;
    }

    public NetworkException(String okCode, int id) {
        super(okCode);
        resId = id;
    }

    public NetworkException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
        resId = -1;
    }

    public NetworkException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
        resId = -1;
    }

    public NetworkException(Throwable throwable, String okCode) {
        super(throwable, okCode);
        resId = -1;
    }
}
