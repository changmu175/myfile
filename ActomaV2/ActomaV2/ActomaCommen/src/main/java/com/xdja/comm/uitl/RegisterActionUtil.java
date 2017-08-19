package com.xdja.comm.uitl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * Created by wanghao on 2015/11/3.
 * 定义action 规则
 *
 * ACTION ： action 大写_功能描述
 *
 */
public class RegisterActionUtil {


    public static final String ACTION_SELECTED_OPEN_TRANSFER = "com.xdja.actom.contact.selected";

    public static final String ACTION_OPEN_TRANSFER = "com.xdja.actoma.uitl.ENCRYPT_SERVICE";

    //public static final String ACTION_FRIEND_DOWNLOAD_SUCCESS = "com.xdja.actoma.friend.download.success";

    public static final String ACTION_REQUEST_DOWNLOAD_SUCCESS = "com.xdja.actoma.friend.request.download.success";

    public static final String ACTION_GROUP_DOWNLOAD_SUCCESS = "com.xdja.actoma.group.download.success";

    public static final String ACTION_ACCOUNT_DOWNLOAD_SUCCESS = "com.xdja.actoma.account.download.success";

    public static final String ACTION_DEPARTMENT_DOWNLOAD_SUCCESS = "com.xdja.actoma.department.download.success";

    public static final String ACTION_CHANGE_GROUP_NAME = "CHANGE_GROUP_NAME";

    //public static final String ACTION_ALARM_NOTIFY = "com.xdja.actoma.alarm.notify";

    public static final String ACTION_TASK_ALL_REMOVE = "com.xdja.actoma.task.all.remove";

    public static final String ACTION_CLOSE_MENU = "com.xdja.friendlist";

    public static final String ACTION_FRIEND_PULL_UPDATE = "com.xdja.actom.contact.update";

    public static final String ACTION_REFRESH_LIST = "com.xdja.actom.contact.refresh";

    public static final String ACTION_FRIEND_REQUEST = "action_friend_request";
    //删除好友，关闭三方加密小盾牌
    public static final String ACTION_DELETE_FRIEND_CLOSE_TRANSFER = "com.xdja.actom.contact.delete.close.transafer";
    //删除集团通讯录成员,关闭三方小盾牌
    public static final String ACTION_DELETE_DEPARTMEMBER_CLOSE_TRANSFER ="com.xdja.actoma.contact.delete.member.transfer";

    public static final String ACTION_OPEN_FRAME_SWITCH = "com.xdja.actoma.contact.openFrameSwitch";
    //收到删除人员的推送  关闭加密通道(删除的人正好是当前选中的人)
    public static final String ACTION_PUSH_CLOSE_TRANSFER = "com.xdja.actoma.close.encrypt";

    public  static final String ACTION_TO_VIEWPAGER_FIRST = "TO_VIEWPAGER_FIRST";

    public static final String ACTION_FRIEND_HAS_DELETED = "com.xdja.actom.contact.delete";

    public static final String ACTION_AGREE_FRIEND_REQUEST = "com.xdja,actoma.agreefriendrequest";

    public static final String ACTION_ACTOMA_USE_SIP = "android.permission.ACTOMA_USE_SIP";

    public static final String ACTION_OPEN_FRAME_SAFETRANSFER = "com.xdja.contact.open_frame_transfer";

    public static  final String ACTION_CLOSE_FRAME_SAFETRANSFER = "com.xdja.contact.close_frame_transfer";

    public static final String ACTION_CHANGE_NICK_NAME = "com.xdja.contact.change_nick_name";
    //删除好友或者集团通讯录好友更改三方加密
    public static final String ACTION_DELETE_FRIEND_OR_DEPARTMEMBER = "com.xdja.contact.delete_friend_or_departmember";

    public static final String ACTION_REFRESH_TAB = "com.xdja.contact.refresh_tab";

    public static final String ACTION_CLOSE_TANSFER = "com.xdja.contact_close_tansfer";

    public static final String ACTION_CONTACT_CHANGE_NICK_NAME = "com.xdja.contact_change_nick_name";

    public static final String ACTION_CONTACT_GIVE_APP_NAME = "com.contactcommon.give.appName";


    public static final String ACTION_CONTACT_REFRESH_TAB_NAME = "com.contact.refresh.tab.name";


    public static final String ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS = "com.contact.accountinfo.download.success";



/*************************************************************************************************************************/

    public static final String EXTRA_KEY_ANTONG_FRIEND_DATA = "TAG_ANTONG_FRIEND_DATA";

    public static final String EXTRA_KEY_DATA_TYPE = "data_type";

    public static final String EXTRA_KEY_DATA_TYPE_ACCOUNT = "data_type_account";

    public static final String EXTRA_KEY_DATA_TYPE_WORK_ID = "data_type_work_id";

    public static final String EXTRA_KEY_DATA_TYPE_SERVER_SEARCH = "data_type_server_search";

    public static final String EXTRA_KEY_DATA_TYPE_SCAN_SEARH = "data_type_scan_search";

    public static final String EXTRA_KEY_DATA_TYPE_GROUP_MEMBER = "data_type_group_member";

    public static final String EXTRA_KEY_DATA_KEY = "data_key";
    //zjc 清除未接来电通知的广播
    public static final String EXTRA_KEY_CANCEL_ONE_MISSED_CALL_NOTIFICATION = "android.custom.clear_peer_notification";

    public static final String EXTRA_KEY_TAG_ACCOUNT_DATA = "TAG_ACCOUNT_DATA";

    public static final String EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATA = "com.xdja.contact.dataCache.group";

    public static final String EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATASOURCE_DATA = "com.xdja.contact.dataCache.friend";

    public static final String EXTRA_KEY_TYPE_GROUP = "group";

    public static final String EXTRA_KEY_TYPE_FRIEND_OR_MEMBER = "friendOrMember";
    //搜索展示数据的类型
    public static final String EXTRA_KEY_FLAG_DATA_SEARCH_TYPE = "FLAG_DATA_SEARCH_TYPE";
    //已有关键字
    public static final String EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD = "FLAG_DATA_TYPE_KEYWORD";

    public static final String EXTRA_KEY_DATA_ACCOUNT_ACCEPT = "DATA_ACCOUNT_ACCEPT";

    public static final String EXTRA_KEY_INTENT_FRIEND = "key_intent_friend";

    //好友数据传递标示
    public static final String EXTRA_KEY_TAG_FRIEND_DATA = "TAG_FRIEND_DATA";

    //add by ysp. catch app remove action. 20161017. begin
    public static final String UNINSTALL_SAFEKEY = "uninstallsafekeyserver";
    public static final String INSTALL_SAFEKEY = "installsafekeyserver";
    //add by ysp. catch app remove action. 20161017. end
	
    //Task 2632, modify for share and forward function by ycm at 20161101 [Begin]
    public static final String SHARE = "share";
    public static final String AVAURL = "avaUrl";
    public static final String NICK_NAME = "nickName";
    public static final String ACCOUNT = "account";
    public static final String GROUP_ID = "account";
    public static final int SEARCH_CONTACT = 2;
    public static final int SINLE_SESSION = 3;
    public static final int GROUP_SESSION = 4;
    public static final int HAND_OUT = 6;
    public static final String SHARE_FOR_CREATENEWSESSION = "share_for_create";//分享标志
    public static final String SHARE_FOR_MORECONTACT = "share_for_morecontact";//分享标志
    public static final String TALKERID = "talkerId";//分享标志
    public static final String TALKTYPE = "talkType";
    public static final String SELECT_ACCOUNT = "selectAccountList";
    public static final String SHARE_FILE = "file/";// modified by ycm 2016/12/22:[文件转发或分享]
    public static final String SHARE_MESSAGE_TYPE = "message_type";// modified by ycm 2016/12/22:[文件转发或分享]
    //Task 2632, modify for share and forward function by ycm at 20161101 [End]

    //注册打开安全通信的广播
    public static void registerOpenTransferAction(Context context,BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SELECTED_OPEN_TRANSFER);
        context.registerReceiver(receiver,filter);
    }

    public static void registerOpenSafeService(Context context,BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN_TRANSFER);
        context.registerReceiver(receiver,filter);
    }


}
