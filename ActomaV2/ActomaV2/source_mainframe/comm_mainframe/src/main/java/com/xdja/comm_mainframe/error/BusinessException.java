package com.xdja.comm_mainframe.error;

import com.xdja.dependence.exeptions.OkException;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
public class BusinessException extends OkException {

    public static final String CODE_EXEC_FAILD = "business_exec_faild";
    public static final String CODE_EXEC_EMPTY_FAILD = "business_exec_empty_faild";

    /***************************************检测流程相关********************************************/
    public static final String ERROR_DRIVER_NOT_EXIST = "error_driver_not_exist";
    public static final String ERROR_CHIP_NOT_EXIST = "error_chip_not_exist";
    public static final String ERROR_CHIP_ACTIVIE_FAILD = "error_chip_activie_faild";
    public static final String ERROR_OBTAIN_SAVE_CONFIG_FAILD = "error_obtain_save_config_faild";
    public static final String ERROR_CKMS_GET_CHALLENGE_FAILD = "error_ckms_get_challenge_faild";
    public static final String ERROR_CKMS_SIGN_CHALLENGE_FAILD = "error_ckms_get_sign_challenge_faild";
    public static final String ERROR_DRIVER_NEED_UPDATE = "error_driver_need_update";
    public static final String ERROR_DRIVER_INSTALL_FAIL = "error_driver_install_fail";

    public static final String ERROR_CKMS_UPLOAD_INFO_FAIL = "error_ckms_upload_info_fail";


    public BusinessException(String okCode) {
        super(okCode);
    }

    public BusinessException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public BusinessException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }

    public BusinessException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }

}
