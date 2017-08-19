package com.xdja.comm.https.ErrorCode;

/**
 * Created by gbc on 2016/10/21.
 */
public class StatusCode {

    /**
     * 错误码
     */
    private final static int SOURCE_ERROR_STATE_CODE = -0x040000;

    /**
     * 网络异常
     * -262244
     */
    public static final int NET_ERROR = SOURCE_ERROR_STATE_CODE - 100;

    /**
     * 服务器返回空或未响应
     * -262245
     */
    public static final int SERVER_EMPTY = SOURCE_ERROR_STATE_CODE - 101;


    /**
     * 生成http文件头出错
     * -262445
     */
    public static final int GEN_HEADER_ERROR = SOURCE_ERROR_STATE_CODE - 301;

    /**
     * 生成签名出错
     * -262446
     */
    public static final int GEN_SIGN_ERROR = SOURCE_ERROR_STATE_CODE - 302;


    /**
     * 数据库操作失败
     * -262545
     */
    public static final int INSERT_DB_ERROR = SOURCE_ERROR_STATE_CODE - 401;






    /**
     * 获取证书sn异常
     * -262546
     */
    public static final int GET_SM2_ENC_ERROR = SOURCE_ERROR_STATE_CODE - 402;


    /**
     * 证书加密失败
     * -262547
     */
    public static final int SM2_ENC_ERROR = SOURCE_ERROR_STATE_CODE - 403;

    /**
     *  生成ksx出错
     *  -262548
     */
    public static final int GEN_KSX_ERROR = SOURCE_ERROR_STATE_CODE - 404;


    /**
     * 加密kuepri的sn和本地卡内容的加密证书的sn不一致
     * -262549
     */
    public static final int SM2_SN_DIF_ERROR = SOURCE_ERROR_STATE_CODE - 405;


    /**
     * 使用卡的密钥解密加密的ksx失败
     * -262550
     */
    public static final int SM2_DEC_KSX_ERROR = SOURCE_ERROR_STATE_CODE - 406;


    /**
     * 使用SM4分散生成ksx失败
     * -262551
     */
    public static final int GEN_KSXD_ERROR = SOURCE_ERROR_STATE_CODE - 407;



    /**
     * 账户私钥解密ksx失败
     * -262552
     */
    public static final int SM2_DEC_KUEP_ERROR = SOURCE_ERROR_STATE_CODE - 408;

    /**
     * 认证失败
     * -262553
     */
    public static final int AUTH_FAIL = SOURCE_ERROR_STATE_CODE - 409;

    /**
     * 服务器查无数据
     * -262554
     */
    public static final int SERVER_QUERY_NULL = SOURCE_ERROR_STATE_CODE - 410;

    /**
     * 获取设备证书失败
     * -262555
     */
    public static final int GET_DEVCER_ERR = SOURCE_ERROR_STATE_CODE - 411;

    /**
     * 无返回值
     * -262556
     */
    public static final int RETURN_PARAMS_ERR = SOURCE_ERROR_STATE_CODE - 412;

    /**
     * 入参错误
     * -262557
     */
    public static final int ENTRY_PARAMS_ERR = SOURCE_ERROR_STATE_CODE - 413;

}
