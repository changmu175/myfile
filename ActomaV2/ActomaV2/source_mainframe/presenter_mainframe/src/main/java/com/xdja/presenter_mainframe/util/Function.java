package com.xdja.presenter_mainframe.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.xdja.comm.server.ConfigurationServer;
import com.xdja.dependence.uitls.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cb on 2015/10/13.
 */
public class Function {
    /**
     * 判断是否是移动网络
     * @param context
     * @return
     */

    @SuppressWarnings("deprecation")
    public static boolean isMobConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //如果是wifi连接成功，就表示有网络可用
            if (wifiNetInfo.isConnected()) {
                return false;
            } else {
                //如果是仅移动网络可用，就要判断是否开启仅wifi可用
                if (mobNetInfo.isConnected()) {
                    return true;
                } else {//没有网络
                    return false;
                }
            }
        }
    }




    /**
     * 判断w网络可用时，是否是移动网络
     * @param context
     * @return
     */

    @SuppressWarnings("deprecation")
    public static boolean isMobConnectActive(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            //如果是手机网络可用则返回true
            if (mobNetInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断程序是否后台运行
     * @param context
     * @return
     */
    public static boolean isBackground(Context context){
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.
                getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess :appProcesses){
            if (appProcess.processName.equals(context.getPackageName())){
                LogUtil.getUtils().i(context.getPackageName() + "getName----" + context.getClass().getName());

                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    LogUtil.getUtils().i("处于后台" + appProcess.processName);
                    return true;
                }else {
                    LogUtil.getUtils().i("处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * 时间转换
     * @param time
     * @return
     */
    @SuppressWarnings("SimpleDateFormatWithoutLocale")
    public static String getTime(long time)  {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String result = format.format(date);
        return result;
    }

    /**
     * 判读是否有网络
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isNetConnect(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetInfo.isConnected() || mobNetInfo.isConnected()) {//网络已开启
            return true;
        } else {//网络未开启
            return false;
        }
    }

    /**
     * 简单退出
     * @param context
     */
    //public static void simpleLogout(Context context) {
        /*EncryptManager manager = new EncryptManager();
        manager.notificationService(false, context);

        //清除强制退出的通知栏信息
        NotificationManager mNotificationManager = (NotificationManager) ActomaApplication.getInstance()
                .getSystemService(ActomaApplication.getInstance().NOTIFICATION_SERVICE);
        mNotificationManager.cancel(LogouthHandle.NOTIFICATION_ID);

        //重置保存状态
        PreferencesServer.getWrapper(context).setPreferenceBooleanValue(LoginComponent.logoutTag, true);

        //退出清空框架的ticket
        PreferencesServer.getWrapper(context).setPreferenceStringValue(LoginComponent.ticketTag, "");*/
    //}

    /**
     * 配置中是否有当前设备
     * @param context
     * @return
     */
    public static boolean isContainsDevice(Context context){
        ConfigurationServer assetsConfig = ConfigurationServer
                .getAssetsConfig(context.getApplicationContext());
        if (assetsConfig != null) {
            String customDevice = assetsConfig.read("customDevice", "", String.class);
            if (!TextUtils.isEmpty(customDevice)) {
                if (customDevice.contains(Build.DEVICE)) {
                    return true;
                }
            }
        }
        return false;
    }
}
