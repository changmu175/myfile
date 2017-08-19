package com.xdja.dependence.exeptions;

import android.text.TextUtils;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 需要明确处理的异常，一般为业务异常，如：后台返回的业务异常，卡操作异常等
 */
public class OkException extends AppException {
    /**
     * 默认的异常错误码定义
     */
    private final String DEFAULT_CODE = getClass().getSimpleName() + "_DefaultCode";
    /**
     * 异常错误码
     */
    private String okCode = DEFAULT_CODE;

    /**
     * @return {@link #okCode}
     */
    public String getOkCode() {
        return okCode;
    }

    /**
     * @param okCode {@link #okCode}
     */
    public void setOkCode(String okCode) {
        this.okCode = okCode;
    }

    public OkException(String okCode) {
        if (!TextUtils.isEmpty(okCode)) {
            this.okCode = okCode;
        }
    }

    public OkException(String detailMessage, String okCode) {
        super(detailMessage);
        if (!TextUtils.isEmpty(okCode)) {
            this.okCode = okCode;
        }
    }

    public OkException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable);
        if (!TextUtils.isEmpty(okCode)) {
            this.okCode = okCode;
        }
    }

    public OkException(Throwable throwable, String okCode) {
        super(throwable);
        if (!TextUtils.isEmpty(okCode)) {
            this.okCode = okCode;
        }
    }

    @Override
    public String toString() {
        return "okCode : " + okCode + "\r\n" + super.toString();
    }
}
