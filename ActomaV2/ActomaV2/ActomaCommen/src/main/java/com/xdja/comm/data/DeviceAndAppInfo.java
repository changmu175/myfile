package com.xdja.comm.data;

import android.content.Context;

import com.xdja.comm.uitl.DeviceUtil;



/**
 * Created by XURJ on 2015/12/24.
 */
public class DeviceAndAppInfo {
    public static DeviceAndAppInfoBean deviceAndAppInfoBean = null;

    public static DeviceAndAppInfoBean getDeviceAndAppInfo(Context context){
        if(deviceAndAppInfoBean == null){
            deviceAndAppInfoBean = new DeviceAndAppInfoBean();

            deviceAndAppInfoBean.setAppId("AT+");//AT+
            deviceAndAppInfoBean.setAppVersion(DeviceUtil.getClientVersion(context));//应用版本
            deviceAndAppInfoBean.setOsType(1);// 操作系统类型 Android
            deviceAndAppInfoBean.setDeviceName(DeviceUtil.getOSModel());//设备名称
            deviceAndAppInfoBean.setImei(DeviceUtil.getDeviceId(context));//IMEI
            deviceAndAppInfoBean.setOsVersion(DeviceUtil.getOsVersion());//操作系统版本
        }

        return deviceAndAppInfoBean;
    }
    
}
