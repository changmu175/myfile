package com.xdja.dependence.uitls;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
public class AppUtil {
    /**
     * 判断程序是否后台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager =
                ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName()))
                return appProcess.importance
                        != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        }
        return false;
    }
}
