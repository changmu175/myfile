package com.xdja.dependence.uitls;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
public class NetworkUtil {
    /**
     * 判断是否是移动网络
     *
     * @param context
     * @return
     */

    public static boolean isMobileConnected(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo mobNetInfo
                = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo
                = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //如果是wifi连接成功，就表示有网络可用
        if (wifiNetInfo.isConnected()) {
            return false;
        }

        return mobNetInfo.isConnected();
    }


    /**
     * 判读是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo
                = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo
                = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (wifiNetInfo.isConnected() || mobNetInfo.isConnected());
    }

}
