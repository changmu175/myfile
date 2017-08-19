package com.csipsimple.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class ScreenUtil {

    public static final String SCREEN_LOCK = "screenLock";
    public static final String SCREEN_LOCK_STATE = "screenLockState";
    /**
     * 屏幕是否被锁
     *
     * @param context context
     * @return 锁屏返回false， 亮屏返回true
     */
    public static boolean isScreenLock(Context context) {
        /**20160818-mengbo-start: 原方法不准确，锁屏状态下，屏幕亮的，会返回false**/
        KeyguardManager mKeyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();

        /**original code:
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return !powerManager.isScreenOn();
         **/
        /**20160818-mengbo-end**/
    }

    /**
     * 存储锁屏状态
     * @param context context
     */
    public static void putScreenState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SCREEN_LOCK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SCREEN_LOCK_STATE, isScreenLock(context));
        android.util.Log.e("isScreenOn", isScreenLock(context) + "");
        editor.apply();
    }

    /**
     * 是否锁屏
     * @param context context
     * @return 锁屏返回true，亮屏返回false
     */
    public static boolean getScreenState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SCREEN_LOCK, Context.MODE_PRIVATE);
        return sp.getBoolean(SCREEN_LOCK_STATE, false);
    }

    /**
     * 当前Activity是否在前台运行
     *
     * @param context context
     * @return 在前台运行返回true，不在返回false
     */
    public boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
