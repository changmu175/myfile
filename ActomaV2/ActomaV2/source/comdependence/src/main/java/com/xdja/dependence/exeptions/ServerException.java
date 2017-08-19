package com.xdja.dependence.exeptions;

/**
 * Created by xdja-fanjiandong on 2016/3/8.
 * 后台返回的业务异常定义
 */
public class ServerException extends OkException {

    public static final String CODE_UNKNOW_SERVER_EXCEPTION = "unknow_server_exception";

    /**
     * 请求参数为空
     */
    public static final String REQUEST_PARAMS_ERROR = "request_params_error";

    /**
     * 账号不存在
     */
    public static final String ACCOUNT_NOT_EXISTS = "account_not_exists";
    /**
     * 账号或密码错误
     */
    public static final String ACCOUNT_OR_PWD_ERROR = "account_or_pwd_error";
    /**
     * 账号被封停
     */
    public static final String ACCOUNT_SEAL = "account_seal";
    /**
     * 账号被冻结
     */
    public static final String ACCOUNT_FREEZE = "account_freeze";
    /**
     * 账号被注销
     */
    public static final String ACCOUNT_LOGOUT = "account_logout";

    /**
     * 用手机验证码登录时判断是否v1账号迁移
     */
    public static final String ACCOUNT_TRANSFER_FAIL = "account_transfer_fail";

    /**
     * 账号设备无对应关系
     */
    public static final String ACCOUNT_DEVICE_NOT_RELATION = "account_device_not_relation";
    /**
     * 账号已经绑定该手机号
     */
    public static final String ACCOUNT_ALREADY_BIND_MOBILE = "account_already_bind_mobile";
    /**
     * 账号手机号无绑定关系
     */
    public static final String ACCOUNT_MOBILE_NOT_BIND = "account_mobile_not_bind";
    /**
     * 账号不一致
     */
    public static final String ACCOUNT_NOT_ACCORDANCE = "account_not_accordance";
    /**
     * 已经设置过自定义账号
     */
    public static final String ALREADY_SET_CUSTOMIZE_ACCOUNT = "already_set_customize_account";
    /**
     * 自定义账号已存在
     */
    public static final String CUSTOMIZE_ACCOUNT_EXISTS = "customize_account_exists";
    /**
     * 账号与手机号对应账号不是好友关系
     */
    public static final String MOBILE_AND_ACCOUNT_NOT_FRIEND = "mobile_and_account_not_friend";
    /**
     * 账号与授权Id无对应关系
     */
    public static final String ACCOUNT_AUTHORIZELD_NOT_RELATION = "account_authorizeld_not_relation";
    /**
     * 账号未备份历史记录
     */
    public static final String ACCOUNT_NOT_HISTORY = "account_not_history";
    /**
     * 没有搜索到账户信息
     */
    public static final String NO_USERS_FOUND = "no_users_found";


    /**
     * 手机号不一致
     */
    public static final String MOBILE_NOT_ACCORDANCE = "mobile_not_accordance";
    /**
     * 手机号未注册
     */
    public static final String MOBILE_NOT_REGISTER = "mobile_not_register";

    /**
     * 手机验证码不正确
     */
    public static final String AUTH_CODE_ERROR = "authCode_error";
    /**
     * 该手机号发送短信次数超限
     */
    public static final String TRANSCEND_SEND_TIMES = "transcend_send_times";
    /**
     * 手机号已注册
     */
    public static final String MOBILE_ALREADY_REGISTER = "mobile_already_register";
    /**
     * 短信发送失败
     */
    public static final String FAIL_SEND_MESSAGE = "fail_send_message";

    /**
     * 账户已经设置过手机号
     */
    public static final String ACCOUNT_ALREADY_SET_MOBILE = "account_already_set_mobile";



    /**
     * 设备不一致
     */
    public static final String DEVICE_NOT_ACCORDANCE = "device_not_accordance";
    /**
     * 待授信设备卡号不一致
     */
    public static final String CARD_NO_NOT_ACCORDANCE = "cardNo_not_accordance";
    /**
     * 设备账号授信成功
     */
    public static final String DEVICE_ACCOUT_ALREADY_AUTHORIZELD = "device_accout_already_authorizeld";

    /**
     * 设备账号已授信
     */
    public static final String DEVICE_ACCOUT_ALREADY_AUTHORIZE = "device_accout_already_authorize";

    /**
     * 设备不存在
     */
    public static final String DEVICE_NOT_REGISTER = "device_not_register";


    /**
     * 内部验证码无效
     */
    public static final String INNER_AUTH_CODE_INVALID = "inner_authCode_invalid";
    /**
     * 旧账号设备无对应关系
     */
    public static final String OLD_ACCOUNT_DEVICE_NOT_RELATION = "oldAccount_device_not_relation";
    /**
     * 设备未授信(当账号未设置手机号时，在非授信设备上使用验证好方式找回密码时返回)
     */
    public static final String DEVICE_IS_UN_AUTHORIZE = "device_is_un_authorize";
    /**
     * 授权信息无效
     */
    public static final String AUTHORIZELD_INVALID = "authorizeld_invalid";
    /**
     * 没有配置信息
     */
    public static final String NO_CONFIGS = "no_configs";
    /**
     * 没有消息
     */
    public static final String NO_MESSAGE = "no_message";


    public static final String TICKET_IS_INVALID = "0x9008";

    //请求参数为空
    public static final String E40001 = "40001";
    //账号为空
    public static final String E40002 = "40002";
    //密码为空
    public static final String E40003 = "40003";
    //手机号为空
    public static final String E40004 = "40004";
    //设备名为空
    public static final String E40005 = "40005";
    //卡号为空
    public static final String E40006 = "40006";
    //内部验证码为空
    public static final String E40007 = "40007";
    //短信验证码为空
    public static final String E40008 = "40008";
    //自定义账号为空
    public static final String E40009 = "40009";
    //旧账号为空
    public static final String E40010 = "40010";
    //新账号为空
    public static final String E40011 = "40011";
    //手机型号为空
    public static final String E40012 = "40012";
    //厂商信息为空
    public static final String E40013 = "40013";
    //最后策略更新Id为空
    public static final String E40014 = "40014";
    //PN标识为空
    public static final String E40015 = "40015";
    //客户端类型为空
    public static final String E40016 = "40016";
    //安通+客户端版本号为空
    public static final String E40017 = "40017";
    //客户端操作系统版本号为空
    public static final String E40018 = "40018";
    //客户端唯一标识为空
    public static final String E40019 = "40019";
    //第三方账号为空
    public static final String E40020 = "40020";
    //Token为空
    public static final String E40021 = "40021";
    //无法找到opCode对应的值
    public static final String E40022 = "40022";
    //授信信息id为空
    public static final String E40023 = "40023";
    //对方账号列表为空
    public static final String E40024 = "40024";
    //消息内容为空
    public static final String E40025 = "40025";
    //推送消息为空
    public static final String E40026 = "40026";
    //无法找到passwd对应的值
    public static final String E40027 = "40027";
    //无法找到nickName对应的值
    public static final String E40028 = "40028";
    //无法找到customizeAccount对应的值
    public static final String E40029 = "40029";
    //头像id为空
    public static final String E40030 = "40030";
    //头像略缩图id为空
    public static final String E40031 = "40031";
    //查询参数为空
    public static final String E40032 = "40032";
    //本批次更新的数量为空
    public static final String E40033 = "40033";
    //最后更新标识为空
    public static final String E40034 = "40034";
    //ticket为空
    public static final String E40035 = "40035";
    //客户端型号为空
    public static final String E40036 = "40036";
    //账号格式非法
    public static final String E41001 = "41001";
    //手机号格式非法
    public static final String E41002 = "41002";
    //自定义账号格式非法
    public static final String E41003 = "41003";
    //旧账号格式非法
    public static final String E41004 = "41004";
    //新账号格式非法
    public static final String E41005 = "41005";
    //好友手机号个数非法
    public static final String E41006 = "41006";
    //好友手机号格式非法
    public static final String E41007 = "41007";
    //好友手机号重复
    public static final String E41008 = "41008";
    //客户端类型不在枚举范围内
    public static final String E41009 = "41009";
    //登录类型不在枚举范围内
    public static final String E41010 = "41010";
    //操作系统名称不在枚举范围内
    public static final String E41011 = "41011";
    //对方账号格式非法
    public static final String E41012 = "41012";
    //更新标识格式非法
    public static final String E41013 = "41013";
    //无效的Ticket
    public static final String E41014 = "41014";
    //请求method与接口要求不符
    public static final String E41015 = "41015";
    //获取账号时，返回的账号为空
    public static final String E50000 = "50000";
    //imei为空
    public static final String E40037 = "40037";//E40301
    //未查到该IMEI对应的人员信息
    public static final String E40301 = "40301";
    //集团服务未开通或超出使用期限
    public static final String E40302 = "40302";

    public ServerException(String okCode) {
        super(okCode);
    }

    public ServerException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public ServerException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }

    public ServerException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }
}
