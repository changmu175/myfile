package com.xdja.comm.encrypt;

/**
 * Created by geyao on 2015/12/4.
 * 重构-第三方加密相关常量类-为便于修改特单独抽出定义
 */
public class IEncryptParams {
    /**
     * 标签
     */
    public static final String TAG = "IEncryptUtils ";
    /**
     * ksfid错误
     */
    public static final long ERROR_CODE = -1;
    /**
     * 获取ksfId service的action
     */
    public static final String GET_KSF_SERVICE_ACTION = "com.xdja.comm.intent.action.START_GET_KSF_SERVICE";
    /**
     * 获取ksfId receiver的action
     */
    public static final String GET_KSF_RECEIVER_ACTION = "com.xdja.comm.intent.action.START_GET_KSF_RECEIVER";
    /**
     * 用于接收安通+死亡通知
     */
    public static final String ACTOMA_IS_DIED_ACTION = "com.xdja.comm.intent.action.ACTOMA_IS_DIED";
    /**
     * 调用ksfId时intent的key
     */
    public static final String KSXID = "ksxId";
    /**
     * 调用成功
     */
    public static final int RESULT_OK = 0;
    public static final int RESULT_PARAMS_ERROR = -1;
    /**
     * 本地服务ksf集合数据为空
     */
    public static final int RESULT_DATA_NULL = -1;
}
