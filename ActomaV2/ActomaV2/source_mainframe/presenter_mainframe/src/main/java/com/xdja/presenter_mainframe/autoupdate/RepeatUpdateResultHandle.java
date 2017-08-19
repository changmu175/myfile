package com.xdja.presenter_mainframe.autoupdate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;

/**
 * Created by chenbing on 2015/7/30.
 * 用于定时检测更新的结果处理
 * Modify by LiXiaolong on 2016/08/11.
 */
public class RepeatUpdateResultHandle implements UpdateListener {

    /**
     * 系统的NotificationManager服务
     */
//    private NotificationManager nm;
    /**
     * 通知
     */
//    private Notification notification;

    private final int POST_DELAY = 5000;

    public RepeatUpdateResultHandle() {
//        createNotification();
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
                LogUtil.getUtils().i("自动检测更新----检测版本信息失败");
                break;
            case AutoUpdate.UPDATE_CODE_NO_NEW: // 无版本更新
                LogUtil.getUtils().i("自动检测更新----暂无新版本");
                break;
            case AutoUpdate.UPDATE_DOWNLOAD_FAIL: // 升级文件下载失败
                break;
            case AutoUpdate.UPDATE_INSTALL: // 安装升级文件
                break;
            case AutoUpdate.UPDATE_CODE_NEW: // 检测到有升级版本
                //通知栏展示和new通知
                SharePreferceUtil.getPreferceUtil(ActomaApplication.getInstance()).setNewVersion(version);
                sendUpdateEvent();
                //开启通知栏
                //nm.notify(AutoUpdate.NOTIFICATION_ID, notification);
                createNotification();
                LogUtil.getUtils().i("自动检测更新----有新版本了---普通更新");
                break;
            case AutoUpdate.UPDATE_FORCE_CODE_NEW:
                SharePreferceUtil.getPreferceUtil(ActomaApplication.getInstance()).setNewVersion(version);
                XToast.show(ActomaApplication.getInstance(), ActomaController.getApp().getString(R.string.version_low_and_quit));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        ActivityContoller.getInstanse().exit();
                        ActivityStack.getInstanse().exitApp();
                    }
                }, POST_DELAY);

                //开启通知栏
//                nm.notify(AutoUpdate.NOTIFICATION_ID, notification);
                createNotification();
                LogUtil.getUtils().i("自动检测更新----有新版本了---强制更新");
                break;
            default:
                break;
        }
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
        NotificationManager mgr = (NotificationManager) ActomaApplication.getInstance()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化通知栏点击跳转页面===>暂时跳到登陆页面
        NotificationIntent ni = new NotificationIntent
                ("com.xdja.presenter_mainframe.presenter.activity.setting.UpdateTransparentPresenter");
        PendingIntent pi = ni.buildPendingIntent(ActomaApplication.getInstance(), AutoUpdate.REQCODE);
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
                .setContentIntent(pi)
                .build();
        //设置通知栏常驻不消失
        ntf.flags = Notification.FLAG_AUTO_CANCEL;
        //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
        mgr.notify(NotifiParamUtil.ANTONG_UPDATE_NOTIFI_ID, ntf);
        //[E]modify by xienana for notification id @2016/10/11 [review by tangsha]
    }

}
