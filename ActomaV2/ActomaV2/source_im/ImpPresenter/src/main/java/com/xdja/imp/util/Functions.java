package com.xdja.imp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ScreenInfo;

import java.util.List;

/**
 * Created by xrj on 2015/8/8.
 */
public class Functions {


    public static ScreenInfo getScreenInfo(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        ScreenInfo screenInfo=new ScreenInfo();
        screenInfo.setDensity(metric.density);// 屏幕密度（0.75 / 1.0 / 1.5）
        screenInfo.setDensityDpi(metric.densityDpi);// 屏幕密度DPI（120 / 160 / 240）
        screenInfo.setHeight(metric.heightPixels);// 屏幕高度（像素）
        screenInfo.setWidth(metric.widthPixels);// 屏幕宽度（像素）
        return screenInfo;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return true 有网络可用，false 无可用网络
     * @since 2014-3-8 weizg
     */
    @SuppressLint("BooleanMethodIsAlwaysInverted")
    public static boolean isAnyNetworkConnected(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 会话列表显示草稿
     * @return
     */
    public static CharSequence formatDraft(String draft){
        String draftPreStr = "<font color=\"#941100\">"+ ActomaController.getApp().getString(R.string.draft)+"</font>";
        return  Html.fromHtml(draftPreStr + draft);
    }


    /**
     * 是否是锁屏 或者是 黑屏
     *
     * @param context
     * @return
     */
    public static boolean isScreenOffOrLock(Context context) {
        //获取屏幕是否亮屏
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isOn = false;
        boolean isLock = true;
        if (pm != null) {
            isOn = pm.isScreenOn();
        }
        //获取屏幕是否锁屏
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (mKeyguardManager != null) {
            try {
                //修改适配android 4.0的获取锁屏状态
                isLock = mKeyguardManager.inKeyguardRestrictedInputMode();
            } catch (Throwable e) {
                LogUtil.getUtils().e(e.getMessage());
            }
        }

        return !isOn || isLock;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appInfos =  am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info: appInfos) {
            if (info.processName.equals(context.getPackageName())) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取前台activity名称
     * @param context
     * @return
     */
    public static String getCurrentActivityName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = am.getRunningTasks(1).get(0);
        return info.topActivity.getClassName();
    }

    /**
     * Drawabel转换Bitmap
     *
     * @param drawable Drawable 对象
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
