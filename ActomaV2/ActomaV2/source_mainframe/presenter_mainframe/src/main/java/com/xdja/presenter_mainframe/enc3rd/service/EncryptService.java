package com.xdja.presenter_mainframe.enc3rd.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter;

import javax.inject.Inject;

/**
 * Created by geyao on 2015/8/3.
 * 第三方应用加密服务通知栏
 */
public class EncryptService extends Service {
    /**
     * 通知
     */
    private Notification notification;
    /**
     * 通知id
     */
    //[S]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]
    //    private final int NOTIFICATION_ID = 0x10011;
    //[E]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]
    /**
     * 请求Code
     */
    private final int REQCODE = 0x10011;
    /**
     * 开启服务的Action
     */
    public static final String ACTION_START_SERVICE =
            "com.xdja.actoma.oae.start";
    /**
     * 关闭服务的Action
     */
    public static final String ACTION_STOP_SERVICE =
            "com.xdja.actoma.oae.stop";

    private Notification.Builder builder;
    /**
     * 记录第一次点击扇形菜单应用的名称
     */
    private String oldAppName;

    @Inject
    TFCardManager tfCardManager;

    @Override
    public void onCreate() {
        LogUtil.getUtils().i("====EncryptService onCreate====");
        super.onCreate();
        try {
            //初始化相关卡服务
            tfCardManager.initTFCardManager();
            tfCardManager.initUnitePinManager();
        } catch (Exception e) {
            LogUtil.getUtils().i("前台服务初始化相关卡服务操作失败");
        }
        LogUtil.getUtils().i("EncryptService onCreate");
        //preEncService(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                //启动服务
                if (action.equals(ACTION_START_SERVICE)) {
                    if (StateParams.getStateParams().isSeverOpen()) {
                        String showName = intent.getStringExtra("showName");
                        String appName = intent.getStringExtra("appName");
                        //wxf@xdja.com 2016-11-11 add. fix bug 5346 . review by mengbo. Start
                        if(!appName.equals("")){
                            oldAppName = appName;
                            notificationMaker(showName,appName);
                        }else{
                            notificationMaker(showName,oldAppName);
                            LogUtil.getUtils().i("MainFramePresenter openThirdTransfer oldAppName: " + oldAppName);
                        }
                        //wxf@xdja.com 2016-11-11 add. fix bug 5346 . review by mengbo. End
                        //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
                        startForeground(NotifiParamUtil.ENCRYPT_3_SERVICE_NOTIFI_ID, notification);
                        //[E]modify by xienana for notification id @2016/10/11 [review by tangsha]
                    }
                }
                //关闭服务
                else if (action.equals(ACTION_STOP_SERVICE)) {
                    LogUtil.getUtils().i("EncryptService stop");
                    stopForeground(true);
                    stopService(new Intent(this, this.getClass()));
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notificationMaker(String showName,String appName) {
        String content = getResources().getString(R.string.notification_msg2);
        if (!TextUtils.isEmpty(showName) && !TextUtils.isEmpty(appName)) {
            content = getResources().getString(R.string.and) + showName + appName +" "+getResources().getString(R.string.secure_communicating);
        }
        // [Start] modify by LiXiaolong on 2016-08-17. fix bug 2902. review by myself.
        NotificationIntent intent =
                new NotificationIntent("com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter");
        // [End] modify by LiXiaolong on 2016-08-17. fix bug 2902. review by myself.
        //构建要跳转的页面和Intent，注意：Intent中的参数必须放在Bundle中进行传递
        Bundle bundle = new Bundle();
        bundle.putInt(MainFramePresenter.ARG_PAGE_INDEX, TabTipsEvent.INDEX_CONTACT);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = intent.buildPendingIntent(getApplicationContext(), REQCODE);

        if (builder == null) {
            builder = new Notification.Builder(this)
                    //设置打开该通知,该通知不消失
                    .setAutoCancel(false)
                            //设置通知的图标
                    .setSmallIcon(R.drawable.af_abs_ic_logo)
                            //设置通知内容的标题
                    .setContentTitle(getResources().getString(R.string.notification_msg1))
                            //通知产生的时间，会在通知信息里显示
                    .setWhen(System.currentTimeMillis())
                            //设置通知内容
//                    .setContentText(content)
                            //设置通知将要启动的Intent
                    .setContentIntent(pendingIntent);
        }
        builder.setContentText(content);
        notification = builder.build();
    }

    /**
     * 修改第三方应用加密服务相关数据值
     *
     * @param //isOpen 是否开启第三方应用加密服务
     */
    //private void preEncService(boolean isOpen) {
        //修改数据库内第三方应用加密服务的值
//        SettingBean bean = new SettingBean();
//        bean.setKey(SettingBean.SEVER);
//        bean.setValue(String.valueOf(isOpen));
//        SettingServer.insertSetting(this, bean);
//        //修改外部调用第三方加密服务值
//        StateParams.getStateParams().setIsSeverOpen(isOpen);
        //修改第三方加解密模块所需第三方加密服务是否开启值
//        ActomaApp.getActomaApp().getAccountInfo().setIsEncryptSeverOpen(String.valueOf(isOpen));
//        if (!isOpen) {
//            //清空安通账号值
//            StateParams.getStateParams().setEncryptAccount(null);
//            ActomaApp.getActomaApp().getAccountInfo().setEncryptAccount(null);
//            SettingServer.deleteSetting(this, SettingBean.ACCOUNT);
//        }
    //}

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.getUtils().i("==========onTaskRemoved=======");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        LogUtil.getUtils().i("====EncryptService onDestroy====");
        //preEncService(false);
        super.onDestroy();
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
