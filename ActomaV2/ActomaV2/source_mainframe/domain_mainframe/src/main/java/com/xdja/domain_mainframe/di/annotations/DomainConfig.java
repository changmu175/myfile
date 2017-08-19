package com.xdja.domain_mainframe.di.annotations;

import android.support.annotation.StringDef;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
public class DomainConfig {

    @StringDef(value = {
            ACCOUNT_REGIST,
            ACCOUNT_REOBTAIN,
            ACCOUNT_CUSTOM,
            ACCOUNT_MODIFY,
            BINDMODBILE_AUTHCODE_OBTAIN,
            BINDMODBILE,
            FORCE_BINDMODBILE,
            ACCOUNT_PWD_LOGIN,
            REFRESH_TICKET,
            LOGIN_AUTHCODE_OBTAIN,
            RESET_AUTHCODE_OBTAIN,
            LOGIN_MOBILE,
            BIND_AUTHOCODE_OBTAIN,
            MODIFY_AUTHOCODE_OBTAIN,
            MODIFY_MOBILE,
            TICKE_BIND_MOBILE,
            TICKE_FORCE_BIND_MOBILE,
            MODIFY_NICKNAME,
            MODIFY_AVATAR,
            TICKE_CUSTOM_ACCOUNT,
            UNBIND_MOBILE,
            UNBIND_DISK_MOBILE,
            /**userInfo部分**/
            AUTH_PASSWD,
            LOGOUT,
            MODIFY_AVATAR,
            MODIFY_DEVICE_NAME,
            MODIFY_NIKE_NAME,
            MODIFY_PASSWD,
            QUERY_ACCOUNT_INFO,
            QUERY_BATCH_ACCOUNT,
            QUERY_DEVICES,
            QUERY_FORCE_LOGOUT_NOTICE,
            QUERY_INCREMENT_ACCOUNTS,
            QUERY_ONLINE_NOTICE,
            QUERY_SERVER_CONFIGS,
            QUERY_UN_BIND_DEVICE_NOTICE,
            RELIEVE_DEVICE,
            /*UPDATE_STRATEGYS,*/
            QUERY_STRATEGYBYMOBILE,
            QUERY_STRATEGYS,
            UPLOAD_IMG,
            GET_CURRENT_ACCOUNT_INFO,
            /**设备认证部分**/
            GET_AUTH_INFO,
            AUTH_DEVICE,
            CHECK_FRIEND_MOBILES,
            CHECK_MOBILE,
            OBTAION_AUTH_INFO,
            OBTAION_DEVICE_AUTHRIZE_AUTHCODE,
            REOBTATION_AUTHINF,
            /**密码管理部分**/
            CHECK_RESTPWD_AUTHCODE,
            RESTPWD_BYAUTHCODE,
            AUTH_FRIEND_PHONE,
            RESTPWD_BYFRIENDMOBILES,
            DETECT_INIT,
            QUERY_ACCOUNT_AT_LOCAL,
            DISK_LOGOUT,
            CKMS_INIT,
            CKMS_CREATE_SEC,
            CKMS_AUTH_ADD_DEV,
            CKMS_FORCE_ADD_DEV,
            /*其他*/
            OPEN_THIRD_ENCRYPT_TRANSFER,
            DOWNLOAD_FROM_APPSTORE,
            GET_NEWREMIND_SETTINGS,
            SET_NEWREMIND_SETTINGS,
            GET_NODISTRUB_SETTINGS,
            SET_NODISTRUB_SETTINGS,
            GET_RECEIVERMODE_SETTINGS,
            SET_RECEIVERMODE_SETTINGS,
            CHECK_NEW_VERSION,
			HOOK_UPDATE,
            CKMS_RELEASE,
            FEED_BACK,
            FEED_BACK_IMAGE,
            DATA_MIGRATION,
            DATA_MIGRATION_FINISH,
			USER_INFO_INIT,

            /*安全锁部分*/
            GET_SAFELOCK_CLOUD_SETTINGS,
            GET_SAFELOCK_SETTINGS,
            SET_SAFELOCK_SETTINGS,
            GET_GESTURE_CLOUD,
            SAVE_GESTURE_CLOUD
    })

    public @interface UseCaseType {

    }

    /**
     * fill方法参数依次为：昵称、密码、头像文件Id、头像缩略图Id
     */
    public static final String ACCOUNT_REGIST = "registAccount";
    /**
     * fill方法参数依次为：旧帐号、内部验证码
     */
    public static final String ACCOUNT_REOBTAIN = "reObtainAccount";
    /**
     * fill方法参数依次为：原帐号，内部验证码，自定义的新账号
     */
    public static final String ACCOUNT_CUSTOM = "customAccount";
    /**
     * fill方法依次为：旧帐号，新帐号，内部验证码
     */
    public static final String ACCOUNT_MODIFY = "modifyAccount";
    /**
     * fill方法参数依次为：帐号、手机号
     */
    public static final String BINDMODBILE_AUTHCODE_OBTAIN = "obtaionBindMobileAuthCode";
    /**
     * fill方法参数依次为：帐号、短信验证码、内部验证码、手机号
     */
    public static final String BINDMODBILE = "bindMobile";
    /**
     * fill方法参数依次为：帐号、内部验证码、手机号
     */
    public static final String FORCE_BINDMODBILE = "forceBindMobile";
    /**
     * fill方法参数依次为：帐号、密码
     */
    public static final String ACCOUNT_PWD_LOGIN = "accountPwdLogin";

    /**
     * fill方法参数依次为：帐号、密码
     */
    public static final String REFRESH_TICKET = "refreshTicket";

    /**
     * fill方法参数为手机号
     */
    public static final String LOGIN_AUTHCODE_OBTAIN = "obtainLoginResetAuthCode";
    /**
     * fill方法参数为手机号
     */
    public static final String RESET_AUTHCODE_OBTAIN = "obtainResetAuthCode";
    /**
     * fill方法参数依次为：手机号、短信验证码、内部验证码
     */
    public static final String LOGIN_MOBILE = "mobileLogin";

    /**
     * fill方法的参数为手机号
     */
    public static final String BIND_AUTHOCODE_OBTAIN = "obtainBindAuthCode";

    /**
     * fill方法的参数为手机号
     */
    public static final String MODIFY_AUTHOCODE_OBTAIN = "obtainModifyAuthCode";

    /**
     * fill方法的参数为手机号、验证码
     */
    public static final String MODIFY_MOBILE = "obtainModifyMobile";
    /**
     * fill方法的参数依次为：短信验证码、手机号
     */
    public static final String TICKE_BIND_MOBILE = "bindeMobileByTicket";
    /**
     * fill方法的参数依次为：手机号
     */
    public static final String TICKE_FORCE_BIND_MOBILE = "forceBindeMobileByTicket";
    /**
     * fill方法的参数为自定义的帐号
     */
    public static final String TICKE_CUSTOM_ACCOUNT = "customAccountByTicket";

    public static final String MODIFY_NICKNAME = "modifyNickName";

    /**
     * fill方法的参数为手机号
     */
    public static final String UNBIND_MOBILE = "unbindMobile";

    /**
     * fill方法的参数为手机号
     */
    public static final String UNBIND_DISK_MOBILE = "unbindDiskMobile";

    /*-----------------------------------userInfo部分----------------------------------------------*/

    /**
     * fill方法的参数依次为：密码
     */
    public static final String AUTH_PASSWD = "authPasswd";

    /**
     * fill方法的参数依次为：无
     */
    public static final String LOGOUT = "logout";

    /**
     * fill方法的参数依次为：头像Id,头像缩略图Id
     */
    public static final String MODIFY_AVATAR = "modifyAvatar";

    /**
     * fill方法的参数依次为：设备卡号,设备名称
     */
    public static final String MODIFY_DEVICE_NAME = "modifyDeviceName";

    /**
     * fill方法的参数依次为：新昵称
     */
    public static final String MODIFY_NIKE_NAME = "modifyNikeName";

    /**
     * fill方法的参数依次为：新密码
     */
    public static final String MODIFY_PASSWD = "modifyPasswd";

    /**
     * fill方法的参数依次为：账号或手机号
     */
    public static final String QUERY_ACCOUNT_INFO = "queryAccountInfo";

    /**
     * fill方法的参数依次为：账号信息列表
     */
    public static final String QUERY_BATCH_ACCOUNT = "queryBatchAccount";

    /**
     * fill方法的参数依次为：无
     */
    public static final String QUERY_DEVICES = "queryDevices";

    /**
     * fill方法的参数依次为：帐号
     */
    public static final String QUERY_FORCE_LOGOUT_NOTICE = "queryForceLogoutNotice";

    /**
     * fill方法的参数依次为：lastUpdateId,batchSize
     */
    public static final String QUERY_INCREMENT_ACCOUNTS = "queryIncrementAccounts";

    /**
     * fill方法的参数依次为：无
     */
    public static final String QUERY_ONLINE_NOTICE = "queryOnlineNotice";

    /**
     * fill方法的参数依次为：无
     */
    public static final String QUERY_SERVER_CONFIGS = "queryServerConfigs";

    /**
     * fill方法的参数依次为：帐号
     */
    public static final String QUERY_UN_BIND_DEVICE_NOTICE = "queryUnBindDeviceNotice";

    /**
     * fill方法的参数依次为：设备卡号
     */
    public static final String RELIEVE_DEVICE = "relieveDevice";

    /**
     * fill方法的参数依次为：version:协议版本号,cardNo:设备芯片卡号,lastStrategyId:最后策略更新ID，第一次为0,batchSize:批量条数
     */
//    public static final String UPDATE_STRATEGYS = "updateStrategys";

    /**
     * fill方法的参数依次为：version:协议版本号,cardNo:设备芯片卡号, model:手机型号, manufacturer:,厂商信息, lastStrategyId:最后策略更新ID，第一次为0,batchSize:批量条数
     */
    public static final String QUERY_STRATEGYBYMOBILE = "queryStrategyByMobile";

    /**
     * fill方法的参数依次为：无
     */
    public static final String QUERY_STRATEGYS = "queryStrategys";

    /**
     * fill方法的参数依次为：bitmap
     */
    public static final String UPLOAD_IMG = "uploadImg";

    /**
     * fill方法无参数
     */
    public static final String GET_CURRENT_ACCOUNT_INFO = "getCurrentAccountInfo";

    /*-----------------------------------设备绑定部分----------------------------------------------*/
    /**
     * fill方法的参数依次为：授权ID
     */
    public static final String GET_AUTH_INFO = "getAuthInfo";

    /**
     * fill方法的参数依次为：授权ID,cardNo
     */
    public static final String AUTH_DEVICE = "authDevice";
    /**
     * fill方法参数依次为：帐号、内部验证码、好友的手机号
     */
    public static final String CHECK_FRIEND_MOBILES = "checkFriendMobiles";
    /**
     * 帐号、手机号、短信验证码、内部验证码
     */
    public static final String CHECK_MOBILE = "chekMobile";
    /**
     * 授权ID
     */
    public static final String OBTAION_AUTH_INFO = "obtainAuthInfo";
    /**
     * 帐号、内部验证码、授权ID
     */
    public static final String OBTAION_DEVICE_AUTHRIZE_AUTHCODE = "obtaionDeviceAuthrizeAuthCode";
    /**
     * 授权ID
     */
    public static final String REOBTATION_AUTHINF = "reObtaionAuthInfo";

     /*-----------------------------------密码管理部分----------------------------------------------*/
    /**
     * 校验重置密码验证码,fill方法参数依次为：手机号、短信验证码、内部验证码
     */
    public static final String CHECK_RESTPWD_AUTHCODE = "checkRestPwdAuthCode";
    /**
     * 通过验证码重置密码，fill方法参数依次为：手机号、内部验证码、密码
     */
    public static final String RESTPWD_BYAUTHCODE = "restPwdByAuthCode";
    /**
     * 验证码好友手机号，fill方法参数依次为：帐号,好友手机号列表
     */
    public static final String AUTH_FRIEND_PHONE = "authFriendPhone";
    /**
     * 通过好友手机号重置密码，fill方法参数依次为：帐号、内部验证码和密码
     */
    public static final String RESTPWD_BYFRIENDMOBILES = "restPwdByFriendMobiles";

    /**
     * 初始化检测流程
     */
    public static final String DETECT_INIT = "detect_init";
    //[S]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.
    /**
     * 初始化检测流程,获取用户配置信息
     */
    public static final String USER_INFO_INIT = "user_info_init";
    //[E]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.

    /*[S]add by tangsha@20160705 for ckms*/
    /**
     * CKMS初始化流程
     */
    public static final String CKMS_INIT = "ckms_init";

    /**
     * CKMS创建安全身份
     */
    public static final String CKMS_CREATE_SEC = "ckms_create_sec";

    /**
     * CKMS授权添加设备
     */
    public static final String CKMS_AUTH_ADD_DEV = "ckms_auth_add_dev";

    /**
     * CKMS强制添加设备
     */
    public static final String CKMS_FORCE_ADD_DEV = "ckms_force_add_dev";

    /**
     * 打开三方通道
     */
    public static final String OPEN_THIRD_ENCRYPT_TRANSFER = "open_third_encrypt_transfer";

    /**
     * CKMS释放流程
     */
    public static final String CKMS_RELEASE = "ckms_release";
    /*[E]add by tangsha@20160705 for ckms*/

    /**
     * 查询在本地存储的上次登录账号
     */
    public static final String QUERY_ACCOUNT_AT_LOCAL = "queryAccountAtLocal";

    /**
     * 客户端本地使用数据的退出
     */
    public static final String DISK_LOGOUT = "DiskLogout";

    /**
     * 应用商店下载
     */
    public static final String DOWNLOAD_FROM_APPSTORE = "downloadFromAppstore";

    /**
     * 获取新消息提醒设置,fill方法参数为：context
     */
    public static final String GET_NEWREMIND_SETTINGS = "getNewRemindSettings";
    /**
     * 设置新消息提醒,fill方法参数为：context，SettingBean[]
     */
    public static final String SET_NEWREMIND_SETTINGS = "setNewRemindSettings";
    /**
     * 获取免打扰模式,fill方法参数为：context
     */
    public static final String GET_NODISTRUB_SETTINGS = "getNoDistrubSettings";
    /**
     * 设置免打扰设置,fill方法参数为：context
     */
    public static final String SET_NODISTRUB_SETTINGS = "setNoDistrubSettings";

    /**
     * 设置听筒模式,fill方法参数为：context
     */
    public static final String SET_RECEIVERMODE_SETTINGS = "setReceiverModeSettings";

    /**
     * 获取听筒模式,fill方法参数为：context
     */
    public static final String GET_RECEIVERMODE_SETTINGS = "getReceiverModeSettings";

    /**
     * 检查是否有升级版本,fill方法参数为：context
     */
    public static final String CHECK_NEW_VERSION = "checkNewVersion";
	
    /**
     * fill方法的参数依次为：version:协议版本号,cardNo:设备芯片卡号,lastStrategyId:最后策略更新ID，第一次为0,batchSize:批量条数
     */
    public static final String HOOK_UPDATE = "hookUpdate";

    /**
     * 反馈和帮助相关
     */
    public static final String FEED_BACK = "feedback";
    public static final String FEED_BACK_IMAGE = "feedbackimage";

    /**
     * 数据迁移判断新老账号
     */
    public static final String DATA_MIGRATION = "dataMigration";
    /**
     * 数据迁移完成
     */
    public static final String DATA_MIGRATION_FINISH = "dataMigrationFinihs";

    /**
     * 安全锁部分
     */
    /**
     * 从服务器上获取安全锁状态
     */
    public static final String GET_SAFELOCK_CLOUD_SETTINGS = "getSafeLockCloudSetting";
    /**
     * 从服务器上获取密码
     */
    public static final String GET_GESTURE_CLOUD = "getGestureCloud";
    /**
     * 向服务器上保存密码
     */
    public static final String SAVE_GESTURE_CLOUD = "saveGestureCloud";

    /**
     * 获取安全锁模式,fill方法参数为：context
     */
    public static final String GET_SAFELOCK_SETTINGS = "getSafeLockSettings";
    /**
     * 设置安全锁设置,fill方法参数为：context
     */
    public static final String SET_SAFELOCK_SETTINGS = "setSafeLockSettings";

}
