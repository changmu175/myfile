package com.xdja.imp.domain.model;

import android.support.annotation.IntDef;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp_domain.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imdemo</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/28</p>
 * <p>Time:11:47</p>
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161104
 * 2)Task 2632, modify for share and forward function by ycm at 20161130
 */
public class ConstDef {

    /**
     * 将消息状态转化为对应的文本信息
     *
     * @param state 消息状态
     * @return 对应的文本描述
     */
    public static String mapMsgState(@MsgState int state) {
        String des = ActomaController.getApp().getString(R.string.im_sending);//STATE_SENDING_DES;
        switch (state) {
            case STATE_SEND_FAILD:
                des = ActomaController.getApp().getString(R.string.im_send_fail);//STATE_SEND_FAILD_DES;
                break;
            case STATE_SEND_SUCCESS:
                des = ActomaController.getApp().getString(R.string.im_send_success);//STATE_SEND_SUCCESS_DES;
                break;
            case STATE_ARRIVE:
                des = ActomaController.getApp().getString(R.string.im_status_arrive);//STATE_ARRIVE_DES;
                break;
            case STATE_READED:
                des = ActomaController.getApp().getString(R.string.im_status_readed);//STATE_READED_DES;
                break;
            case STATE_DESTROY:
                des = ActomaController.getApp().getString(R.string.im_status_destroy);//STATE_DESTROY_DES;
                break;
            case STATE_DEFAULT:
                des = ActomaController.getApp().getString(R.string.im_default_status);//STATE_DEFAULT_DES;
                break;
            case STATE_SENDING:
                des = ActomaController.getApp().getString(R.string.im_sending);
                break;
            case STATE_DESTROYING:
                break;
            default:
                break;

        }
        return des;
    }
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {STATE_SEND_FAILD, STATE_SEND_SUCCESS,
            STATE_ARRIVE, STATE_READED, STATE_DESTROY,
            STATE_DEFAULT, STATE_SENDING,STATE_DESTROYING})
    public @interface MsgState {
    }
    /**
     * 默认状态
     */
    public static final int STATE_DEFAULT = -1;
    public static final String STATE_DEFAULT_DES = ActomaController.getApp().getString(R.string.im_default_status);
    /**
     * 消息发送失败
     */
    public static final int STATE_SEND_FAILD = 0;
    public static final String STATE_SEND_FAILD_DES = ActomaController.getApp().getString(R.string.im_send_fail);

    /**
     * 消息发送中
     */
    public static final int STATE_SENDING = 1;
    public static final String STATE_SENDING_DES = ActomaController.getApp().getString(R.string.im_sending);
    /**
     * 已发送
     */
    public static final int STATE_SEND_SUCCESS = 2;
    public static final String STATE_SEND_SUCCESS_DES = ActomaController.getApp().getString(R.string.im_send_success);
    /**
     * 已送达
     */
    public static final int STATE_ARRIVE = 3;
    public static final String STATE_ARRIVE_DES = ActomaController.getApp().getString(R.string.im_status_arrive);
    /**
     * 已阅读
     */
    public static final int STATE_READED = 4;
    public static final String STATE_READED_DES = ActomaController.getApp().getString(R.string.im_status_readed);
    /**
     * 已销毁
     */
    public static final int STATE_DESTROY = 5;
    public static final String STATE_DESTROY_DES = ActomaController.getApp().getString(R.string.im_status_destroy);

    public static final int STATE_DESTROYING = 6;

    /**
     * 消息提醒方式定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {REMIND_ALL, REMIND_NONE, REMIND_VIBOR, REMIND_VOIC})
    public @interface RemindType {
    }

    /**
     * 不提醒
     */
    public static final int REMIND_NONE = 0;
    /**
     * 声音提醒
     */
    public static final int REMIND_VOIC = 1;
    /**
     * 震动提醒
     */
    public static final int REMIND_VIBOR = 2;
    /**
     * 声音和震动提醒
     */
    public static final int REMIND_ALL = 3;

    /**
     * 会话详情消息类型定义
     * */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TYPE_PIC , TYPE_FILE , TYPE_TINY_VIDEO})
    public @interface SessionFileType{
    }
    /**
     * 会话详情图片文件
     * */
    public static final int TYPE_PIC = 1;
    /**
     * 会话详情普通文件
     * */
    public static final int TYPE_FILE = 2;
    /**
     * 会话详情小视频
     * */
    public static final int TYPE_TINY_VIDEO = 3;

    /**
     * 通用文件类型定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TYPE_VOICE, TYPE_VIDEO, TYPE_PHOTO, TYPE_APK, TYPE_TXT,
            TYPE_WORD, TYPE_PPT, TYPE_EXCEL, TYPE_PDF, TYPE_ZIP, TYPE_OTHER, TYPE_WEB})
    public @interface FileType {
    }

    /**
     * 音频文件
     */
    public static final int TYPE_VOICE = 1;
    /**
     * 视频文件
     */
    public static final int TYPE_VIDEO = 2;
    /**
     * 图片文件
     */
    public static final int TYPE_PHOTO = 3;
    /**
     * APK安装包
     */
    public static final int TYPE_APK = 4;
    /**
     * txt文本
     */
    public static final int TYPE_TXT = 5;
    /**
     * WORD文件
     */
    public static final int TYPE_WORD = 6;
    /**
     * PPT文件
     */
    public static final int TYPE_PPT = 7;
    /**
     * excel文件
     */
    public static final int TYPE_EXCEL = 8;
    /**
     * PDF文件
     */
    public static final int TYPE_PDF = 9;
    /**
     * 压缩文件
     */
    public static final int TYPE_ZIP = 10;

    /**
     * 普通文件
     * */
    public static final int TYPE_NORMAL = 12;
    /**
     * 其他类型
     */
    public static final int TYPE_OTHER = 11;
	/**
     * 网页类型
     */
    public static final int TYPE_WEB = 13;

    /**
     * 文件状态
     * */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {INACTIVE , DONE , LOADING , PAUSE , FAIL})
    public @interface FileState{};
    /**
     * 未开始
     * */
    public static final int INACTIVE = 1;
    /**
     * 完成
     * */
    public static final int DONE = 2;
    /**
     * 下载中
     * */
    public static final int LOADING = 3;
    /**
     * 暂停
     * */
    public static final int PAUSE = 4;
    /**
     * 失败
     * */
    public static final int FAIL = 5;
    /**
     * 刷新动作定义
     *
     * @see #ACTION_MODIFY
     * @see #ACTION_DELETE
     * @see #ACTION_ADD
     * @see #ACTION_REFRESH
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {ACTION_MODIFY, ACTION_DELETE, ACTION_ADD, ACTION_REFRESH})
    public @interface ActionRefCon {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {ACTION_MODIFY, ACTION_DELETE, ACTION_ADD})
    public @interface ActionRefMsg{}


    /**
     * 会话有修改
     */
    public static final int ACTION_MODIFY = 0;
    /**
     * 会话有删除
     */
    public static final int ACTION_DELETE = 1;
    /**
     * 会话有增加
     */
    public static final int ACTION_ADD = 2;
    /**
     * 整个会话刷新
     */
    public static final int ACTION_REFRESH = 3;

    /**
     * 根据会话类型定义获取会话类型文字描述
     *
     * @return 会话类型文字描述
     */
    public static String mapChatType(@ChatType int type) {
        String des = CHAT_TYPE_DEFAULT_DES;
        switch (type) {
            case CHAT_TYPE_DEFAULT:
                des = CHAT_TYPE_DEFAULT_DES;
                break;
            case CHAT_TYPE_P2P:
                des = CHAT_TYPE_P2P_DES;
                break;
            case CHAT_TYPE_P2G:
                des = CHAT_TYPE_P2G_DES;
                break;
            case CHAT_TYPE_P2M:
                des = CHAT_TYPE_P2M_DES;
                break;
            case CHAT_TYPE_ACTOMA:
                des = CHAT_TYPE_ACTOMA_DES;
                break;
            case CHAT_TYPE_PIC_SELECT:
                des = CHAT_TYPE_PIC_SELECT_DES;
                break;
            case CHAT_TYPE_PIC_PREVIEW:
                des = CHAT_TYPE_PIC_PREVIEW_DES;
                break;
            default:
                break;
        }
        return des;
    }

    /**
     * 会话类型定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {CHAT_TYPE_DEFAULT, CHAT_TYPE_P2P, CHAT_TYPE_P2G, CHAT_TYPE_P2M, CHAT_TYPE_ACTOMA,
            CHAT_TYPE_PIC_SELECT, CHAT_TYPE_PIC_PREVIEW})
    public @interface ChatType {
    }

    /**
     * 默认会话类型
     */
    public static final int CHAT_TYPE_DEFAULT = 0;
    public static final String CHAT_TYPE_DEFAULT_DES = ActomaController.getApp().getString(R.string.im_type_default);

    /**
     * 单人聊天
     */
    public static final int CHAT_TYPE_P2P = 1;
    public static final String CHAT_TYPE_P2P_DES = ActomaController.getApp().getString(R.string.im_single_chat);
    /**
     * 群组聊天
     */
    public static final int CHAT_TYPE_P2G = 2;
    public static final String CHAT_TYPE_P2G_DES = ActomaController.getApp().getString(R.string.im_group_chat);
    /**
     * 群发聊天
     */
    public static final int CHAT_TYPE_P2M = 3;
    public static final String CHAT_TYPE_P2M_DES = ActomaController.getApp().getString(R.string.im_group_send_chat);
    /**
     * AT+团队
     */
    public static final int CHAT_TYPE_ACTOMA = 4;
    public static final String CHAT_TYPE_ACTOMA_DES = ActomaController.getApp().getString(R.string.im_at_team);

    /**
     * 图片选择
     */
    public static final int CHAT_TYPE_PIC_SELECT = 5;
    public static final String CHAT_TYPE_PIC_SELECT_DES = ActomaController.getApp().getString(R.string.im_picture_select);

    /**
     * 图片预览
     */
    public static final int CHAT_TYPE_PIC_PREVIEW = 6;
    public static final String CHAT_TYPE_PIC_PREVIEW_DES = ActomaController.getApp().getString(R.string.im_picture_preview);

    /**
     * 自定义消息
     */
    public static final int CHAT_TYPE_CUSTOM = 999;
    public static final String CHAT_TYPE_CUSTOM_DES = ActomaController.getApp().getString(R.string.im_custom_message);

    /**
     * 消息类型定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MSG_TYPE_DEFAULT, MSG_TYPE_TEXT, MSG_TYPE_VOICE, MSG_TYPE_VIDEO, MSG_TYPE_WEB,
            MSG_TYPE_PHOTO, MSG_TYPE_FILE, MSG_TYPE_PRESENTATION})
    public @interface MsgType{}

    /**
     * 默认消息类型
     */
    public static final int MSG_TYPE_DEFAULT = -1;
    /**
     * 文本新消息
     */
    public static final int MSG_TYPE_TEXT = 0;

    /**
     * 语音消息
     */
    public static final int MSG_TYPE_VOICE = 1;

    /**
     * 视频消息
     */
    public static final int MSG_TYPE_VIDEO = 2;

    /**
     * 图片消息
     */
    public static final int MSG_TYPE_PHOTO = 3;

    /**
     * 文件消息
     */
    public static final int MSG_TYPE_FILE = 4;
	
    /**
     * 网页消息
     */
    public static final int MSG_TYPE_WEB = 13;
    /**
     * 提示信息
     */
    public static final int MSG_TYPE_PRESENTATION = 101;

    /**
     * 没有内容
     */
    public static final int MSG_TYPE_NO_CONTENT = 102;


    //fix bug 3371 by licong, reView by zya, 2016/8/29
    /**
     * 群组文件
     */
    public static final int MSG_TYPE_GROUP_FILE = 6;

    /**
     * 群组文本
     */
    public static final int MSG_TYPE_GROUP_TEXT = 5;
    //end by licong

    /**
     * 消息组合类型定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MSG_COMBINE_TYPE_P2P_PRESENTATION,MSG_COMBINE_TYPE_P2G_PRESENTATION})
    public @interface MsgCombineType{}

    /**
     * 单人聊天提示信息
     */
    public static final int MSG_COMBINE_TYPE_P2P_PRESENTATION=129;

    /**
     * 群组聊天提示信息
     */
    public static final int MSG_COMBINE_TYPE_P2G_PRESENTATION=133;


    /**
     * 漫游状态定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {ROAM_STATE_OPEN, ROAM_STATE_CLOSE})
    public @interface RoamState {
    }

    /**
     * 漫游开启
     */
    public static final int ROAM_STATE_OPEN = 1;
    /**
     * 漫游关闭
     */
    public static final int ROAM_STATE_CLOSE = 2;

    /**
     * 回调结果处理状态定义
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {CALLBACK_NOT_HANDLED, CALLBACK_HANDLED})
    public @interface CallBackHandleState {
    }

    /**
     * 回调函数未处理
     */
    public static final int CALLBACK_NOT_HANDLED = 0;
    /**
     * 回调函数已经处理
     */
    public static final int CALLBACK_HANDLED = 1;


    /**
     *登录信息需要的cardId
     */
    public static final String TAG_CARDID = "cardId";

    /**
     *登录信息需要的account
     */
    public static final String TAG_ACCOUNT = "account";

    /**
     *登录信息需要的ticket
     */
    public static final String TAG_TICKET = "ticket";


    /**
     * 获取聊天对象的的TAG
     */
    public static final String TAG_TALKERID = "talkerId";
    /**
     * 获取会话ID的TAG
     */
    public static final String TAG_TALKFLAG = "talkId";
    /**
     * 获取消息接收方的TAG
     */
    public static final String TAG_TO = "to";
    /**
     * 会话类型的TAG
     */
    public static final String TAG_TALKTYPE = "talkType";

    /**
     * 图片传递TAG
     */
    public static final String TAG_SELECTPIC = "selectPic";

    /**
     * 文件传递Tag
     */
    public static final String TAG_SELECTFILE = "selectFile";
    public static final String TAG_SELECTWEB = "selectWeb";
    /**
     * 短视频传递Tag
     */
    public static final String TAG_SELECTVIDEO = "selectVideo";

    /**
     * 所有图片传递TAG
     */
    public static final String TAG_ALLPIC = "allPic";

    /**是否是从预览按钮跳转*/
    public static final String FROM_PREVIEW_BTN = "isFromPreviewBtn";

    /**是否是从拍照界面跳转*/
    public static final String FROM_TAKE_PHOTO = "isFromTakephoto";

    /**当前会话的所有图片信息*/
    public static final String SEESION_FILE_INFOS = "sessionFileInfos";

    /**是否从聊天图片预览界面跳转*/
    public static final String FROM_CHAT_DETAIL = "isFromChatdetail";

    /**当前图片名称*/
    public static final String CUR_FILE = "curfile";

    /**当前文件位置*/
    public static final String CUR_POS = "curpos";
	
    public static final int FOR_RESULT_SEND = 1;

    /**
     * 图片索引值传递TAG
     */
    public static final String TAG_SELECTPIC_INDEX = "selectPic_index";

    /**
     * 配置文件名称
     */
//    public static final String PRONAME = "config.properties";
    public static String PRONAME = "";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {ALARM_TYPE_LIMITMSG_DESTORY,ALARM_TYPE_LIMITMSG_SHOW_BOMBANIM})
    public @interface AlarmType{}

    /**
     * 图片、小视频浏览界面，View类型
     * */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {IMAGE_ITEM , TINY_VIDEO_ITEM , NORMAL_ITEM})
    public @interface MediaType{};

    public static final int IMAGE_ITEM = 1;

    public static final int TINY_VIDEO_ITEM = 2;

    public static final int NORMAL_ITEM = 3;
    /**
     * 时限消息播放销毁动画
     */
    public static final int ALARM_TYPE_LIMITMSG_SHOW_BOMBANIM = 1;

    /**
     * 时限消息销毁
     */
    public static final int ALARM_TYPE_LIMITMSG_DESTORY = 2;

    /**
     * 闹铃类型
     */
    public static final String ALARM_TYPE = "alarmType";

    public static final String ALARM_MSG = "alarmMsg";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {NODISTURB_SETTING_SESSION_TYPE_SINGLE,NODISTURB_SETTING_SESSION_TYPE_GROUP})
    public @interface NoDisturbSettingSessionType{}

    /**
     * 勿扰模式设置单人会话类型
     */
    public static final int NODISTURB_SETTING_SESSION_TYPE_SINGLE = 1;
    /**
     * 勿扰模式设置群组会话类型
     */
    public static final int NODISTURB_SETTING_SESSION_TYPE_GROUP = 2;


    /**
     * 实际消息类型
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MESSAGE_TYPE_SEND_TEXT, MESSAGE_TYPE_RECEIVE_TEXT, MESSAGE_TYPE_SEND_IMAGE, MESSAGE_TYPE_RECEIVE_IMAGE,
            MESSAGE_TYPE_SEND_VOICE,MESSAGE_TYPE_RECEIVE_VOICE,MESSAGE_TYPE_SEND_FILE,MESSAGE_TYPE_RECEIVE_FILE,MESSAGE_TYPE_SEND_VIDEO,
			MESSAGE_TYPE_RECEIVE_VIDEO,MESSAGE_TYPE_PRESENTATION_TEXT,MESSAGE_TYPE_NO_CONTENT, MESSAGE_TYPE_SEND_WEB, MESSAGE_TYPE_RECEIVE_WEB})
    public @interface ChatDetailType {
    }


    /**
     * 发送的文本
     */
    public static final int MESSAGE_TYPE_SEND_TEXT = 0;

    /**
     * 接收的文本
     */
    public static final int MESSAGE_TYPE_RECEIVE_TEXT = 1;

    /**
     * 提醒文字
     */
    public static final int MESSAGE_TYPE_PRESENTATION_TEXT = 2;

    /**
     * 发送的語音
     */
    public static final int MESSAGE_TYPE_SEND_VOICE = 3;

    /**
     * 接收的語音
     */
    public static final int MESSAGE_TYPE_RECEIVE_VOICE = 4;

    /**
     * 发送的图片
     */
    public static final int MESSAGE_TYPE_SEND_IMAGE = 5;

    /**
     * 接收的图片
     */
    public static final int MESSAGE_TYPE_RECEIVE_IMAGE = 6;


    /**
     * 发送的文件
     */
    public static final int MESSAGE_TYPE_SEND_FILE = 7;

    /**
     * 接收的文件
     */
    public static final int MESSAGE_TYPE_RECEIVE_FILE = 8;



    /**
     * 没有内容的消息
     */
    public static final int MESSAGE_TYPE_NO_CONTENT = 13;

    /**
     * 发送的小视频
     */
    public static final int MESSAGE_TYPE_SEND_VIDEO = 9;

    /**
     * 接收的小视频
     */
    public static final int MESSAGE_TYPE_RECEIVE_VIDEO = 10;
	
    /**
     * 发送的网页
     */
    public static final int MESSAGE_TYPE_SEND_WEB = 11;
	
	/**
     * 接收的网页
     */
    public static final int MESSAGE_TYPE_RECEIVE_WEB = 12;

    /**
     * 会话拥有者ID
     */
    public static final String OWNERID = "user_id";

    /**
     * 消息ID
     */
    public static final String MESSAGEID = "messageId";

    /**
     * 文件信息
     */
    public static final String FILEINFO = "fileInfo";

    /**
     * 消息状态
     */
    public static final String MESSAGE_STATE = "messageState";

    /**
     * 列表上的索引值
     */
    public static final String LIST_POSITION = "listPosition";

    /**
     * 消息类型
     */
    public static final String MESSAGE_TYPE = "messageType";

    /**
     * 会话类型
     */
    public static final String CHAT_TYPE = "chatType";

    /**
     * 当前缩略图索引值
     */
    public static final String PAGER_INDEX = "pagerIndex";

    /**
     * 消息内容
     */
    public static final String TALK_MESSAGE_BEAN = "talkMessageBean";

    /**
     * 安通+团队通知URL
     */
    public static final String AN_TONG_NOTIFICATION_URL = "anTongUrl";

    /**
     * 选中的图片路径
     */
    public static final String EXTRA_IMAGE_URLS = "selected_image_urls";

    /**
     * 选中的图片对象
     */
    public static final String EXTRA_IMAGE_URL_BEANS = "selected_image_url_beans";

    /**
     * 是否是闪信
     */
    public static final String LIMIT_MESSAGE = "limitMessage";

    /**
     * 语音播放状态
     */
    public static final String VOICE_PLAY_STATE = "voicePlayState";

    /**
     * 删除联系人时传参key
     */
    public static final String CONTACT_ACCOUNT = "account";

    /**
     * 联系人备注
     */
    public static final String CONTACT_REMARK = "showName";

    /**
     * 是否需要删除原文件
     */
    public static final String ISDELETEFILE_AFTER_ENTRY = "isDeleteSrcFileAfterEntry";

    /**
     * 窗体标题
     */
    public static final String ACTIVITY_TITLE = "activityTitle";

    /**
     * 要跳转到的页面
     */
    public static final String ARG_PAGE_INDEX = "pageIndex";

    /**
     * 通知栏点击标记
     */
    public static final String FLAG = "flag";

    /**
     * 通知类型
     */
    public static final String NOTIFITYPE = "notifiType";

    /**
     * 安通+团队消息已导入标记
     */
    public static final String HAS_LOAD_ANTONG_MSG = "loadAntongteanMessage";

    /**
     * 语音信息气泡长度DP值
     */
    public static final int MIN_DP=80;
    public static final int FIRST_STEP_DP=140;
    public static final int SECOND_STEP_DP=180;
    public static final int MAX_DP=200;
    /**
     *同步时间差Key
     */
    public static final String KEY_TIME_DIFFERENT="timediff";


    /** 拍照请求码 */
    public static final int REQUEST_CODE_PHOTO = 1;
    /** 本地图库请求码*/
    public static final int REQUEST_CODE_ALBUM = 2;
    /** 拍照图片预览返回码*/
    public static final int REQUEST_CODE_PREVIEW = 3;
    /** 图片选择界面返回码*/
    public static final int REQUEST_CODE_SELECT = 4;
    /** 文件选择界面返回码*/
    public static final int REQUEST_CODE_FILE = 5;
	/** 文本和图片转发界面返回码*/
    public static final int REQUEST_CODE_FORWARD = 6;
    
    /** 文件查看界面返回码*/
    public static final int REQUEST_CODE_FILE_CHECK = 7;

    /** 小视频界面返回码*/
    public static final int  REQUEST_CODE_VIDEO = 8;

    /**文件加密后缀*/
    public static final String FILE_ENCRPTY_SUFFIX = ".dat";

    //add by zya@xdja.com,20160928,重复下载的问题的修改
    public static final int DOWNLOAD_DECRYPT_SUCC = 10;
    
    

    /********************* liming add for optimize begin ******************************/
    //jyg add 2017/3/13 start fix bug 9261
	/**
     * 文件是缩略图
     */
    public static final int FILE_IS_THUMB = 1;
	//jyg add 2017/3/13 end
    /**
     * 文件是高清缩略图
     */
    public static final int FILE_IS_THUMB_HD = 2;

    /**
     * 文件是原文件(仅限图片,短视频使用)
     */
    public static final int FILE_IS_RAW = 3;
    /********************* liming add for optimize end ******************************/
	
	
	public static final String FILE_NAME_VOICE = "[语音]";
    public static final String FILE_NAME_VIDEO = "[视频]";
    public static final String FILE_NAME_IMAGE = "[图片]";
    public static final String FILE_NAME_NORMAL = "[文件]";
	public static final String FILE_NAME_BOMB = "[闪信]";
    // Task 2632, modify for share and forward function by ycm at 20161101.[Begin]
    public static final String SHARE = "share";//分享标志
    public static final String SHARE_FOR_CREATENEWSESSION = "share_for_create";//分享标志
    public static final String SHARE_FOR_MORECONTACT = "share_for_morecontact";//分享标志
    public static final String IMAGE_SHARE_TYPE = "image/";//图片分享动作类型
    public static final String TEXT_SHARE_TYPE = "text/";//文本分享动作类型
    public static final String FILE_SHARE_TYPE = "file/";//文件分享动作类型
    public static final String VIDEO_SHARE_TYPE = "video/";//视频分享动作类型
    public static final String WEB_SHARE_TYPE = "web/";//视频分享动作类型

    public static final int SELECT_CONTACT = 1;//选择联系创建会话
    public static final int SEARCH_CONTACT = 2;//选择查找联系人创建会话
    public static final int SINGLE_SESSION = 3;//选择单人会话
    public static final int GROUP_SESSION = 4;//选择群会话
    public static final int MORE_CONTACT = 5;//选择更多联系人创建会话
	
    public static final String FORWARD = "android.intent.action.FORWARD";
    public static final int FRIEND_ITEM = 1;//好友item
    public static final int GROUP_ITEM = 4;//群组item
    public static final String AVAURL = "avaUrl";
    public static final String NICK_NAME = "nickName";
    public static final String ACCOUNT = "account";

    public static final String FORWARD_CONTENT = "forwardContent";
    public static final String SELECT_ACCOUNT_LIST = "selectAccountList";
    public static final String MULTIPLE = "multiple";
    public static final String MAINFREAME = "com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter";
    public static final String TEL = "tel:";
    public static final int hyperlink_click_normal = 1;
    public static final int hyperlink_click_addContact = 2;

    private static final int LINK_WEB = 0x01;
    private static final int LINK_EMAIL = 0x02;
    private static final int LINK_PHONE = 0x04;
    public static final int ALL = LINK_WEB | LINK_EMAIL | LINK_PHONE;
    public static final String IS_ORIGINAL = "isOriginal";
    public static final String SMS_BODY = "sms_body";
    public static final String URL = "url";
	//add by ycm 20161205 [start]
    public static final int WEB_LINK = LINK_WEB;
    public static final String TYPE = "type";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String AUDIO = "audio";
    public static final String FILE = "file";
    public static final String PRIMARY = "primary";
    public static final String CONTENT = "content";
    public static final String WEIXIN_TITLE = "weixin_title";
    public static final String WEIXIN_TEXT = "weixin_text";
    public static final String WEB_URL = "web_url";
    public static final String HTTP = "http";

    public static final String FILE_DATA_FIELD = "_data";
    public static final String FILE_SIZE_FIELD = "_size";
    public static final String FILE_MODIFIED_FIELD = "date_modified";
    public static final String FILE_TYPE_FIELD = "mime_type";
    public static final String FILE_DISPLAY_FIELD = "_display_name";
    public static final String SCHEME_FILE = "file://";
    public static final String RESULT_REQUEST_VERSION = "requestAppVersion";

	//add by ycm 20161205[end]
	//Task 2632, modify for share and forward function by ycm at 20161101.[End]


    /**
     * 文件列表参数
     */
    public static final String ARGS_FILE_TYPE = "file_type";
	 //add by zya
    public static final int DOWNLOAD_BEFORE = 1;
    public static final int DOWNLOAD_MIDDLE = 2;
    public static final int DOWNLOAD_COMPLETE = 3;
    public static final int DOWNLOAD_FAILED = 4;
    public static final int DOWNLOAD_PAUSE = 5;
    public static final int[] SUPPORT_MSG_TYPE = new int[]{MSG_TYPE_TEXT, MSG_TYPE_VOICE, MSG_TYPE_PHOTO, MSG_TYPE_VIDEO,MSG_TYPE_WEB, MSG_TYPE_FILE,
            MSG_TYPE_PRESENTATION, MSG_TYPE_NO_CONTENT, MSG_COMBINE_TYPE_P2P_PRESENTATION, MSG_COMBINE_TYPE_P2G_PRESENTATION};

    public static boolean isSupportMsgType(int type){
        for (int aSUPPORT_MSG_TYPE : SUPPORT_MSG_TYPE) {

            if (type == aSUPPORT_MSG_TYPE) {
                return true;
            }
        }
        return false;
    }

    public static final int FAIL_CHECK = -2;    //对方版本过低
    public static final int FAIL_FRIEND = -5;   //对方不是好友

    //短视频
    public static final String VIDEO_SUFFIX = "mp4";   //短视频文件后缀

    public static final String MSG_ID = "msgid";
}
