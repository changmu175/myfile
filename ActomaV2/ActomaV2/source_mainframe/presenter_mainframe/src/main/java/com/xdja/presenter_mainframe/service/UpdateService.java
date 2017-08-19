package com.xdja.presenter_mainframe.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.receiver.UpdateReceiver;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * 版本升级定时检测服务
 * 该服务启动后做两件事（1、检测升级，2、升级策略）
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-10 13:23
 */
public class UpdateService extends Service {
    /**
     * 定时检测配置信息
     */
    private static final int HourOfDay = 16;

    private static final int MINUTE_RANDOM = 60;
    private static final int HOUR_OF_DAY = 24;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.getUtils().e("UpdateService - onStartCommand");
        if (intent != null && AutoUpdate.ACTION_CHECK_UPDATE.equals(intent.getAction())) {
            onCreateRegularBroadcast();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getUtils().e("UpdateService - onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getUtils().e("UpdateService - onDestroy");
    }

    /**
     * 定时发送检测升级广播
     */
    public void onCreateRegularBroadcast() {
        //随机生成一个分钟数
        Random random = new Random();
        int minute = random.nextInt(MINUTE_RANDOM);
        LogUtil.getUtils().i("UpdateService - 定时分钟：" + minute);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        //设置检测时间
        calendar.set(Calendar.HOUR_OF_DAY, HourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //设置第二天同一时间的闹钟
        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
        //设置检测时间
        calendar1.set(Calendar.HOUR_OF_DAY, HourOfDay);
        calendar1.set(Calendar.MINUTE, minute);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.add(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);

        Intent intent = new Intent(AutoUpdate.ACTION_CHECK_UPDATE_TIMER);
        intent.setClass(getApplicationContext(), UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        /*
        1.如果当前时间超过上面获得到的定时的时间，就设置闹钟从第二天的当前时间开始检测
        2.如果没有到达上面定时的时间，就从上面定时的时间开始
        */
        if (System.currentTimeMillis() < calendar.getTimeInMillis()) {//设定的时间在当前时间之后
            LogUtil.getUtils().i("UpdateService - 定时更新 设定的时间在当前时间之后 - 设置为今天的" + calendar.getTime());
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            LogUtil.getUtils().i("UpdateService - 定时更新 设定的时间在当前时间之前 - 设置为明天的" + calendar1.getTime());
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
