package com.xdja.comm.uitl.handler;

import android.support.annotation.IntDef;

/**
 * Created by xdja-错误过滤器 on 2016/2/27.
 */
public class ErrorFilter {

    public static final int SUCCESS = 0;

    /**
     * 未检测到安全卡
     */
    public static final int ERROR_DETECT_CARD_FAILD = -1;
    /**
     * 安全卡损坏
     */
    public static final int ERROR_CARD_BROKEN = -2;
    /**
     * PIN码验证失败
     */
    public static final int ERROR_PIN_MISSTIC = -3;
    /**
     * 未知错误
     */
    public static final int ERROR_OTHER = -4;

    @IntDef(
            value = {
                    SUCCESS,
                    ERROR_DETECT_CARD_FAILD,
                    ERROR_CARD_BROKEN,
                    ERROR_PIN_MISSTIC,
                    ERROR_OTHER
            })
    public @interface CardErrorCode {
    }

    @CardErrorCode
    public int filte(int code){
        int handleType;
//        String errorMsg = "未知错误";
        handleType = TFCardDef.getHandleType(code);
//        errorMsg = ErrorCode.getErrorInfo(code);
        if (handleType == JNIDef.HANDLER_UNKNOW) {
            handleType= UnitePinDef.getHandleType(code);
//            errorMsg = UnitePinErrorCode.getErrorInfo(code);
            if (handleType == JNIDef.HANDLER_UNKNOW) {
                handleType = JNIDef.getHandleType(code);
//                errorMsg = "未知错误";
            }
        }
        int errorType = ERROR_OTHER;
        switch (handleType){
            case JNIDef.HANDLER_SUCESS:
                errorType = SUCCESS;
                break;
            case TFCardDef.HANDLER_DRIVE_FAILD:
            case UnitePinDef.HANDLER_DRIVE_FAILD:
            case UnitePinDef.HANDLER_RETRY_INSTALL:
            case JNIDef.HANDLER_RELOAD_CARDAPK:
            case JNIDef.HANDLER_UNIN_5:
            case JNIDef.HANDLER_UNIN_6:
            case JNIDef.HANDLER_UNIN_1:
                errorType = ERROR_DETECT_CARD_FAILD;
                break;
            case UnitePinDef.HANDLER_VERIFY_PIN:
            case UnitePinDef.HANDLER_RETRY_USN:
                errorType = ERROR_PIN_MISSTIC;
                break;
            case JNIDef.HANDLER_UNIN_2:
            case JNIDef.HANDLER_UNIN_4:
            case JNIDef.HANDLER_FIX:
            case JNIDef.HANDLER_UNKNOW_RESON:
            case JNIDef.HANDLER_UNKNOW_NEW:
            case JNIDef.HANDLER_CALL_FUNCTION:
                errorType = ERROR_CARD_BROKEN;
                break;
            case JNIDef.HANDLER_UNKNOW:
            case TFCardDef.HANDLER_DEVELOPER_DEBUG:
            case TFCardDef.HANDLER_CER_VERIFY:
            case TFCardDef.HANDLER_MD5_NOT_SUPPORT:
            case TFCardDef.HANDLER_RETRY:
            case UnitePinDef.HANDLER_DEVELOPER_DEBUG:
            case UnitePinDef.HANDLER_RETRY_INIT:
            case UnitePinDef.HANDLER_RECALL:
            case JNIDef.HANDLER_RETRY:
            case JNIDef.HANDLER_RELOAD_APK:
            case JNIDef.HANDLER_REBOOT:
            case JNIDef.HANDLER_UPDATE_APK:
            case JNIDef.HANDLER_NOT_EXIST:
            case JNIDef.HANDLER_DISCUSS:
            case JNIDef.HANDLER_UNLOCK:
            case JNIDef.HANDLER_USER:
            case JNIDef.HANDLER_UNIN_3:
                break;
            default:
                break;
        }
        return errorType;
    }
}
