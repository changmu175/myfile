package com.xdja.imsdk.constant.internal;

/**
 * 项目名称：ImSdk             <br>
 * 类描述  ：ImSdk内部常量类    <br>
 * 创建时间：2016/11/16 17:23  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class Constant {

    /***************************************
     * ****** ImSdk内部错误码起始值 *******  *
     * 1、ImSdk状态码，50010-50029
     * @see com.xdja.imsdk.constant.StateCode
     *
     * 2、ImSdk操作文件状态码，50030-50040
     * @see com.xdja.imsdk.constant.IMFailCode
     *
     * 3、网络请求结果码，50100-50110
     * @see com.xdja.imsdk.http.error.HttpErrorCode
     ***************************************/
    public static final int CODE_BEGIN = 50000;

    /***************************************
     * **** ImSdk内部intent key name *****  *
     ***************************************/
    public static final String IM_ACTION_INIT = "im.action.init";    // ImSdk init extra
    public static final String IM_ACTION_STATE = "im.action.state";  // Push state extra
    public static final String IM_ACTION_TOPIC = "im.action.topic";  // Push notice extra
    public static final String IM_INIT_PARAM = "im.init.param";      // ImSdk init action
    public static final String IM_STATE_PARAM = "im.state.param";    // Push state action
    public static final String IM_TOPIC_PARAM = "im.topic.param";    // Push notice action

    /***************************************
     * ******* ImSdk内Push相关常量 ********  *
     ***************************************/
    public static final String IM_ACCOUNT_PREFIX = "xdja/d/";        //初始化Push的账号前缀
    public static final String IM_ACCOUNT_SUFFIX = "/im";            //初始化Push的账号后缀
    public static final String PUSH_TOPIC = "topic";                 //初始化Push订阅的主题key
    public static final String PUSH_ACTION = "com.xdja.apushsdk";    //注册接收Push消息广播action
    public static final String PUSH_CODE = "pushcode";               //Push的连接状态广播key
    public static final String PUSH_CONNECTED = "10000";             //Push的长连接成功
    public static final String PUSH_DISCONNECTED = "10001";          //Push长链接断开
    public static final String PUSH_NET_OK = "10002";                //Push上报网络可用
    public static final String PUSH_NET_DISMISS = "10003";           //Push闪避无可用网络
    public static final String PUSH_CONNECT_FAIL = "10004";          //Push建立长连接失败

    /***************************************
     * ******* ImSdk请求IM后台的返回码 *****  *
     ***************************************/
    public static final int SERVER_STATE_OK = 200;                   //IM服务器返回状态更新成功
    public static final int SERVER_STATE_FAIL = 201;                 //IM服务器返回状态更新失败
    public static final int SERVER_ERROR = 553;                      //IM服务器内部错误
    public static final int SERVER_PARA = 552;                       //IM服务器参数错误
    public static final int SERVER_NONE = 551;                       //请求结果不存在
    public static final int SERVER_NO_WORKING = 550;                 //IM服务器处于非工作状态
    public static final int FORMATE_ERROR = 601;                     //消息格式错误
    public static final int CONTENT_ERROR = 602;                     //消息内容错误
    public static final int SENDER_ERROR = 603;                      //发送方ticket、账号、设备号不匹配
    public static final int MODIFIED_ERROR = 604;                    //状态消息-状态已被修改过，内部使用
    public static final int NO_MESSAGE = 605;                        //状态消息-消息不存在，内部使用
    public static final int NO_SERVER = 701;                         //服务不存在
    public static final int TICKET_ERROR = 702;                      //ticket验证失败
    public static final int SERVER_FORBID = 703;                     //无发送权限，不是好友关系

    /***************************************
     * * ImSdk消息属性状态码，从右向左按位计算 **
     * ***** 第一位：0，本账号发送的消息 *******
     * *****        1，本账号接收的消息 *******
     * ***** 第二位：0，新消息          *******
     * *****        1，历史消息        *******
     ***************************************/
    public static final int MSG_DIRECTION = 1;                       //消息是本账号发送还是接收的
    public static final int MSG_SHOW = 2;                            //消息是新消息还是历史消息

    public static final int MSG_SENT_NEW = 0;                        //0x00：本账号发送的新消息
    public static final int MSG_REC_NEW = 1;                         //0x01：本账号接收的新消息
    public static final int MSG_SENT_OLD = 2;                        //0x10：本账号发送的历史消息
    public static final int MSG_REC_OLD = 3;                         //0x11：本账号接收的历史消息

    /***************************************
     * ******* ImSdk操作文件行为类型 *****  *
     ***************************************/
    public enum FileOptType {
        UP_PAUSE,
        UP_RESUME,
        DOWN_PAUSE,
        DOWN_RESUME
    }

    /**
     * 发送消息队列消息类型
     */
    public enum SentType {
        NORMAL,
        STATE
    }

    /**
     * 接收消息队列类型
     * 接收到的消息
     * 待解密的文件
     */
    public enum ReceiveType {
        RECEIVE,
        DECRYPT
    }

    /**
     * 消息内容体类型
     */
    public enum BodyType {
        TEXT,
        VOICE,
        IMAGE,
        VIDEO,
        NORMAL,
        UNKNOWN
    }

    public enum FileCallType {
        UP_UPDATE,
        UP_FAIL,
        UP_FINISH,
        UP_PAUSE,
        DOWN_UPDATE,
        DOWN_FAIL,
        DOWN_FINISH,
        DOWN_PAUSE
    }

    /***************************************
     * ******* ImSdk发送文件最大尺寸 *****  *
     ***************************************/
    public static final long UPLOAD_IMAGE_MAX = 5 * 1024 * 1024;     //图片发送最大尺寸：5M
    public static final long UPLOAD_VIDEO_MAX = 50 * 1024 * 1024;     //视频发送最大尺寸：50M
    public static final long UPLOAD_FILE_MAX = 50 * 1024 * 1024;     //文件发送最大尺寸：50M

    /***************************************
     * ******* ImSdk检测对方版本号结果 *****  *
     ***************************************/
    public static final int CHECK_SUCCESS = 0;                      //对方版本支持此功能
    public static final int CHECK_FAIL = 1;                         //对方版本过低，不支持此功能
    public static final int CHECK_ERROR = 2;                        //检测错误

    /**
     * 系统时间毫秒与纳秒的转换倍数
     */
    public static final int TIME_MULTIPLE = 1000000;

    /**
     * 单个会话每次回调最大条数
     */
    public static final int CALLBACK_MAX = 15;

    /**
     * 是否开启批量回调机制
     */
    public static final boolean CALLBACK_BATCH = true;

    /**
     * 发送文件前，是否检测对方版本号
     */
    public static final boolean CHECK_VERSION = true;

    /**
     * 闪信默认生存时间，单位是毫秒
     */
    public static final int BOMB_TIME = 9000;

    /**
     * 消息体默认服务器Id
     */
    public static final long DEFAULT_SERVER_ID = -1;

    /**
     * 每条状态消息最大消息数
     */
    public static final int MAX_STATE = 50;

    /**
     * 加密文件默认后缀
     */
    public static final String ENCRYPT_SUFFIX = ".dat";

    public static final String ENCRYPT_SUFFIX_SUB = "dat"; // TODO: 2017/1/2 liming 
}
