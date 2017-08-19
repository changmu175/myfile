package com.xdja.comm.uitl;

/**
 * Created by liyingqing on 15-9-2.
 */
public class NotifiParamUtil {

    //[S]add by xienana for notification id @2016/10/11 [review by tangsha]
    /**
     * 联系人消息提醒ID
     */
    public static final int CONTACT_NOTIFI_ID = 100;

    /**
     * PostGlobalLife类里面的消息提醒ID，目前用于下线通知和解绑通知
     * 此通知ID已在 BasePresenterActivity 里面使用，请勿重复使用id,此处仅做提醒
     */
    //public static final int POST_GlOBAL_NOTIFI_ID  = 0x00011;

    /**
     * 安通+升级消息提醒ID
     */
    public static final int ANTONG_UPDATE_NOTIFI_ID = 0x10010;

    /**
     * 三方加密服务消息提醒ID
     */
    public static final int ENCRYPT_3_SERVICE_NOTIFI_ID = 0x10011;

    /**
     * VOIP消息提醒ID
     */
    public static final int REGISTER_NOTIF_ID = 10000;

    public static final int CALL_NOTIF_ID = REGISTER_NOTIF_ID + 1;

    public static final int CALLLOG_NOTIF_ID = REGISTER_NOTIF_ID + 2;

    public static final int MESSAGE_NOTIF_ID = REGISTER_NOTIF_ID + 3;

    public static final int VOICEMAIL_NOTIF_ID = REGISTER_NOTIF_ID + 4;

    public static final int MISSED_CALL_NOTIF_ID = REGISTER_NOTIF_ID + 5;

    //[E]add by xienana for notification id @2016/10/11 [review by tangsha]
}
