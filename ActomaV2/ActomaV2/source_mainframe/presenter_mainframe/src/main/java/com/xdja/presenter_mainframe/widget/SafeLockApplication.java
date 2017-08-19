package com.xdja.presenter_mainframe.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.handler.SafeLockUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.AndroidApplication;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.http.util.TextUtils;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenGesturePresenter;
import com.xdja.presenter_mainframe.util.LockPatternUtils;

import java.util.List;

/**
 * Created by licong on 2016/11/30.
 */
@SuppressLint("Registered")
public class SafeLockApplication extends AndroidApplication {

    public static String SCREEN_BROADCAST = "com.xdja.actoma_screenBroadCast";//灭屏广播到达
    private String isSetPattern = false + "";//是否设置开启安全锁 设置安全锁有三个状态 true 打开 false 关闭  -1 未设置 -2 忘记密码
    private boolean useBackGround = false;//是否设置后台锁定安通+
    private String isPauseActivity = "";//进入Pause状态的activity
    private ScreenBroadcastReceiver mScreenReceiver;//锁屏监听

    @Override
    public void onCreate() {
        super.onCreate();

        mScreenReceiver = new ScreenBroadcastReceiver();
        registerScreenBroadcastReceiver();

        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (!activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.setting.OpenGesturePresenter")) {
                    startLock(activity);
                    return;
                }
            }

            private void startLock(Activity activity) {
                //获取是否设置开启安全锁
                isSetPattern = SettingServer.getSafeLock();
                if (isSetPattern.equals("true")) {

                    //获取是否设置了手机锁屏后锁定安通+
//                    useScreenLock = SettingServer.getLockScreen();
                    //获取是否设置了后台运行时锁定安通+
                    useBackGround = SettingServer.getLockBackground();

                    //[S]fix bug 7717 by licong, for safeLock
                    if (((ActomaApplication)activity.getApplication()).getUserComponent() == null) {
                        return;
                    }
                    //[E]fix bug 7717 by licong, for safeLock

                    //[S] fix bug 最近任务栏切换 7972  by licong
                    if(activity.getLocalClassName().equals("com.xdja.imp.presenter.activity.ChooseIMSessionActivity")){
                        OpenGesturePresenter.isTransmit = true;
                    }
                    //[E] fix bug 最近任务栏切换 7972  by licong


                    if (activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.login.LoginPresenter")
                            || activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.login.ProductIntroductionAnimPresenter")
                            || activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.login.LauncherPresenter")
                            || activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.SplashPresenter")) {
                        return;
                    }

                    if (activity.getLocalClassName().contains("com.xdja.voipsdk.InCallPresenter")) {
                        return;
                    }
                   //[S] fix bug 7706 by licong for safeLock
                    LogUtil.getUtils("gbc").e("startLock  isScreenLockerState  = " +ActomaApplication.isScreenLockerState());
                    if (ActomaApplication.isScreenLockerState()) {
                        if (SafeLockUtil.isUseCameraOrFile()) {
                            SafeLockUtil.setUseCameraOrFile(false);
                            synchronized (ActomaApplication.getObjLocker()) {
                                ActomaApplication.setScreenLockerState(false);
                            }
                        } else {
                            LockPatternUtils.startConfirmPattern(activity);
                            SafeLockUtil.setUseCameraOrFile(false);
                        }
                        return;
                    } else {
                        //maybe start lockactivity with broadcast
                        if (OpenGesturePresenter.isHaveLockActivity()) {
                            LockPatternUtils.startConfirmPattern(activity);
                        }
                    }
                    //[E] fix bug 7706 by licong for safeLock
                    if (activity.getLocalClassName().contains("com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter")
                            && isPauseActivity.contains("com.xdja.presenter_mainframe.presenter.activity.SplashPresenter")) {
                        synchronized (ActomaApplication.getObjLocker()) {
                            ActomaApplication.setScreenLockerState(true);
                        }
                        LockPatternUtils.startConfirmPattern(activity);
                        return;
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                isPauseActivity = activity.getLocalClassName();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (SettingServer.getSafeLock().equals("true")) {
                    //后台锁定的时候，将变量设置成true
                    if (!isProcessRnning(activity) && SettingServer.getLockBackground()) {
                        synchronized (ActomaApplication.getObjLocker()) {
                            ActomaApplication.setScreenLockerState(true);
                            LogUtil.getUtils().d("onActivityStopped" + SettingServer.getLockBackground());
                        }
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //[S] fix bug 最近任务栏切换 7972 8719  by licong
                if(activity.getLocalClassName().equals("com.xdja.imp.presenter.activity.ChooseIMSessionActivity")){
                    OpenGesturePresenter.isTransmit = false;
                    SafeLockUtil.setIsForwardMessage(false);
                }
                //[E] fix bug 最近任务栏切换 7972 8719
            }
        });

    }

    /**
     * 判断安通+进程是否在前台运行
     *
     * @param context 上下文句柄
     * @return 是否在前台运行
     */
    public static boolean isProcessRnning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            String processName = processInfo.processName;
            if (TextUtils.isEmpty(processName)) {
                return false;
            }
            //[S] fix bug by licong for 6.0 版本锁屏灭屏的判断
            if (processName.equals(context.getPackageName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                            ||  (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING)) {
                        return true;
                    }
                } else {
                    if ((processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                        return true;
                    }
                }
            }
            //[E] fix bug by licong for 6.0 版本锁屏灭屏的判断
        }
        return false;
    }

    private void registerScreenBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.setPriority(1000);
        registerReceiver(mScreenReceiver, filter);
    }

    /**
     * 停止screen状态更新
     */
    public void unRegisterScreenStateUpdate() {
        unregisterReceiver(mScreenReceiver);
    }


    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_USER_PRESENT.equals(action) ||
                    Intent.ACTION_SCREEN_OFF.equals(action) /*||
                    Intent.ACTION_SCREEN_ON.equals(action)*/) {
                //当前界面不是通话界面，收到灭屏广播，执行业务
                if (isSetPattern.equals("true") && SettingServer.getLockScreen()
                        && !OpenGesturePresenter.isHaveLockActivity()) {

                    synchronized (ActomaApplication.getObjLocker()) {
                        synchronized (ActomaApplication.getObjLocker()) {
                            ActomaApplication.setScreenLockerState(true);
                        }
                        if (isProcessRnning(context)) {
                            if(!ActivityStack.getInstanse().getTopActivity().getLocalClassName().equals("com.xdja.voipsdk.InCallPresenter")){
                                sendBroadcast(new Intent(SCREEN_BROADCAST));
                            }
                        }
                    }
                }
            }
        }
    }
}
