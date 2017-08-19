package com.xdja.contact.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.presenter.activity.FriendRequestHistoryPresenter;

import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by wanghao on 2015/8/9.
 * 联系人模块通知栏显示通知时需要调用当前对象
 *
 */
public class NotificationUtil {

    static NotificationManager notificationManager;

    static {
        notificationManager = (NotificationManager)ActomaController.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
    }
    //[S]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]
//    public static final int NOTIFICATION_ID = 100;
    //[E]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]
    /**
     * 当token失效时调用通知显示
     */
    public static void showTokenExpiredNotification(){
        NotificationParams notificationParams = new NotificationParams.Builder().tokenExpired();
        showNotification(getDefaultBitmap(), notificationParams);
    }

    /**
     * 显示正常的通知消息
     */
    public static void showNotification(String content, Avatar avatar){
        final NotificationParams notificationParams = new NotificationParams.Builder().normalParams(content);
        //notificationParams.setContentText(content);
        if(!ObjectUtil.objectIsEmpty(avatar) && !ObjectUtil.stringIsEmpty(avatar.getAvatar())){
            Glide.get(ActomaController.getApp()).register(GlideUrl.class,
                    InputStream.class,
                    new OkHttpUrlLoader.Factory(OkHttpsClient.getInstance(ActomaController.getApp()).getOkHttpClient()));
//            Glide.with(ActomaController.getApp()).load(RequestHeadImage.getGlideUrl(avatar.getAvatar())).asBitmap().into(new SimpleTarget<Bitmap>() {
            Glide.with(ActomaController.getApp()).load(avatar.getThumbnail()).asBitmap().into(new SimpleTarget<Bitmap>() {//modify by lwl
                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    LogUtil.getUtils().e("NotificationUtil onLoadFailed "+e.toString());
                    showNotification(getDefaultBitmap(), notificationParams);
                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    showNotification(splitImage(resource), notificationParams);
                }
            });
        }else {
            showNotification(getDefaultBitmap(), notificationParams);
        }
    }


    private static Bitmap getDefaultBitmap(){
        return BitmapFactory.decodeResource(ActomaController.getApp().getResources(), R.drawable.notification_logo);
    }


    private static Notification.Builder getSystemNotificationBuilder(){
        Context context = ActomaController.getApp();
        AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioService.getRingerMode();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setWhen(System.currentTimeMillis());
        if(ringerMode == AudioManager.RINGER_MODE_NORMAL && !openSoundReminder()){
             builder.setDefaults(Notification.DEFAULT_SOUND);
        }else{
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        return builder;
    }

    private static void showNotification(Bitmap resource, NotificationParams params){
        Notification.Builder builder = getSystemNotificationBuilder();
        builder.setLargeIcon(resource);
        builder.setSmallIcon(R.drawable.notification_logo);
        builder.setTicker(params.getContentText());
        builder.setContentTitle(params.getContentTitle());
        builder.setContentText(params.getContentText());
        builder.setContentIntent(builderIntent(params));
        builder.setFullScreenIntent(builderIntent(params), true);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        notificationManager.notify(params.getNotificationId(), notification);
    }

    private static PendingIntent builderIntent(NotificationParams params){
        //start:add by wal@xdja.com for 3322
//        Intent intent = new Intent(params.getContext(),FriendRequestHistoryPresenter.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        return PendingIntent.getActivity(params.getContext(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationIntent notificationIntent = new NotificationIntent(FriendRequestHistoryPresenter.class.getName());
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = notificationIntent.buildPendingIntent(params.getContext(),((int) System.currentTimeMillis()));
        return  pendingIntent;
        //end:add by wal@xdja.com for 3322
    }


    private static Bitmap splitImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }


    /**
     * 是否 勿扰模式开启声音提醒
     * @return
     */
    private static boolean openSoundReminder(){
        boolean model = SettingServer.getNoDisturbModelOpened();
        long noDisturbModelBT = SettingServer.getNoDisturbModelBeginTime();
        long noDisturbModelET = SettingServer.getNoDisturbModelEndTime();
        long currentSystemMillons = System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(currentSystemMillons);
        int mHour=mCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinutes=mCalendar.get(Calendar.MINUTE);
        long currentTime = (mHour * 3600 + mMinutes * 60) * 1000;
        if(model){
            //如果结束时间小雨开始时间说明是跨天
            if(noDisturbModelET<noDisturbModelBT){
                if((currentTime>noDisturbModelBT&& currentTime<=24*3600*1000) || (currentTime>=0&& currentTime<=noDisturbModelET)){
                    return true;
                }
            }else {
                if (currentTime >= noDisturbModelBT && currentTime <= noDisturbModelET) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void cancelNotification(){
        if (notificationManager != null) {
            //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
            notificationManager.cancel(NotifiParamUtil.CONTACT_NOTIFI_ID);
            //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
        }
    }
}
