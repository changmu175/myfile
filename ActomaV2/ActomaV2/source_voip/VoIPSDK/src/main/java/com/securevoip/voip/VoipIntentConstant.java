package com.securevoip.voip;

import com.csipsimple.api.SipManager;

/**
 * 内部接口Intent传递的Action常量值定义
 * Created by zjc on 2015/7/24.
 */
public class VoipIntentConstant {

    /**
     * 账户
     */
    public final static String VOIP_ACCOUNT = "actom_account";
    /**
     * Ticket认证信息
     */
    public final static String VOIP_TICKET = "ticket";
    /**
     * 昵称
     */
    public final static String VOIP_DISPLAY_NAME = "nickname";

    /**
     * 注册登录
     */
    public final static String VOIP_REGIST = SipManager.ACTION_SIP_REGISTRATION_CHANGED;
    /**
     * 注销登出
     */
    public final static String VOIP_UNREGIST = SipManager.ACTION_SIP_ACCOUNT_DELETED;
    /**
     * 添加账户
     */
    public final static String VOIP_ADD_ACCOUNT = "";
    /**
     * 删除账户
     */
    public final static String VOIP_REMOVE_ACCOUNT = "voip_delete_account";
    /**
     * 拨打加密电话
     */
    public final static String VOIP_MAKE_CALL = "voip_make_call";
    /**
     * 获取登录状态
     */
    public final static String VOIP_GET_STATE = "";
    /**
     * 启动SipService
     */
    public final static String VOIP_START_SIP_SERVICVE = "voip_start_sip_service";

/*    public final static String VOIP_ADD_CALLLOG = "";

    public final static String VOIP_DELETE_CALLLOG = "";

    public final static String VOIP_GET_CALLLOG_DETAIL = "";

    public final static String VOIP_SEARCH_CALLLOG = "";*/

}
