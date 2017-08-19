package com.xdja.comm.uitl.handler;

import com.xdja.safekeyjar.ErrorCode;

/**
 * Created by xdja-yogapro3 on 2016/2/24.
 */
public class TFCardDef {

    /**
     * 开发人员针对接口进行调试即可解决的问题
     */
    public static final int HANDLER_DEVELOPER_DEBUG = -30001;

    /**
     * 1.重新调用init接口，如果还有问题，请按以下建议解决；
     * 2.检查安全芯片驱动apk是否存在；
     * 3.如果手机有关联管控，检查安全芯片驱动是否被禁止关联启动
     */
    public static final int HANDLER_DRIVE_FAILD = -30002;

    /**
     * 查看芯片内是否有正确的证书
     */
    public static final int HANDLER_CER_VERIFY = -30003;

    /**
     * 此终端环境不支持MD5算法，即在此终端此接口不可用
     */
    public static final int HANDLER_MD5_NOT_SUPPORT = -30004;

    /**
     * 口令剩余重试次数处理
     */
    public static final int HANDLER_PWD = -30005;
    /**
     * 调用接口重试
     */
    public static final int HANDLER_RETRY = -30006;

    public static int getHandleType(int code) {

        int handleType = JNIDef.HANDLER_UNKNOW;

        switch (code) {
            case ErrorCode.XKR_PWD_1:
            case ErrorCode.XKR_PWD_2:
            case ErrorCode.XKR_PWD_3:
            case ErrorCode.XKR_PWD_4:
            case ErrorCode.XKR_PWD_5:
            case ErrorCode.XKR_PWD_6:
            case ErrorCode.XKR_PWD_7:
            case ErrorCode.XKR_PWD_8:
            case ErrorCode.XKR_PWD_9:
                handleType = HANDLER_PWD;
                break;
            case ErrorCode.CONTEXT_NULL:
            case ErrorCode.PARAM_ERROR:
            case ErrorCode.PUB_KEY_CALCULATE_PARAM_ERROR:
            case ErrorCode.OUT_PARAM_LEN_ERROR:
            case ErrorCode.ACTIVATE_URL_ERROR:
                handleType = HANDLER_DEVELOPER_DEBUG;
                break;
            case ErrorCode.INIT_ERROR:
                handleType = HANDLER_DRIVE_FAILD;
                break;
            case ErrorCode.GET_CERT_ORG_ERROR:
            case ErrorCode.GET_SAFE_CARD_SN_ERROR:
                handleType = HANDLER_CER_VERIFY;
                break;
            case ErrorCode.GEN_BUSINESS_KEY_ERROR:
                handleType = HANDLER_MD5_NOT_SUPPORT;
                break;
            case ErrorCode.GET_SAFE_KEY_INFO_ERROR:
            case ErrorCode.ACTIVATE_SAFE_KEY_FAIL:
            case ErrorCode.ACTIVATE_ERROR_BY_JSON_ENCODE:
            case ErrorCode.ACTIVATE_ERROR_BY_URL:
            case ErrorCode.ACTIVATE_ERROR_BY_SSL:
            case ErrorCode.ACTIVATE_ERROR_BY_JSON_DECODE:
            case ErrorCode.SAFE_KEY_SERVICE_DIE:
            case ErrorCode.ERROR_EXCEPTION_JAR:
                handleType = HANDLER_RETRY;
                break;
            default:
                break;
        }
        return handleType;
    }
}
