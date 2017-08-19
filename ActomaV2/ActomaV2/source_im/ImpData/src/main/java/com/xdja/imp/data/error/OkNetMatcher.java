package com.xdja.imp.data.error;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp_data.R;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Summary:网络异常匹配</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/8</p>
 * <p>Time:11:10</p>
 */
public class OkNetMatcher implements OkMatcher<OkNetException> {
    /**
     * HTTP400响应错误
     */
    public static Map<String, String> HTTP_400_ERROR;
    public static final int HTTP_400_ERROR_CODE = 400;
    /**
     * HTTP401响应错误
     */
    public static Map<String, String> HTTP_401_ERROR;
    public static final int HTTP_401_ERROR_CODE = 401;
    /**
     * HTTP500响应错误
     */
    public static Map<String, String> HTTP_500_ERROR;
    public static final int HTTP_500_ERROR_CODE = 500;
    /**
     * 未在可控范围内的Http响应
     */
    public static Map<String, String> HTTP_UNKNOWN_ERROR;
    public static final int HTTP_UNKNOWN_ERROR_CODE = 0;


    public static Map<Integer, Map<String, String>> HTTP_ERROR_MAP;

    public static final String UNKNOW_HTTP_CODE = "unknowCode";
    public static final String UNKNOW_HTTP_USERMSG = ActomaController.getApp().getString(R.string.im_error_retry);

    //fix bug 2318 by licong,review by zya, 2016/8/5
    public static final String CODE_SSLHANDLE_FAILD = "CODE_SSLHANDLE_FAILD";
    public static final String USSLHANDLE_FAILD_USERMSG = ActomaController.getApp().getString(R.string.im_time_differ_remind);
    //end

    public static final String HTTP_400_ERROR_REQUEST_WITHOUT_TICKET
            = "request_without_ticket";
    public static final String HTTP_400_ERROR_MSG_REQUEST_WITHOUT_TICKET
            = ActomaController.getApp().getString(R.string.im_head_no_ticket_resource);
    public static final String HTTP_400_ERROR_REQUEST_WITHOUT_ACCOUNT_OR_CARDID
            = "request_without_account_or_cardid";
    public static final String HTTP_400_ERROR_MSG_REQUEST_WITHOUT_ACCOUNT_OR_CARDID
            = ActomaController.getApp().getString(R.string.im_request_no_id_data);
    public static final String HTTP_400_ERROR_WITHOUT_REQ_PARAMETER
            = "without_req_parameter";
    public static final String HTTP_400_ERROR_MSG_WITHOUT_REQ_PARAMETER
            = ActomaController.getApp().getString(R.string.im_request_field_null);
    public static final String HTTP_401_ERROR_INVALID_TICKET
            = "invalid_ticket";
    public static final String HTTP_401_ERROR_MSG_INVALID_TICKET
            = ActomaController.getApp().getString(R.string.im_ticket_validate_not_go);
    public static final String HTTP_500_ERROR_MXS_THRIFT_ERROR
            = "mxs_thrift_error";
    public static final String HTTP_500_ERROR_MSG_MXS_THRIFT_ERROR
            = ActomaController.getApp().getString(R.string.im_server_call_error);
    public static final String HTTP_500_ERROR_REQ_PARSE_ERROR
            = "req_parse_error";
    public static final String HTTP_500_ERROR_MSG_REQ_PARSE_ERROR
            = ActomaController.getApp().getString(R.string.im_request_field_error);


    static {
        HTTP_400_ERROR = new HashMap<>();

        // TODO: 2016/1/8 以下错误信息应该替换成为用户错误信息，目前还保留的是错误原始信息

        HTTP_400_ERROR.put(HTTP_400_ERROR_REQUEST_WITHOUT_TICKET,
                HTTP_400_ERROR_MSG_REQUEST_WITHOUT_TICKET);
        HTTP_400_ERROR.put(HTTP_400_ERROR_REQUEST_WITHOUT_ACCOUNT_OR_CARDID,
                HTTP_400_ERROR_MSG_REQUEST_WITHOUT_ACCOUNT_OR_CARDID);
        HTTP_400_ERROR.put(HTTP_400_ERROR_WITHOUT_REQ_PARAMETER,
                HTTP_400_ERROR_MSG_WITHOUT_REQ_PARAMETER);

        HTTP_401_ERROR = new HashMap<>();
        HTTP_401_ERROR.put(HTTP_401_ERROR_INVALID_TICKET,
                HTTP_401_ERROR_MSG_INVALID_TICKET);

        HTTP_500_ERROR = new HashMap<>();
        HTTP_500_ERROR.put(HTTP_500_ERROR_MXS_THRIFT_ERROR,
                HTTP_500_ERROR_MSG_MXS_THRIFT_ERROR);
        HTTP_500_ERROR.put(HTTP_500_ERROR_REQ_PARSE_ERROR,
                HTTP_500_ERROR_MSG_REQ_PARSE_ERROR);

        HTTP_UNKNOWN_ERROR = new HashMap<>();
        HTTP_UNKNOWN_ERROR.put(UNKNOW_HTTP_CODE, UNKNOW_HTTP_USERMSG);
        //fix bug 2318 by licong,review by zya, 2016/8/5
        HTTP_UNKNOWN_ERROR.put(CODE_SSLHANDLE_FAILD, USSLHANDLE_FAILD_USERMSG);//end

        HTTP_ERROR_MAP = new HashMap<>();
        HTTP_ERROR_MAP.put(HTTP_400_ERROR_CODE, HTTP_400_ERROR);
        HTTP_ERROR_MAP.put(HTTP_401_ERROR_CODE, HTTP_401_ERROR);
        HTTP_ERROR_MAP.put(HTTP_500_ERROR_CODE, HTTP_500_ERROR);
        HTTP_ERROR_MAP.put(HTTP_UNKNOWN_ERROR_CODE, HTTP_UNKNOWN_ERROR);
    }

    @Nullable
    @Override
    public String match(@Nullable OkNetException ex) {
        if (ex == null
                || ex.getStatueCode() < 0
                || TextUtils.isEmpty(ex.getOkCode())) {
            return null;
        }
        return HTTP_ERROR_MAP.get(ex.getStatueCode()).get(ex.getOkCode());
    }
}
