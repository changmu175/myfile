package com.xdja.presenter_mainframe.autoupdate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.comm.event.LauncherUpdateEvent;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;

/**
 * Created by chenbing on 2015/7/30.
 * 用于启动时后台检测更新的结果处理
 * Modify by LiXiaolong on 2016/08/11.
 */
public class BackUpdateResultHandle implements UpdateListener {
    /**
     * 系统的NotificationManager服务
     */
//    private NotificationManager nm;
    /**
     * 通知
     */
//    private Notification notification;

    private Context context;

    public BackUpdateResultHandle(Context context) {
        this.context = context;
//        initNotification();
    }


    /**
     * 处理更新各种结果
     *
     * @param actionTag 版本检测结果标识
     * @param version   版本检测返回的版本号
     */
    @Override
    public void handerResult(int actionTag, String version, AutoUpdate autoUpdate) {
        switch (actionTag) {
            case AutoUpdate.UPDATE_CANCEL: // 正常更新 暂不更新
                break;
            case AutoUpdate.UPDATE_CODE_ERROR://检测版本信息失败
                sendLauncherUpdateEvent(false);
                break;
            case AutoUpdate.UPDATE_CODE_NO_NEW: // 无版本更新
                sendLauncherUpdateEvent(false);
                break;
            case AutoUpdate.UPDATE_DOWNLOAD_FAIL: // 升级文件下载失败
                break;
            case AutoUpdate.UPDATE_FORCE_DOWNLOAD_FAIL://强制升级文件下载失败
//                ActivityContoller.getInstanse().exit();
                ActivityStack.getInstanse().exitApp();
                break;
            case AutoUpdate.UPDATE_INSTALL: // 安装升级文件
                break;
            case AutoUpdate.UPDATE_CODE_NEW: // 检测到有升级版本
                sendLauncherUpdateEvent(false);
                SharePreferceUtil.getPreferceUtil(context).setNewVersion(version);
                sendUpdateEvent();
                //开启通知栏
//                nm.notify(AutoUpdate.NOTIFICATION_ID, notification);
                createNotification();
                break;
            case AutoUpdate.UPDATE_FORCE_CODE_NEW:
                sendLauncherUpdateEvent(true);
                SharePreferceUtil.getPreferceUtil(context).setNewVersion(version);
                sendUpdateEvent();
                autoUpdate.showUpdateMessage();
                break;
            case AutoUpdate.UPDATE_FORCDE_CANCEL://强制升级 暂不升级  关闭安通+
//                ActivityContoller.getInstanse().exit();
                ActivityStack.getInstanse().exitApp();
                break;
        }
    }

//    /**
//     * 强制升级的提醒事件
//     */
//    public static class LauncherUpdateEvent {
//        private boolean isForceUpdate = false;
//
//        public boolean isForceUpdate() {
//            return isForceUpdate;
//        }
//
//        public void setIsForceUpdate(boolean isForceUpdate) {
//            this.isForceUpdate = isForceUpdate;
//        }
//    }

    /**
     * 发送登录检测更新是否为强制升级的事件通知
     *
     * @param isForceUpdate 是否为强制升级
     */
    private void sendLauncherUpdateEvent(boolean isForceUpdate) {
        LauncherUpdateEvent event = new LauncherUpdateEvent();
        event.setIsForceUpdate(isForceUpdate);
        BusProvider.getMainProvider().post(event);
    }

    /**
     * 发送显示NEW红点的事件
     */
    private void sendUpdateEvent() {
        FreshUpdateNewEvent event = new FreshUpdateNewEvent();
        event.setIsHaveUpdate(true);
        BusProvider.getMainProvider().post(event);
    }

    /**
     * 初始化通知
     */
    @SuppressLint("NewApi")
    private void createNotification() {
        //获取系统NotificationManager服务
        if (ActomaApplication.getInstance() == null) {
            return;
        }
        NotificationManager mgr = (NotificationManager) ActomaApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化通知栏点击跳转页面===>暂时跳到登陆页面

        NotificationIntent notificationIntent = new NotificationIntent
                ("com.xdja.presenter_mainframe.presenter.activity.setting.UpdateTransparentPresenter");
        PendingIntent pendingIntent = notificationIntent.buildPendingIntent(ActomaApplication.getInstance(), AutoUpdate.REQCODE);
        //创建通知栏
        Notification ntf = new Notification.Builder(ActomaApplication.getInstance())
                //设置打开该通知,该通知消失
                .setAutoCancel(true)
                //设置显示在状态栏的通知提示消息
                //设置通知的图标
                .setSmallIcon(R.drawable.af_abs_ic_logo)
                //通知产生的时间，会在通知信息里显示
                .setWhen(System.currentTimeMillis())
                //设置标题
                .setContentTitle(ActomaApplication.getInstance().getResources().getString(R.string.update_notification_title))
                //设置通知内容
                .setContentText(ActomaApplication.getInstance().getResources().getString(R.string.update_notification_msg))
                //设置通知将要启动的Intent
                .setContentIntent(pendingIntent)
                .build();
        //设置通知栏常驻不消失
        ntf.flags = Notification.FLAG_AUTO_CANCEL;
        //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
        mgr.notify(NotifiParamUtil.ANTONG_UPDATE_NOTIFI_ID, ntf);
        //[E]modify by xienana for notification id @2016/10/11 [review by tangsha]
    }

}
