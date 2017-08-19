package com.xdja.dependence.uitls;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

/**
 * <p>Summary:获取设备信息</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.uitl</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/22</p>
 * <p>Time:11:17</p>
 */
public class DeviceUtil {
    /**
     * 获取当前手机型号
     *
     * @return 手机型号
     */
    public static String getOSModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统OS版本
     *
     * @return
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备ID
     *
     * @param context 上下文句柄
     * @return 设备ID
     */
    public static String getDeviceId(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
