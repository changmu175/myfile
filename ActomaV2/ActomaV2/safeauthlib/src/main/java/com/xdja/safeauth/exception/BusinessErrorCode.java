package com.xdja.safeauth.exception;

/**
 * Created by THZ on 2016/5/19.
 */
public class BusinessErrorCode {

    /**
     * 服务器返回成功
     */
    public static final String SUCCESS = "1";

    /**
     * 参数异常
     */
    public static final String ERROR_PARAM = "2";

    /**
     * 卡或者证书异常
     */
    public static final String CARD_CERT_ERR = "3";

    /**
     * 验签失败
     */
    public static final String VERY_SIGN_ERR = "4";

    /**
     * 服务器返回为空
     */
    public static final String ERROR_EMPTY = "-101";

    /**
     * 服务器返回数据异常，不包含错误码 code
     */
    public static final String ERROR_SERVER_CODE = "-102";

    /**
     * 服务器内部异常，正常返回200，但是body没有数据
     */
    public static final String ERROR_SERVER_EMPTY = "-103";

    /**
     * 网络操作服务器返回中，未知的异常
     */
    public static final String UNKOWN_ERROR = "-104";

    /**
     * 服务器返回数据异常，有正确的code，不包含正确的内容
     */
    public static final String ERROR_CONTENT_DATA = "-105";

    /**
     * 本地保存ticket失败
     */
    public static final String SAVE_TICKET_ERR = "-106";
}
