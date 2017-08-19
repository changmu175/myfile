
package com.csipsimple.api;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.csipsimple.db.DBProvider;

/**
 * Manage SIP application globally <br/>
 * Define intent, action, broadcast, extra constants <br/>
 * It also define authority and uris for some content holds by the internal
 * database
 */
public final class SipManager {
    // -------
    // Static constants
    // PERMISSION
    /**
     * Permission that allows to use sip : place call, control call etc.
     */
    public static final String PERMISSION_USE_SIP = "android.permission.ACTOMA_USE_SIP";
    /**
     * Permission that allows to configure sip engine : preferences, accounts.
     */
    public static final String PERMISSION_CONFIGURE_SIP = "android.permission.ACTOMA_CONFIGURE_SIP";

    // SERVICE intents
    /**
     * Used to bind sip service to configure it.<br/>
     * This method has been deprected and should not be used anymore. <br/>
     * Use content provider approach instead
     *
     * @see SipConfigManager
     */
    public static final String INTENT_SIP_CONFIGURATION = "com.csipsimple.service.SipConfiguration";
    /**
     * Bind sip service to control calls.<br/>
     * If you start the service using {@link android.content.Context#startService(android.content.Intent intent)}
     * , you may want to pass {@link #EXTRA_OUTGOING_ACTIVITY} to specify you
     * are starting the service in order to make outgoing calls. You are then in
     * charge to unregister for outgoing calls when user finish with your
     * activity or when you are not anymore in calls using
     * {@link #ACTION_OUTGOING_UNREGISTER}<br/>
     * If you actually make a call or ask service to do something but wants to
     * unregister, you must defer unregister of your activity using
     * {@link #ACTION_DEFER_OUTGOING_UNREGISTER}.
     *
     * @see #EXTRA_OUTGOING_ACTIVITY
     */
    public static final String INTENT_SIP_SERVICE = "com.csipsimple.service.SipService";

    /**
     * Shortcut to turn on / off a sip account.
     * <p>
     * Expected Extras :
     * <ul>
     * <li>{@link SipProfile#FIELD_ID} as Long to choose the account to
     * activate/deactivate</li>
     * <li><i>{@link SipProfile#FIELD_ACTIVE} - optional </i> as boolean to
     * choose if should be activated or deactivated</li>
     * </ul>wx
     * </p>
     */
    public static final String INTENT_SIP_ACCOUNT_ACTIVATE = "com.xdja.voip.accounts.activate";

    /**
     * Scheme for csip uri.
     */
    public static final String PROTOCOL_CSIP = "csip";
    /**
     * Scheme for sip uri.
     */
    public static final String PROTOCOL_SIP = "sip";
    /**
     * Scheme for sips (sip+tls) uri.
     */
    public static final String PROTOCOL_SIPS = "sips";
    // -------
    // ACTIONS
    /**
     * Action launched when a sip call is ongoing.
     * <p>
     * Provided extras :
     * <ul>
     * <li>{@link #EXTRA_CALL_INFO} a {@link SipCallSession} containing infos of
     * the call</li>
     * </ul>
     * </p>
     */
    public static final String ACTION_SIP_CALL_UI = "com.xdja.voip.phone.action.INCALL";
    /**
     * Action launched when the status icon clicked.<br/>
     * Should raise the dialer.
     */
    public static final String ACTION_SIP_DIALER = "com.csipsimple.phone.action.DIALER";
    /**
     * Action launched when a missed call notification entry is clicked.<br/>
     * Should raise call logs list.
     */
    public static final String ACTION_SIP_CALLLOG = "com.csipsimple.phone.action.CALLLOG";
    /**
     * Action launched when user want to go in sip favorites.
     * Should raise the sip favorites view.
     */
    public static final String ACTION_SIP_FAVORITES = "com.csipsimple.phone.action.FAVORITES";
    /**
     * Action launched to enter fast settings.<br/>
     */
    public static final String ACTION_UI_PREFS_FAST = "com.csipsimple.ui.action.PREFS_FAST";
    /**
     * Action launched to enter global csipsimple settings.<br/>
     */
    public static final String ACTION_UI_PREFS_GLOBAL = "com.csipsimple.ui.action.PREFS_GLOBAL";

    // SERVICE BROADCASTS
    /**
     * 电话状态改变
     * Broadcast sent when call state has changed.
     * <p>
     * Provided extras :
     * <ul>
     * <li>{@link #EXTRA_CALL_INFO} a {@link SipCallSession} containing infos of
     * the call</li>
     * </ul>
     * </p>
     */
    public static final String ACTION_SIP_CALL_CHANGED = "com.xdja.voip.service.CALL_CHANGED";
    /**
     * Broadcast sent when sip account has been changed.
     * <p>
     * Provided extras :
     * <ul>
     * <li>{@link SipProfile#FIELD_ID} the long id of the account</li>
     * </ul>
     * </p>
     */
    public static final String ACTION_SIP_ACCOUNT_CHANGED = "com.xdja.voip.service.ACCOUNT_CHANGED";


    // 重新添加默认账号 xjq 2015-09-12
    public static final String ACTION_SIP_READD_DEFAULT_ACCOUNT = "com.actoma.service.READD_DEFAULT_ACCOUNT";
    /**
     * Broadcast sent when a sip account has been deleted
     * <p>
     * <ul>
     * <li>{@link SipProfile#FIELD_ID} the long id of the account</li>
     * </ul>
     * </p>
     */
    public static final String ACTION_SIP_ACCOUNT_DELETED = "com.xdja.voip.service.ACCOUNT_DELETED";
    /**
     * Broadcast sent when sip account registration has changed.
     * <p>
     * Provided extras :
     * <ul>
     * <li>{@link SipProfile#FIELD_ID} the long id of the account</li>
     * </ul>
     * </p>
     */

    public static final String ACTION_SIP_REGISTRATION_CHANGED = "com.xdja.voip.service.REGISTRATION_CHANGED";
    /**
     * Broadcast sent when the state of device media has been changed.
     */
    public static final String ACTION_SIP_MEDIA_CHANGED = "com.xdja.voip.service.MEDIA_CHANGED";
    /**
     * Broadcast sent when a ZRTP SAS
     */
    public static final String ACTION_ZRTP_SHOW_SAS = "com.xdja.voip.service.SHOW_SAS";
    /**
     * Broadcast sent when a message has been received.<br/>
     * By message here, we mean a SIP SIMPLE message of the sip simple protocol. Understand a chat / im message.
     */
    public static final String ACTION_SIP_MESSAGE_RECEIVED = "com.csipsimple.service.MESSAGE_RECEIVED";
    /**
     * Broadcast sent when a conversation has been recorded.<br/>
     * <p>
     * Provided extras :
     * <ul>
     * <li>{@link SipManager#EXTRA_FILE_PATH} the path to the recorded file</li>
     * <li>{@link SipManager#EXTRA_CALL_INFO} the information on the call recorded</li>
     * </ul>
     * </p>
     */
    public static final String ACTION_SIP_CALL_RECORDED = "com.csipsimple.service.CALL_RECORDED";

    // REGISTERED BROADCASTS
    /**
     * Broadcast to send when the sip service can be stopped.
     */
    public static final String ACTION_SIP_CAN_BE_STOPPED = "com.xdja.voip.service.ACTION_SIP_CAN_BE_STOPPED";
    /**
     * Broadcast to send when the sip service should be restarted.
     */
    public static final String ACTION_SIP_REQUEST_RESTART = "com.xdja.voip.service.ACTION_SIP_REQUEST_RESTART";
    /**
     * Broadcast to send when your activity doesn't allow anymore user to make outgoing calls.<br/>
     * You have to pass registered {@link #EXTRA_OUTGOING_ACTIVITY}
     *
     * @see #EXTRA_OUTGOING_ACTIVITY
     */
    public static final String ACTION_OUTGOING_UNREGISTER = "com.xdja.voip.service.ACTION_OUTGOING_UNREGISTER";

    /**
     * 发送网络重新连接的广播（Mate8动态注册的网络监听广播上无法收到网络连接的消息）xjq 2016-03-25
     */
    public static final String ACTION_NETWORK_CONNECT = "com.xdja.voip.ACTION_NETWORK_CONNECT";

    /**
     * 停止sip service xjq
     */
    public static final String ACTION_STOP_SIPSERVICE = "com.xdja.voip.ACTION_STOP_SIPSERVICE";

    /**u
     * 建立广播发送标示，为其他地方调用打电话提供标示。
     * 打电话直接通过广播发送，告诉sipService服务，服务自行调用打电话功能。
     *
     */
    public static final String ACTION_SIP_CALLING = "com.xdja.voip.ACTION_SIP_CALLING";

    // 安通+呼叫使用唯一的action标识 xjq 2015-11-12
    public static final String ACTION_SIP_ACTOMA_CALLING = "com.xdja.voip.ACTION_SIP_ACTOMA_CALLING";
    // 暂时添加一个新的广播标志 xjq
    public static final String ACTION_SIP_CALLING2 = "com.xdja.voip.ACTION_SIP_CALLLING2";

    // VoIP服务器地址发生变化 xjq 2016-01-11
    public static final String ACTION_SIP_SERVER_ADDR_CHANGED = "com.xdja.voip.ACTION_SIP_SERVER_ADDR_CHANGED";
    /**
     * Broadcast to send when you have launched a sip action (such as make call), but that your app will not anymore allow user to make outgoing calls actions.<br/>
     * You have to pass registered {@link #EXTRA_OUTGOING_ACTIVITY}
     *
     * @see #EXTRA_OUTGOING_ACTIVITY
     */
    public static final String ACTION_DEFER_OUTGOING_UNREGISTER = "com.xdja.voip.service.ACTION_DEFER_OUTGOING_UNREGISTER";

    // PLUGINS BROADCASTS
    /**
     * Plugin action for themes.
     */
    public static final String ACTION_GET_DRAWABLES = "com.csipsimple.themes.GET_DRAWABLES";
    /**
     * Plugin action for call handlers.<br/>
     * You can expect {@link android.content.Intent#EXTRA_PHONE_NUMBER} as argument for the
     * number to call. <br/>
     * Your receiver mu
     * {@link android.content.BroadcastReceiver#getResultExtras(boolean)} with parameter true to
     * fill response. <br/>
     * Your response contains :
     * <ul>
     * <li>{@link android.content.Intent#EXTRA_SHORTCUT_ICON} with
     * {@link android.graphics.Bitmap} (mandatory) : Icon representing the call
     * handler</li>
     * <li>{@link android.content.Intent#EXTRA_TITLE} with
     * {@link java.lang.String} (mandatory) : Title representing the call
     * handler</li>
     * <li>{@link android.content.Intent#EXTRA_REMOTE_INTENT_TOKEN} with
     * {@link android.app.PendingIntent} (mandatory) : The intent to fire when
     * this action is choosen</li>
     * <li>{@link android.content.Intent#EXTRA_PHONE_NUMBER} with
     * {@link java.lang.String} (optional) : Phone number if the pending intent
     * launch a call intent. Empty if the pending intent launch something not
     * related to a GSM call.</li>
     * </ul>
     */
    public static final String ACTION_GET_PHONE_HANDLERS = "com.csipsimple.phone.action.HANDLE_CALL";

    /**
     * Plugin action for call management extension. <br/>
     * Any app that register this plugin and has rights to {@link #PERMISSION_USE_SIP} will appear 
     * in the call cards. <br/>
     * The activity entry in manifest may have following metadata
     * <ul>
     * <li>{@link #EXTRA_SIP_CALL_MIN_STATE} minimum call state for this plugin to be active. Default {@link SipCallSession.InvState#EARLY}.</li>
     * <li>{@link #EXTRA_SIP_CALL_MAX_STATE} maximum call state for this plugin to be active. Default {@link SipCallSession.InvState#CONFIRMED}.</li>
     * <li>{@link #EXTRA_SIP_CALL_CALL_WAY} bitmask flag for selecting only one way. 
     *  {@link #BITMASK_IN} for incoming; 
     *  {@link #BITMASK_OUT} for outgoing.
     *  Default ({@link #BITMASK_IN} | {@link #BITMASK_OUT}) (any way).</li>
     * </ul> 
     * Receiver activity will get an extra with key {@value #EXTRA_CALL_INFO} with a {@link SipCallSession}.
     */
    public static final String ACTION_INCALL_PLUGIN = "com.csipsimple.sipcall.action.HANDLE_CALL_PLUGIN";

    public static final String EXTRA_SIP_CALL_MIN_STATE = "com.csipsimple.sipcall.MIN_STATE";
    public static final String EXTRA_SIP_CALL_MAX_STATE = "com.csipsimple.sipcall.MAX_STATE";
    public static final String EXTRA_SIP_CALL_CALL_WAY = "com.csipsimple.sipcall.CALL_WAY";

    /** Begin add by xjq 20140918 增加自定义会话状态变化广播action **/
    public static final String ACTION_XDJA_SIP_CHANGED = "com.xdja.voip.SIP_STATE";
    /** End add by xjq 20140918 增加自定义会话状态变化广播action **/

    /**
     * Bitmask to keep media/call coming from outside
     */
    @SuppressLint("PointlessBitwiseExpression")
    public final static int BITMASK_IN = 1 << 0;
    /**
     * Bitmask to keep only media/call coming from the app
     */
    public final static int BITMASK_OUT = 1 << 1;
    /**
     * Bitmask to keep all media/call whatever incoming/outgoing
     */
    public final static int BITMASK_ALL = BITMASK_IN | BITMASK_OUT;

    /**
     * Plugin action for rewrite numbers. <br/>     
     * You can expect {@link android.content.Intent#EXTRA_PHONE_NUMBER} as argument for the
     * number to rewrite. <br/>
     * Your receiver must
     * {@link android.content.BroadcastReceiver#getResultExtras(boolean)} with parameter true to
     * fill response. <br/>
     * Your response contains :
     * <ul>
     * <li>{@link android.content.Intent#EXTRA_PHONE_NUMBER} with
     * {@link java.lang.String} (optional) : Rewritten phone number.</li>
     * </ul>
     */
    public final static String ACTION_REWRITE_NUMBER = "com.csipsimple.phone.action.REWRITE_NUMBER";
    /**
     * Plugin action for audio codec.
     */
    public static final String ACTION_GET_EXTRA_CODECS = "com.xdja.voip.codecs.action.REGISTER_CODEC";
    /**
     * Plugin action for video codec.
     */
    public static final String ACTION_GET_EXTRA_VIDEO_CODECS = "com.csipsimple.codecs.action.REGISTER_VIDEO_CODEC";
    /**
     * Plugin action for video.
     */
    public static final String ACTION_GET_VIDEO_PLUGIN = "com.csipsimple.plugins.action.REGISTER_VIDEO";
    /**
     * Meta constant name for library name.
     */
    public static final String META_LIB_NAME = "lib_name";
    /**
     * Meta constant name for the factory name.
     */
    public static final String META_LIB_INIT_FACTORY = "init_factory";
    /**
     * Meta constant name for the factory deinit name.
     */
    public static final String META_LIB_DEINIT_FACTORY = "deinit_factory";

    // Content provider
    /**
     * Authority for regular database of the application.
     */
    /** 20170222-mengbo-start: 解决同一手机安装不同版本客户端,provider异常 **/
    //public static final String AUTHORITY = "com.actoma.csipsimple.db";
    /** 20170222-mengbo-end **/

    /**
     * Base content type for csipsimple objects.
     */
    public static final String BASE_DIR_TYPE = "vnd.android.cursor.dir/vnd.csipsimple";
    /**
     * Base item content type for csipsimple objects.
     */
    public static final String BASE_ITEM_TYPE = "vnd.android.cursor.item/vnd.csipsimple";

    // Content Provider - call logs
    /**
     * Table name for call logs.
     */
    public static final String CALLLOGS_TABLE_NAME = "calllogs";

    public static final String CALLLOGS = "calllog";


    // 第三方账户体系 xjq 2015-12-10
    public static final String CUST_ACC = "cust_acc";
    public static final String CUST_ACC_NAME = "cust_acc_name";
    public static final String CUST_ACC_TICKET = "cust_acc_ticket";
    //public static final Uri CUST_ACC_URI = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + CUST_ACC);

    // 自定义header，可以用来重写默认header或者用新的header xjq 2015-
    public static final String HDR_EXTRA_CALL_ID = "X-callid";

    // 安通+加密密钥因子 xjq 2016
    public static final String HDR_EXTRA_KEY_FACTOR = "X-factor";

    // 安通+ ticket数据 xjq 2016
    public static final String HDR_TICKET = "Ticket";

    // 网络类型 xjq
    public static final String NETWORK_TYPE = "network_type";

    /**
     * Content type for call logs provider.
     */
    public static final String CALLLOG_CONTENT_TYPE = BASE_DIR_TYPE + ".calllog";

    /**
     * Item type for call logs provider.
     */
    public static final String CALLLOG_CONTENT_ITEM_TYPE = BASE_ITEM_TYPE + ".calllog";

    /**
     * Uri for call log content provider.
     */
    //public static final Uri CALLLOG_URI = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + CALLLOGS_TABLE_NAME);

    /**
     * 自定义的通话记录URI，在自定义的ContentProvider中通过SQL来去重查询通话记录
     */
    //public static final Uri CUST_CALLLOG_URI = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + CALLLOGS);

    /**
     * Base uri for a specific call log. Should be appended with id of the call log.
     */
    //public static final Uri CALLLOG_ID_URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + CALLLOGS_TABLE_NAME + "/");
	
    // -- Extra fields for call logs
    /**
     * The account used for this call
     */
    public static final String CALLLOG_PROFILE_ID_FIELD = "account_id";
    /**
     * The final latest status code for this call.
     */
    public static final String CALLLOG_STATUS_CODE_FIELD = "status_code";
    /**
     * The final latest status text for this call.
     */
    public static final String CALLLOG_STATUS_TEXT_FIELD = "status_text";

    // Content Provider - filter
    /**
     * Table name for filters/rewriting rules.
     */
    public static final String FILTERS_TABLE_NAME = "outgoing_filters";
    /**
     * Content type for filter provider.
     */
    public static final String FILTER_CONTENT_TYPE = BASE_DIR_TYPE + ".filter";
    /**
     * Item type for filter provider.
     */
    public static final String FILTER_CONTENT_ITEM_TYPE = BASE_ITEM_TYPE + ".filter";
    /**
     * Uri for filters provider.
     */
    //public static final Uri FILTER_URI = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + FILTERS_TABLE_NAME);
    /**
     * Base uri for a specific filter. Should be appended with filter id.
     */
    //public static final Uri FILTER_ID_URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT
    //        + "://" + SipManager.AUTHORITY + "/" + FILTERS_TABLE_NAME + "/");


    // 第三方账户体系provider类型 xjq 2015-12-10
    public final static String CUST_ACC_TYPE = "cust_acc_type";
    // EXTRAS
    /**
     * Extra key to contains infos about a sip call.<br/>
     * @see SipCallSession
     */
    public static final String EXTRA_CALL_INFO = "call_info";

    /**
     * 标示电话号码字段
     */
    public static final String CALL_NUM= "call_num";

    /**
     * 标示账户ID
     */
    public static final String ACCOUNT_ID= "account_id";

    /**
     * 建立广播发送标示，为其他地方调用打电话提供标示。
     * 打电话直接通过广播发送，告诉sipService服务，服务自行调用打电话功能。
     *
     */

    /**
     * Tell sip service that it's an user interface requesting for outgoing call.<br/>
     * It's an extra to add to sip service start as string representing unique key for your activity.<br/>
     * We advise to use your own component name {@link android.content.ComponentName} to avoid collisions.<br/>
     * Each activity is in charge unregistering broadcasting {@link #ACTION_OUTGOING_UNREGISTER} or {@link #ACTION_DEFER_OUTGOING_UNREGISTER}<br/>
     *
     * @see android.content.ComponentName
     */
    public static final String EXTRA_OUTGOING_ACTIVITY = "outgoing_activity";

    /**
     * Extra key to contain an string to path of a file.<br/>
     * @see java.lang.String
     */
    public static final String EXTRA_FILE_PATH = "file_path";


    /**
     * NickName, from contacts
     */
    public static final String CALLLOG_NICKNAME = "nickname";


    /**
     * NickName's short name, from contacts
     */
    public static final String CALLLOG_NICKNAME_PY = "nickname_py";

    /**
     * NickName's full pinyin name, from contacts
     */
    public static final String CALLLOG_NICKNAME_PYF = "nickname_pyfull";
    /**
     * This contact's photo, from contacts
     */
    public static final String CALLLOG_AVATAR_URL = "photo_uri";
    /**
     * Extra key to contain behavior of outgoing call chooser activity.<br/>
     * In case an account is specified in the outgoing call intent with {@link SipProfile#FIELD_ACC_ID}
     * and the application doesn't find this account,
     * this extra parameter allows to determine what is the fallback behavior of
     * the activity. <br/>
     * By default {@link #FALLBACK_ASK}.
     * Other options : 
     */
    public static final String EXTRA_FALLBACK_BEHAVIOR = "fallback_behavior";
    /**
     * Parameter for {@link #EXTRA_FALLBACK_BEHAVIOR}.
     * Prompt user with other choices without calling automatically.
     */
    public static final int FALLBACK_ASK = 0;
    /**
     * Parameter for {@link #EXTRA_FALLBACK_BEHAVIOR}.
     * Warn user about the fact current account not valid and exit.
     * WARNING : not yet implemented, will behaves just like {@link #FALLBACK_ASK} for now
     */
    public static final int FALLBACK_PREVENT = 1;
    /**
     * Parameter for {@link #EXTRA_FALLBACK_BEHAVIOR}
     * Automatically fallback to any other available account in case requested sip profile is not there.
     */
    public static final int FALLBACK_AUTO_CALL_OTHER = 2;

    // Constants
    /**
     * Constant for success return
     */
    public static final int SUCCESS = 0;
    /**
     * Constant for network errors return
     */
    public static final int ERROR_CURRENT_NETWORK = 10;

    /**
     * Possible presence status.
     */
    public enum PresenceStatus {
        /**
         * Unknown status
         */
        UNKNOWN,
        /**
         * Online status
         */
        ONLINE,
        /**
         * Offline status
         */
        OFFLINE,
        /**
         * Busy status
         */
        BUSY,
        /**
         * Away status
         */
        AWAY,
    }

    /**
     * Current api version number.<br/>
     * Major version x 1000 + minor version. <br/>
     * Major version are backward compatible.
     */
    public static final int CURRENT_API = 2004;

    private static Uri custAccUri = null;

    /**
     * Uri for call log content provider.
     */
    private static Uri calllogUri = null;

    /**
     * 自定义的通话记录URI，在自定义的ContentProvider中通过SQL来去重查询通话记录
     */
    private static Uri custCalllogUri = null;

    /**
     * Base uri for a specific call log. Should be appended with id of the call log.
     */
    private static Uri baseCalllogIdUri = null;

    /**
     * Uri for filters provider.
     */
    private static Uri filterUri = null;

    /**
     * Base uri for a specific filter. Should be appended with filter id.
     */
    private static Uri baseFilterIdUri = null;

    /**
     * Ensure capability of the remote sip service to reply our requests <br/>
     *
     * @param service the bound service to check
     * @return true if we can safely use the API
     */
    public static boolean isApiCompatible(ISipService service) {
        if (service != null) {
            try {
                int version = service.getVersion();
                return (Math.floor(version / 1000) == Math.floor(CURRENT_API % 1000));
            } catch (RemoteException e) {
                // We consider this is a bad api version that does not have
                // versionning at all
                return false;
            }
        }

        return false;
    }

    public static Uri getCustAccUri(Context context) {
        if(custAccUri == null || custAccUri.equals(Uri.EMPTY)){
            if(context != null){
                custAccUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + CUST_ACC);
            }else{
                custAccUri = Uri.EMPTY;
            }
        }
        return custAccUri;
    }

    public static Uri getCalllogUri(Context context) {
        if(calllogUri == null || calllogUri.equals(Uri.EMPTY)){
            if(context != null){
                calllogUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + CALLLOGS_TABLE_NAME);
            }else{
                calllogUri = Uri.EMPTY;
            }
        }
        return calllogUri;
    }

    public static Uri getCustCalllogUri(Context context) {
        if(custCalllogUri == null || custCalllogUri.equals(Uri.EMPTY)){
            if(context != null){
                custCalllogUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + CALLLOGS);
            }else{
                custCalllogUri = Uri.EMPTY;
            }
        }
        return custCalllogUri;
    }

    public static Uri getBaseCalllogIdUri(Context context) {
        if(baseCalllogIdUri == null || baseCalllogIdUri.equals(Uri.EMPTY)){
            if(context != null){
                baseCalllogIdUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + CALLLOGS_TABLE_NAME + "/");
            }else{
                baseCalllogIdUri = Uri.EMPTY;
            }
        }
        return baseCalllogIdUri;
    }

    public static Uri getFilterUri(Context context) {
        if(filterUri == null || filterUri.equals(Uri.EMPTY)){
            if(context != null){
                filterUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + FILTERS_TABLE_NAME);
            }else{
                filterUri = Uri.EMPTY;
            }
        }
        return filterUri;
    }

    public static Uri getBaseFilterIdUri(Context context) {
        if(baseFilterIdUri == null || baseFilterIdUri.equals(Uri.EMPTY)){
            if(context != null){
                baseFilterIdUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + FILTERS_TABLE_NAME + "/");
            }else{
                baseFilterIdUri = Uri.EMPTY;
            }
        }
        return baseFilterIdUri;
    }

}
