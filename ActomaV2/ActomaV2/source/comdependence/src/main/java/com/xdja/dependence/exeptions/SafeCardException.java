package com.xdja.dependence.exeptions;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 卡操作异常定义
 */
public class SafeCardException extends OkException {

    public static final String ERROR_UNKNOWN = "unKnownError";

    public static final String ERROR_GETCARDID_FAILD = "getCardIdFaild";

    public SafeCardException(String okCode) {
        super(okCode);
    }

    public SafeCardException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public SafeCardException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }

    public SafeCardException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }
}
