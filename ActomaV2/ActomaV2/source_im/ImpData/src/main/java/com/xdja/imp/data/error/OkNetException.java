package com.xdja.imp.data.error;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.data.entity.RespErrorBean;
import com.xdja.imp_data.R;

/**
 * <p>Summary:网络错误通用类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error.net</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:14:37</p>
 */
public class OkNetException extends OkException {
    public static final String UNKNOW_HTTP_CODE = "unknowCode";
    public static final String UNKNOW_HTTP_MSG = ActomaController.getApp().getString(R.string.im_unknow_http_error);

    public static final int HTTP_400_ERROR_CODE = 400;
    /**
     * HTTP401响应错误
     */
    public static final int HTTP_401_ERROR_CODE = 401;
    /**
     * HTTP500响应错误
     */
    public static final int HTTP_500_ERROR_CODE = 500;

    /**
     * 将原始错误信息转化为可识别的错误对象
     *
     * @param source 原始错误信息
     * @param <R>    原始错误信息类型
     * @return 可识别的错误对象
     */
    @Nullable
    public static <R> OkNetException buildException(@Nullable R source) {
        if (source == null) {
            return null;
        }

        OkNetException exception;

        if (source instanceof RespErrorBean) {
            RespErrorBean respErrorBean = ((RespErrorBean) source);
            if (TextUtils.isEmpty(respErrorBean.getErrCode())) {
                exception = buildDefaultException();
            } else {
                exception = new OkNetException();
                exception.setOkCode(respErrorBean.getErrCode());
                exception.setOkMessage(respErrorBean.getMessage());
                exception.setStatueCode(respErrorBean.getStatueCode());
            }
            return exception;
        }
        return null;
    }

    /**
     * 创建已知的异常对象
     *
     * @return 异常对象
     */
    //fix bug 2318 by licong,review by zya, 2016/8/5
   /* public static OkNetException buildDefaultException(RetrofitError cause) {
        OkNetException exception = new OkNetException();
        exception.setOkMessage(cause.getMessage());
        return exception;
    }*///end

    /**
     * 创建默认的异常对象
     *
     * @return 异常对象
     */
    public static OkNetException buildDefaultException() {
        OkNetException exception = new OkNetException();
        exception.setOkCode(OkNetException.UNKNOW_HTTP_CODE);
        exception.setOkMessage(OkNetException.UNKNOW_HTTP_MSG);
        return exception;
    }

    /**
     * http状态码
     */
    private int statueCode;

    /**
     * @return {@link #statueCode}
     */
    public int getStatueCode() {
        return statueCode;
    }

    /**
     * @param statueCode {@link #statueCode}
     */
    public void setStatueCode(int statueCode) {
        this.statueCode = statueCode;
    }

    public OkNetException() {
        super(new OkNetMatcher());
    }

    public OkNetException(String okCode, String okMessage) {
        super(new OkNetMatcher(), okCode, okMessage);
    }
}
