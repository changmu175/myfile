package com.xdja.comm.uitl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by XURJ on 2015/12/25.
 */
public class NetUtil {
    /**
     * 飞行模式
     */
    public static final int FLIGHT_MODE = -2;

    /**
     * 没有网络
     */
    public static final int NO_NETWORK = -1;

    public static int getNetType(Context context){
        if(getAirplaneMode(context)){
            //飞行模式
            return FLIGHT_MODE;
        }
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();

        if(info == null){
            //没有网络
            return NO_NETWORK;
        }
        return info.getSubtype();
    }

    /**
     * 判断手机是否是飞行模式
     * @param context
     * @return
     */
    public static boolean getAirplaneMode(Context context){
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) ;
        return isAirplaneMode == 1;// modified by ycm for lint 2017/02/13
    }
}
