package webrelay.bean;

import com.xdja.comm.server.ActomaController;
import com.xdja.voipsdk.R;

/**
 * Created by xjq on 16-1-5.
 */
public class StatusCode {
    public static final int SUCCESS = 0; // 成功



    public static final int CALLER_CANCEL = 400;// 主叫取消呼叫
    public static final int CALLEE_REJECT = 401;// 被叫拒绝接听
    public static final int CALLER_TIMEOUT = 402; // 主叫呼叫超时
    public static final int CALLEE_TIMEOUT = 403; // 被叫无人接听
    public static final int CALLEE_BUSY = 405; // 被叫正在通话中
    public static final int CALLEE_OFFLINE = 406; // 被叫不在线
    public static final int NETWORK_DISCONNECTED = 407;// 网络异常
    public static final int CALLER_ERROR_INITIAL = 408;// 主叫初始化呼叫失败
    public static final int CALLER_ERROR_CALLING = 409;// 主叫发送呼叫请求失败
    public static final int ERROR_REJECT = 410;// 发送挂断请求失败
    public static final int NETWORK_CONNECT = 411; //恢复网络连接

    public static final int REMOTE_NETWORK_DELAY = 412; // 对方网络较差
    public static final int LOCAL_NETWORK_DELAY = 413;// 自己网络较差
    public static final int CHANGE_LOW_BAND = 414; // 调整为窄带参数
    public static final int LOCAL_NETWORK_NORMAL = 415; // 自己网络状况恢复
    public static final int NETWORK_DELAY_HANGUP = 416; // 网络状况较差，通话结束


    public static final int NOT_FRIENDS = 417; //非好友关系
    public static final int TICKET_INVID = 418; // ticket无效
    //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. Start
    public static final int VOIP_SEVER_ERROR = 419; // voip服务器异常
    //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. End
    //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123 . review by mengbo. Start
    public static final int MX_SEVER_ERROR = 420; // 密信服务器异常
    //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123 . review by mengbo. End

    /** 20161230-mengbo-start: 新增网络繁忙，请等待约10秒提示,避免服务无法销毁问题 **/
    public static final int WAIT_DISCONNECTING_CALLBACK = 421; //网络繁忙，请等待约10秒
    /** 20161230-mengbo-end **/


    public static String getComment(int code) {
        String comment;
        switch(code) {
            case SUCCESS:
                comment = "";
                break;
            case CALLEE_BUSY:
                comment = ActomaController.getApp().getString(R.string.CODE_CALLEE_BUSY);
                break;
            case CALLEE_OFFLINE:
                /** 20161019-mengbo-start: 修改提示为：无人接听 2016-11-17 14:52:26-wangzhen-start ：修改提示为：资源型引用**/
                //comment = "无人接听";
                comment = ActomaController.getApp().getString(R.string.CODE_CALLEE_TIMEOUT);
                //comment = "对方不在线";
                /** 20161019-mengbo-end 2016-11-17 14:52:55 wangzhen-end**/
                break;
            case NETWORK_DISCONNECTED:
                comment = ActomaController.getApp().getString(R.string.CODE_NETWORK_DISCONNECTED);
                break;
            case CALLEE_TIMEOUT:
                comment = ActomaController.getApp().getString(R.string.CODE_CALLEE_TIMEOUT);
                break;
            case CALLER_ERROR_INITIAL:
                comment = ActomaController.getApp().getString(R.string.CODE_CALLER_ERROR_INITIAL);
                break;
            case CALLER_ERROR_CALLING:
                comment = ActomaController.getApp().getString(R.string.CODE_CALLER_ERROR_CALLING);
                break;
            case ERROR_REJECT:
                comment = ActomaController.getApp().getString(R.string.CODE_ERROR_REJECT);
                break;
            case NETWORK_CONNECT:
                comment = ActomaController.getApp().getString(R.string.CODE_NETWORK_CONNECT);
                break;
            case REMOTE_NETWORK_DELAY:
                comment = ActomaController.getApp().getString(R.string.CODE_REMOTE_NETWORK_DELAY);
                break;
            case LOCAL_NETWORK_DELAY:
                comment = ActomaController.getApp().getString(R.string.CODE_LOCAL_NETWORK_DELAY);
                break;
            case CHANGE_LOW_BAND:
                comment = ActomaController.getApp().getString(R.string.CODE_CHANGE_LOW_BAND);
                break;
            case LOCAL_NETWORK_NORMAL:
                comment = "";
                break;
            case NETWORK_DELAY_HANGUP:
                /** 20161019-mengbo-start: 修改提示为：呼叫失败 2016-11-17 14:54:12-wangzhen-start ：修改提示为：资源型引用 **/
                //comment = "呼叫失败";
                comment = ActomaController.getApp().getString(R.string.CALL_FAILED);
                //comment = "网络状况较差，通话结束";
                /** 20161019-mengbo-end 2016-11-17 14:54:40-wangzhen-end **/
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. Start
            case VOIP_SEVER_ERROR:
                comment = ActomaController.getApp().getString(R.string.CODE_VOIP_SEVER_ERROR);
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. End

            /** 20161230-mengbo-start: 新增网络繁忙，请等待约10秒提示 **/
            case WAIT_DISCONNECTING_CALLBACK:
                comment = ActomaController.getApp().getString(R.string.WAIT_DISCONNECTING_CALLBACK);
                break;
            /** 20161230-mengbo-end **/

            default:
                comment = "";
                break;
        }
        return comment;
    }
}
