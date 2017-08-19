package com.xdja.presenter_mainframe.global;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.ForceExitAppEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.di.modules.PostModule;
import com.xdja.presenter_mainframe.global.obs.Observable;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.receiver.Application2FrontReceiver;
import com.xdja.presenter_mainframe.util.ActivityStatusListener;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Subscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.global</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/28</p>
 * <p>Time:10:07</p>
 */
public class PostGlobalLife implements GlobalLifeCycle {

    private final ActivityStatusListener activityStatusListener;
    private final Context context;
    private final Map<String, Provider<String>> deviceStringMap;
    private final PushController pushController;
    private Observable<Map<String, String>>
            unBindDeviceObservable,
            forceLogoutObservable,
            onLineNoticeObservable;

    private LogoutHelper logoutHelper;

    private PerSubscriber<Map<String, String>> unBindSubscriber
            = new PerSubscriber<Map<String, String>>(null) {
        @SuppressWarnings("EmptyMethod")
        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }

        @SuppressWarnings("EmptyMethod")
        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onNext(Map<String, String> stringMap) {
            super.onNext(stringMap);

            //BasePresenterActivity topActivity = ActivityStack.getInstanse().getTopActivity();
            // TODO: 2016/5/3 和产品经理确认解绑设备的处理
            LogUtil.getUtils().d("收到设备解绑通知:" + stringMap.get(CacheModule.KEY_DEVICEID) + " , " + stringMap.get(CacheModule.KEY_UNBIND_USER));
            if (stringMap.get(CacheModule.KEY_DEVICEID).equals(deviceStringMap.get(CacheModule.KEY_DEVICEID).get())) {
                logoutHelper.diskLogout();
                String message = ActomaController.getApp().getString(R.string.device_removed , TextUtils.isEmpty(stringMap.get(CacheModule.KEY_UNBIND_USER)) ? "" : stringMap.get(CacheModule.KEY_UNBIND_USER));
                final XDialog xDialog = new XDialog(ActivityStack.getInstanse().getTopActivity());
                xDialog.setTitle(ActomaController.getApp().getString(R.string.account_will_quit))
                        .setMessage(message)
                        .setPositiveButton(ActomaController.getApp().getString(R.string.text_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                xDialog.dismiss();
                                ActivityStack.getInstanse().exitApp();
                            }
                        })
                        .setNegativeButton(ActomaController.getApp().getString(R.string.relogin), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                xDialog.dismiss();
                                //如果是退出登录操作，需要跳转到登录界面
                                Navigator.navigateToLoginWithExit();
                            }
                        })
                        .show();
                popNotification(ActomaController.getApp().getString(R.string.account_unbind));
            }
        }
    };

    private PerSubscriber<Map<String, String>> forceLogoutSubscriber =
            new PerSubscriber<Map<String, String>>(null) {
                @Override
                public void onNext(Map<String, String> stringMap) {
                    super.onNext(stringMap);
                    LogUtil.getUtils().d("onNext forceLogoutSubscriber logoutHelper.diskLogout()");
                    // TODO: 2016/5/3 和产品经理确认强制下线的处理
                    logoutHelper.diskLogout();
                    final XDialog xDialog = new XDialog(ActivityStack.getInstanse().getTopActivity());
                    //modify by alh@xdja.com to fix bug: 863 2016-06-29 start (rummager : wangchao1)
                    xDialog.setCanceledOnTouchOutside(false);
                    xDialog.setCancelable(false);
                    //modify by alh@xdja.com to fix bug: 863 2016-06-29 end (rummager : wangchao1)
                    xDialog.setTitle(ActomaController.getApp().getString(R.string.prompt))
                            .setMessage(ActomaController.getApp().getString(R.string.forced_offline))
                            .setPositiveButton(ActomaController.getApp().getString(R.string.certain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtil.getUtils().d("xDialog.onClick navigateToLoginWithExit");
                                    xDialog.dismiss();
                                    BusProvider.getMainProvider().post(new ForceExitAppEvent());
                                    //如果是退出登录操作，需要跳转到登录界面
                                    Navigator.navigateToLoginWithExit();
                                }
                            })
                            .show();
                    LogUtil.getUtils().d("xDialog.isShow : " + xDialog.isShowing());
                    popNotification(ActomaController.getApp().getString(R.string.forced_offline_title));
                }

                @SuppressWarnings("EmptyMethod")
                @Override
                public void onCompleted() {
                    super.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    //调用接口错误也应该清除本地
                    //alh@xdja.com<mailto://alh@xdja.com> 2016-12-02 add. fix bug 6355 . review by wangchao1. Start
                    LogUtil.getUtils().d("onError forceLogoutSubscriber logoutHelper.diskLogout()");
                    logoutHelper.diskLogout();
                    BusProvider.getMainProvider().post(new ForceExitAppEvent());
                    //alh@xdja.com<mailto://alh@xdja.com> 2016-12-02 add. fix bug 6355 . review by wangchao1. End


                    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-02 add. fix bug 3176 . review by wangchao1. Start
                    //如果是退出登录操作，需要跳转到登录界面
                    Navigator.navigateToLoginWithExit();
                    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-02 add. fix bug 3176 . review by wangchao1. Start
                }
            };

    private void popNotification(String title) {
        if (activityStatusListener.getStatus() != ActivityStatusListener.STATUS_FRONT) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.af_abs_ic_logo)
                            .setContentTitle(title);
            Intent myIntent = new Intent(Application2FrontReceiver.ACTION_APPLICATION_2_FRONT);
            mBuilder.setContentIntent(PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            //[S]modify by xienana for bug 4629 @2016/10/09 [reviewed by wangchao]
            mNotificationManager.notify(BasePresenterActivity.POST_GlOBAL_NOTIFI_ID, notification);
            //[E]modify by xienana for bug 4629 @2016/10/09 [reviewed by wangchao]
        }
    }

    private Subscriber<Map<String, String>> onLineNoticeSubscriber = new Subscriber<Map<String, String>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Map<String, String> stringStringMap) {
            //TODO: 2016/05/19 确认下线通知处理
        }
    };


    @SuppressWarnings({"UnusedParameters", "ConstructorWithTooManyParameters"})
    @Inject
    public PostGlobalLife(PushController pushController,
                          @Named(PostModule.OBSERBABLE_UNBINDDEVICE)
                          Observable<Map<String, String>> unBindDeviceObservable,
                          @Named(PostModule.OBSERBABLE_FORCELOGOUT)
                          Observable<Map<String, String>> forceLogoutObservable,
                          @Named(PostModule.OBSERBABLE_ONLINENOTICE)
                          Observable<Map<String, String>> onLineNoticeObservable,
                          LogoutHelper logoutHelper,
                          @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
                          ActivityStatusListener activityStatusListener,
                          Map<String, Provider<String>> stringMap
    ) {
        this.pushController = pushController;
        this.unBindDeviceObservable = unBindDeviceObservable;
        this.forceLogoutObservable = forceLogoutObservable;
        this.onLineNoticeObservable = onLineNoticeObservable;
        this.logoutHelper = logoutHelper;
        this.context = context;
        this.activityStatusListener = activityStatusListener;
        this.deviceStringMap = stringMap;
    }

    @Override
    public void create() {
        if (this.unBindDeviceObservable != null) {
            this.unBindDeviceObservable.subscribe(unBindSubscriber);
        }
        if (this.forceLogoutObservable != null) {
            this.forceLogoutObservable.subscribe(forceLogoutSubscriber);
        }
        if (this.onLineNoticeObservable != null) {
            this.onLineNoticeObservable.subscribe(onLineNoticeSubscriber);
        }
    }

    @Override
    public void destroy() {
        if (this.unBindDeviceObservable != null) {
            this.unBindDeviceObservable.unSubscribe(unBindSubscriber);
            this.unBindDeviceObservable.release();
        }
        if (this.forceLogoutObservable != null) {
            this.forceLogoutObservable.unSubscribe(forceLogoutSubscriber);
            this.forceLogoutObservable.release();
        }
        if (this.onLineNoticeObservable != null) {
            this.onLineNoticeObservable.unSubscribe(onLineNoticeSubscriber);
            this.onLineNoticeObservable.release();
        }
    }
}
