package com.xdja.dependence.exeptions;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 校验性质的异常定义
 */
public class CheckException extends OkException {

    public static final String CODE_PARAMES_NOTVALID = "parms_notValid";
    public static final String CODE_ACCOUNT_NONE = "account_notValid";
    public static final String CODE_PASSWORD_NONE = "password_notValid";
    public static final String CODE_PASSWORD_FORMAT = "password_format";
    public static final String CODE_PASSWORD_DISCORD = "password_discord";
    public static final String DEVICE_ID_NOT_PROVIDE = "device_id_not_provide";

    public CheckException(String okCode) {
        super(okCode);
    }

    public CheckException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public CheckException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }

    public CheckException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }

    public static CheckException buildParamsCheckExec(@Nullable String detailMsg){
        return new CheckException(detailMsg,CheckException.CODE_PARAMES_NOTVALID);
    }
}
