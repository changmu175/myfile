package com.xdja.comm.https.ErrorCode;

import com.xdja.comm.R;
import com.xdja.comm.server.ActomaController;

/**
 * Created by gbc on 2016/10/21.
 */
public class ErrorMsg {
    /**
     * 网络异常
     */
    //[s]modify by xienana for bug 5305 @20161026 [review by tangsha]
    public static final String NET_ERROR = ActomaController.getApp().getString(R.string.net_disable);
    //[e]modify by xienana for bug 5305 @20161026 [review by tangsha]
    /**
     * 服务器返回空或未响应
     */
    public static final String SERVER_EMPTY = ActomaController.getApp().getString(R.string.server_empty);

    /**
     * 生成http文件头出错
     */
    public static final String GEN_HEADER_ERROR = "生成http文件头出错";

    /**
     * 生成签名出错
     */
    public static final String GEN_SIGN_ERROR = "生成签名出错";


    /**
     * 数据库操作失败
     */
    public static final String INSERT_DB_ERROR = "插入数据库失败";


    /**
     * 数据库查无内容
     */
    public static final String QUERY_DB_ERROR = "数据库查无内容";

    /**
     * 数据库异常
     */
    public static final String DB_EXCEPTION = "数据库异常";

    /**
     * 认证失败
     */
    public static final String AUTH_ERROR = "认证失败";

    /**
     * 服务器查询无数据
     */
    public static final String QUERY_SERVER_NULL = "服务器查无数据";

    /**
     * 获取证书sn异常
     */
    public static final String GET_SM2_ENC_ERROR = "获取证书sn异常";


    /**
     * 证书加密失败
     */
    public static final String SM2_ENC_ERROR = "对会话密钥加密失败";


    /**
     *  生成ksx出错
     */
    public static final String GEN_KSX_ERROR = "生成ksx失败";


    /**
     * 加密kuepri的sn和本地卡内容的加密证书的sn不一致
     */
    public static final String SM2_SN_DIF_ERROR = "加密证书sn不一致";


    /**
     * 使用卡的密钥解密加密的ksx失败
     */
    public static final String SM2_DEC_KSX_ERROR = "解密ksx失败";


    /**
     * 使用SM4分散生成ksx失败
     */
    public static final String GEN_KSXD_ERROR = "分散生成ksxd失败";

    /**
     * 账户私钥解密kuep失败
     */
    public static final String SM2_DEC_KUEP_ERROR = "解密账户信息失败";

    /**
     * 获取设备证书失败
     */
    public static final String GET_DEVCER_ERR = "获取设备证书失败";

    /**
     * 返回值错误
     */
    public static final String RETURN_PARAMS_ERR = "返回值错误";

    /**
     * 入参错误
     */
    public static final String ENTRY_PARAMS_ERR = "入参错误";

    /**
     * 时间错误
     */
    public static final String TIME_ERR = ActomaController.getApp().getString(R.string.time_error);
    /**
     * 网络异常
     */
    public static final String NETWORK_ERR = "网络异常请稍后重试";

    /**
     * IO异常
     */
    //[s]modify by xienana for bug 5305 @20161026 [review by tangsha]
    public static final String IO_ERR = ActomaController.getApp().getString(R.string.net_disable);
    //[e]modify by xienana for bug 5305 @20161026 [review by tangsha]
}
