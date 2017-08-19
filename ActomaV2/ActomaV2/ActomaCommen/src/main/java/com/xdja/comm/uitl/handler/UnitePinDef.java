package com.xdja.comm.uitl.handler;

import com.xdja.unitepin.UnitePinErrorCode;

/**
 * Created by xdja-fjd on 2016/2/24.
 */
public class UnitePinDef {

    /**
     * 开发人员针对接口进行调试即可解决的问题
     */
    public static final int HANDLER_DEVELOPER_DEBUG = -20002;

    /**
     * 重新调用init接口
     */
    public static final int HANDLER_RETRY_INIT = -20003;

    /**
     * 1.重新调用init接口，如果还有问题，请按以下建议解决；
     * 2.检查安全芯片驱动apk是否存在；
     * 3.如果手机有关联管控，检查安全芯片驱动是否被禁止关联启动
     */
    public static final int HANDLER_DRIVE_FAILD = -20004;

    /**
     * 调用接口重试
     */
    public static final int HANDLER_RECALL = -20005;

    /**
     * 应用根据业务自行判断是否需要让用户输入安全口令进行验证
     */
    public static final int HANDLER_VERIFY_PIN = -20006;

    /**
     * 开发人员针对传入usn进行调试查看
     */
    public static final int HANDLER_RETRY_USN = -20007;

    /**
     * 重新安装本应用或安全芯片驱动apk，使两者签名一致
     */
    public static final int HANDLER_RETRY_INSTALL = -20008;

    public static int getHandleType(int code) {
        int handleType = JNIDef.HANDLER_UNKNOW;
        switch (code) {
            case UnitePinErrorCode.XKR_INVALID_PARA:
            case UnitePinErrorCode.XKR_PASSWORD:
            case UnitePinErrorCode.XKR_KEY_LOCKED:
                handleType = JNIDef.getHandleType(code);
                break;
            case UnitePinErrorCode.ERROR_CONTEXT_NULL:
            case UnitePinErrorCode.ERROR_PARAM:
            case UnitePinErrorCode.ERROR_PARAM_OUT_NULL:
            case UnitePinErrorCode.ERROR_PARAM_LEN:
                handleType = HANDLER_DEVELOPER_DEBUG;
                break;
            case UnitePinErrorCode.ERROR_NOT_INIT:
                handleType = HANDLER_RETRY_INIT;
                break;
            case UnitePinErrorCode.ERROR_INIT_FAIL:
                handleType = HANDLER_DRIVE_FAILD;
                break;
            case UnitePinErrorCode.ERROR_EXCEPTION:
            case UnitePinErrorCode.ERROR_PARAM_DYNAMIC_CODE_ERROR:
            case UnitePinErrorCode.ERROR_GEN_NOW_PASSWD_FAILED:
            case UnitePinErrorCode.ERROR_GET_STATIC_CODE_FAILED:
                handleType = HANDLER_RECALL;
                break;
            case UnitePinErrorCode.ERROR_PIN_NULL:
                handleType = HANDLER_VERIFY_PIN;
                break;
            case UnitePinErrorCode.ERROR_PARAM_USN_ERROR:
                handleType = HANDLER_RETRY_USN;
                break;
            case UnitePinErrorCode.ERROR_UNLOCK_ACTION_NOT_EQUAL_CURACTION:
                break;
            case UnitePinErrorCode.ERROR_VERIFY_PACKAGE_FAILED:
                handleType = HANDLER_RETRY_INSTALL;
                break;
            default:
                break;
        }
        return handleType;
    }
}
