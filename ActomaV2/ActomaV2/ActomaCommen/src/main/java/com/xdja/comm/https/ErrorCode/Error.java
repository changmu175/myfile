package com.xdja.comm.https.ErrorCode;

/**
 * Created by gbc on 2016/10/21.
 */
public class Error {

    /**
     * 网络异常
     */
    public static final String NET_ERROR = "CLIENT_NET_ERROR";

    /**
     * 服务器返回空或未响应
     */
    public static final String SERVER_EMPTY = "SERVER_EMPTY";



    /**
     * 生成http文件头出错
     */
    public static final String GEN_HEADER_ERROR = "CLIENT_GEN_HEADER_ERROR";

    /**
     * 生成签名出错
     */
    public static final String GEN_SIGN_ERROR = "CLIENT_GEN_SIGN_ERROR";


    /**
     * 数据库操作失败
     */
    public static final String INSERT_DB_ERROR = "INSERT_DB_ERROR";


    /**
     * 数据库查无内容
     */
    public static final String QUERY_DB_ERROR = "QUERY_DB_ERROR";

    /**
     * 数据库异常
     */
    public static final String DB_EXCEPTION = "DB_EXCEPTION";

    /**
     * 认证失败
     */
    public static final String AUTH_ERROR = "AUTH_ERROR";

    /**
     * 获取证书sn异常
     */
    public static final String GET_SM2_ENC_ERROR = "GET_SM2_ENC_ERROR";



    /**
     * 证书加密失败
     */
    public static final String SM2_ENC_ERROR = "SM2_ENC_ERROR";

    /**
     * 服务器查询无数据
     */
    public static final String QUERY_SERVER_NULL = "QUERY_SERVER_NULL";

    /**
     *  生成ksx出错
     */
    public static final String GEN_KSX_ERROR = "GEN_KSX_ERROR";


    /**
     * 加密kuepri的sn和本地卡内容的加密证书的sn不一致
     */
    public static final String SM2_SN_DIF_ERROR = "SM2_SN_DIF_ERROR";


    /**
     * 使用卡的密钥解密加密的ksx失败
     */
    public static final String SM2_DEC_KSX_ERROR = "SM2_DEC_KSX_ERROR";

    /**
     * 使用SM4分散生成ksx失败
     */
    public static final String GEN_KSXD_ERROR = "GEN_KSXD_ERROR";

    /**
     * 账户私钥解密kuep失败
     */
    public static final String SM2_DEC_KUEP_ERROR = "SM2_DEC_KUEP_ERROR";

    /**
     * 获取设备证书失败
     */
    public static final String GET_DEVCER_ERR = "GET_DEVCER_ERR";

    /**
     * 返回值错误
     */
    public static final String RETURN_PARAMS_ERR = "RETURN_PARAMS_ERR";

    /**
     * 入参错误
     */
    public static final String ENTRY_PARAMS_ERR = "ENTRY_PARAMS_ERR";

    /**
     * 时间错误
     */
    public static final String TIME_ERR = "TIME_ERR";

    /**
     * 网络异常
     */
    public static final String NETWORK_ERR = "NETWORK_ERR";

    /**
     * IO异常
     */
    public static final String IO_ERR = "IO_ERR";
}
