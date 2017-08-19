package com.xdja.comm.server;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * <p>Summary:推送中转处理</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.server</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/19</p>
 * <p>Time:19:28</p>
 */
public class NotificationIntent extends Intent {
    /**
     * 推送处理界面的Action
     */
    public static final String notificationAction = "com.xdja.actoma.push.handler";
    /**
     * 目标界面便签名称
     */
    public static final String TAG_TARGET_ACTIVITY = "targetActivity";

    /**
     * 更新Activity标记
     */
    public static final String EXTRA_UPDATE_ACTIVITY = "EXTRA_UPDATE_ACTIVITY";

    /**
     * @param targetName 推送跳转目标界面类名
     */
    public NotificationIntent(@NonNull String targetName) {
        super(notificationAction);
        //this.setClassName(notificationHandlerPkg, notificationHandlerClsName);
        this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.putExtra(TAG_TARGET_ACTIVITY, targetName);
    }

    /**
     * 将该Intent转化为PendingIntent
     *
     * @param context 上下文句柄
     * @return 目标意图
     */
    public PendingIntent buildPendingIntent(@NonNull Context context,int reqCode) {
//        return PendingIntent.getActivity(context, reqCode, this,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        return PendingIntent.getBroadcast(context, reqCode, this,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // add "creator" by ycm for lint 2017/02/10 [start]
    public static final Parcelable.Creator<NotificationIntent> CREATOR = new Parcelable.Creator<NotificationIntent>()
    {
        public NotificationIntent createFromParcel(Parcel in)
        {
            return new NotificationIntent(in.readString());
        }

        public NotificationIntent[] newArray(int size)
        {
            return new NotificationIntent[size];
        }
    };
    // add "creator" by ycm for lint 2017/02/10 [end]
}
