package com.xdja.presenter_mainframe.enc3rd.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.xdja.comm.uitl.StateParams;

import java.util.List;

/**
 * Created by geyao on 2015/8/4.
 * 第三方加密服务通知栏管理器
 */
public class EncryptManager {

    private final String ENC_SERVICE_NAME = "com.xdja.actoma.service.EncryptService";

    /**
     * 第三方加密服务是否开启
     *
     * @param isShow  是否开启
     * @param context 上下文句柄
     */
   // public synchronized void notificationService(final boolean isShow, final Context context) {
//        boolean isServiceRunning = isServiceRunning(context, ENC_SERVICE_NAME);
//        if (!isServiceRunning) {
//            if (isShow) {
//                //显示前台服务通知栏
//                Intent intent = new Intent(context, EncryptService.class);
//                intent.setAction(EncryptService.ACTION_START_SERVICE);
//                context.startService(intent);
//            }
//        } else {
//            if (!isShow) {
//                //显示前台服务通知栏
//                Intent intent = new Intent(context, EncryptService.class);
//                intent.setAction(EncryptService.ACTION_STOP_SERVICE);
//                context.startService(intent);
//                //联系人清空开启加解密通道的对象
//                new EncryptRecordService(context).closeSafeTransfer();
//            }
//        }
//        //-------------wanghao 2015 10 25 这里移动到函数的结束部分
//        //发送广播通知联系人
//        Intent i = new Intent();
//        i.setAction("com.xdja.actoma.uitl.ENCRYPT_SERVICE");
//        i.putExtra("isEncryptServiceOpen", isShow);
//        context.sendBroadcast(i);
    //}

    /**
     * 第三方加密服务开启
     *
     * @param context  上下文句柄
     * @param nickName 昵称
     */
   // public void contactNotificationService(Context context, String nickName) {
//        //显示前台服务通知栏
//        Intent intent = new Intent(context, EncryptService.class);
//        intent.setAction(EncryptService.ACTION_START_SERVICE);
//        intent.putExtra("nickName", nickName);
//        context.startService(intent);
//
//        //发送广播通知联系人
//        Intent i = new Intent();
//        i.setAction("com.xdja.actoma.uitl.ENCRYPT_SERVICE");
//        i.putExtra("isEncryptServiceOpen", true);
//        context.sendBroadcast(i);
    //}

    /**
     * 查找服务是否正在运行中
     *
     * @param context     上下文句柄
     * @param serviceName 服务名称
     * @return 查找结果
     */
    private boolean isServiceRunning(Context context, String serviceName) {
        boolean isFindService = false;
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

//        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
//            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
//                String sourcePkg = runningServiceInfo.service.getPackageName();
//                String currentPkg = context.getPackageName();
//                if (sourcePkg.equals(currentPkg)) {
//                    CompareSign mCompareSign = new CompareSign();
//                    int result = mCompareSign.compareSign(context, sourcePkg, currentPkg);
//                    if (result == CompareSign.COMPARE_RESULT_SAME) {
//                        isFindService = true;
//                        break;
//                    }
//                }
//            }
//        }
        return isFindService;
    }


    /**
     * 改变通知栏显示内容
     *
     * @param context  上下文句柄
     * @param showName 昵称
     * @param appName  应用名称
     */
    public void changeNotificationContent(Context context, String showName, String appName) {
        Intent intent = new Intent(context, EncryptService.class);
        intent.setAction(EncryptService.ACTION_START_SERVICE);
        intent.putExtra("showName", showName);
        intent.putExtra("appName",appName);
        context.startService(intent);
    }

    /**
     * 清除通知栏通知
     *
     * @param context 上下文句柄
     */
    public void clearNotificaiton(Context context) {
        StateParams.getStateParams().setEncryptAccount(null);
        //显示前台服务通知栏
        Intent intent = new Intent(context, EncryptService.class);
        intent.setAction(EncryptService.ACTION_STOP_SERVICE);
        context.startService(intent);
    }
}
