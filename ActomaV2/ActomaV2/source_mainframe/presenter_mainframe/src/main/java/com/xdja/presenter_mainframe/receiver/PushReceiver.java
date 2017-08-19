package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.global.obs.BindDeviceObservable;
import com.xdja.presenter_mainframe.global.obs.DeviceOnLineObservable;
import com.xdja.presenter_mainframe.global.obs.ForceLogoutObservable;
import com.xdja.presenter_mainframe.global.obs.UnBindDeviceObservable;
import com.xdja.presenter_mainframe.service.UpdateService;

import java.util.Map;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.receiver</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/28</p>
 * <p>Time:9:56</p>
 */
public class PushReceiver extends BroadcastReceiver {


    /**
     * action
     */
    public static final String ACTION = "com.xdja.apushsdk";

    /**
     * key c
     */
    public static final String CONTENT = "c";

    /**
     * topic
     */
    public static final String TOPIC = "topic";

    public static final String CON_FORCELOGOUT = "forceLogout";
    public static final String CON_LOGINNOTICE = "loginNotice";
    public static final String CON_BINDDEVICE = "bindDevice";
    public static final String CON_UNBINDDEVICE = "unBindDevice";
    public static final String CON_FORCEUPDATE = "forceUpdate";
    public static final String CON_FORCEBINDMOBILE = "forceBindMobile";

    /**
     * 转发老推送的action
     */
    public static final String OLD_ACTION = "com.xdja.push.MESSAGE";

    /**
     * 老推送的内容content
     */
    public static final String OLD_CONTENT = "NOTIFICATION_MESSAGE";

    /**
     * 程序的包名
     */
    public static final String NOTIFICATION_PACKAGE = "NOTIFICATION_PACKAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || !action.equals(ACTION)) {
            return;
        }
//        String packageName = intent.getPackage();


        String topic = intent.getStringExtra(TOPIC);
        topic = topic.substring(topic.lastIndexOf("/"));

        Map<String, String> pnMap = ((ActomaApplication) context
                                .getApplicationContext()).getAppComponent().pnMap();
        LogUtil.getUtils().d("Receive Push Message intent : " + intent+" topic "+topic);

        if (!TextUtils.isEmpty(topic)) {
            if (pnMap.containsValue(topic)) {

                String con = intent.getStringExtra(CONTENT);
                if (TextUtils.isEmpty(con)) {
                    return;
                }
                //[S]add by tangsha for third encrypt
                if(topic.equals(pnMap.get("TOPIC_THIRDENCRYPT"))){
                    ParseThirdEnPushMsgTask thirdEnPushMsgTask = new ParseThirdEnPushMsgTask(con,ContactUtils.getCurrentAccount());
                    thirdEnPushMsgTask.execute();
                    return;
                }
                //[E]add by tangsha for third encrypt
                Object event = null;
                if (con.equals(CON_FORCELOGOUT)) {
                    event = new ForceLogoutObservable.ForceLogoutEvent();
                } else if (con.equals(CON_LOGINNOTICE)) {
                    event = new DeviceOnLineObservable.DeviceOnLineEvent();
                } else if (con.equals(CON_BINDDEVICE)) {
                    event = new BindDeviceObservable.BindDeviceEvent();
                } else if (con.equals(CON_UNBINDDEVICE)) {
                    event = new UnBindDeviceObservable.UnBindDeviceEvent();
                } else if (con.equals(CON_FORCEUPDATE)) {
                    // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
                    //推送进行版本升级
                    Intent intentUpdate = new Intent(context, UpdateService.class);
                    intent.setAction(AutoUpdate.ACTION_CHECK_UPDATE);
                    context.startService(intentUpdate);
                    // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
                }else if (con.equals(CON_FORCEBINDMOBILE)){
                    event = new UnBindDeviceObservable.UnBindMobileEvent();
                }

                if (event != null) {
                    LogUtil.getUtils().d("busProvider.post : " + event);
                    BusProvider busProvider = ((ActomaApplication) context.getApplicationContext())
                            .getApplicationComponent().busProvider();
                    if (busProvider != null) {
                        busProvider.post(event);
                    }
                }else {
                    sendOldAction(context,con,intent.getPackage());
                }
            }
        }
    }

    /**
     * 发送老推送的广播
     * @param context 上下文
     * @param content 内容
     */
    private void sendOldAction(Context context, String content, String packageName) {
        Intent intent = new Intent();
        intent.setAction(OLD_ACTION);
        intent.putExtra(OLD_CONTENT, content);
        intent.putExtra(NOTIFICATION_PACKAGE, packageName);
        context.sendBroadcast(intent);
    }
}
