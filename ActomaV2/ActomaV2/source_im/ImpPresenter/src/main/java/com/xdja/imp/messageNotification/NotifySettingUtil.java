package com.xdja.imp.messageNotification;

import android.content.Context;
import android.media.AudioManager;

import com.xdja.comm.server.SettingServer;

public class NotifySettingUtil {

    public NotificationConfigBean getNotifySetting(Context context, boolean notify) {
        NotificationConfigBean notificationConfigBean = new NotificationConfigBean();
        // 系统声音对象
        AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //获取系统是否开启消息提醒
        boolean sysNotify = SettingServer.getNewsRemind();
        //判断当前是否处在勿扰模式下
        boolean currentISNoDisturb = SettingServer.isNoDisturbModeValidNow();
        //获取当前会话对象是否允许消息提醒
        //boolean notify = ChatNotifiSetedCache.getSettingValueByTalkId(context, talkID);
        // 获取通知 声音 震动 提醒设置
        if (sysNotify && notify) {
            notificationConfigBean.setIsRemind(true);
        } else {
            notificationConfigBean.setIsRemind(false);
        }
        if (notificationConfigBean.getIsRemind()) {

            if (currentISNoDisturb) {
                notificationConfigBean.setHasVoice(false);
                notificationConfigBean.setHasShake(false);
            } else {
                //**START*************ADD BY LYQ 2014-6-17*********判断当前系统情景模式来设置提示模式*****************************************************//
                int mode = audioService.getRingerMode();
                boolean sysShake;
                boolean sysVoice;
                if (mode == AudioManager.RINGER_MODE_SILENT) {//当前系统情景模式为静音
                    sysShake = false;
                    sysVoice = false;
                } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {//当前系统情景模式为震动
                    sysShake = true;
                    sysVoice = false;
                } else {//当前系统情景模式为铃声
                    sysShake = true;
                    sysVoice = true;
                }
                // 获取通知 声音 震动 提醒设置
                //当前系统为震动，应用程序同时也开启，则启用震动,否则关闭
                if (sysShake && SettingServer.getNewsRemindShake()) {
                    notificationConfigBean.setHasShake(true);
                } else {
                    notificationConfigBean.setHasShake(false);
                }
                //当前系统为响铃，应用程序同时也开启，则启用响铃,否则关闭
                if (sysVoice && SettingServer.getNewsRemindRing()) {
                    notificationConfigBean.setHasVoice(true);
                } else {
                    notificationConfigBean.setHasVoice(false);
                }
            }
        }
        return notificationConfigBean;
    }


}
