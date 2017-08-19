package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/19.
 */
public class CipErrorCode {

    /**
     * 读取安全芯片id为空
     */
    public static final String CIP_CARID_NULL = "-301";

    /**
     * 本地生成签名生成的结构体是空
     * */
    public static final String GET_TICKET_DATA_ERROR = "-302";

    /**
     * PIN码是空
     */
    public static final String PIN_IS_NULL = "-303";
}
