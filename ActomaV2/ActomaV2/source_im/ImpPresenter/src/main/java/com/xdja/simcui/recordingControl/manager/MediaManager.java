package com.xdja.simcui.recordingControl.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;

import com.securevoipcommon.VoipFunction;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.TelphoneState;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.util.IMMediaPlayer;

/**
 * 1）听筒模式、耳机模式、蓝牙模式
 * 2）和Voip之间的切换对接问题
 * Created by lll on 2016/11/8.
 */

public class MediaManager {
    private static final int VERSION = Build.VERSION.SDK_INT;

    private final AudioManager mAudioManager;

    private PowerManager.WakeLock mWakeLock;

    /**
     * 是否熄灭屏幕
     */
    private boolean isWakeAcquire = false;

    private boolean isReceiverMode = false;

    private boolean isHeadsetOn = false;

    public boolean isReceiverMode() {
        return isReceiverMode;
    }

    public void setReceiverMode(boolean receiverMode) {
        isReceiverMode = receiverMode;
    }

    private static class SingletonInstance {
        private static final MediaManager mInstance = new MediaManager();
    }

    @SuppressLint("InlinedApi")
    private MediaManager() {
        Context context = ActomaController.getApp();

        //音频管理模块
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(VERSION >= Build.VERSION_CODES.LOLLIPOP) {
            //电源管理模块
            PowerManager pManager = (PowerManager) ActomaController.getApp()
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, LogUtil.tag);
        }
    }

    public static MediaManager getInstance() {
        return SingletonInstance.mInstance;
    }


    /**
     * 听筒模式切换
     *
     * @param isReceiverMode 是否为听筒模式
     */
    public void onSensorChanged(boolean isReceiverMode) {

        if (!IMMediaPlayer.isPlaying()) {
            if (isWakeAcquire) {
                wakeLockBrightRelease();
            }
            return;
        }

        if (isReceiverMode) {
            setReceiverModeOn();
        } else {
            setReceiverModeOff();
        }
    }

    /**
     * 设置耳机插入状态
     *
     * @param headsetOn true:插入耳机  false 未插入
     */
    public void setHeadsetOn(boolean headsetOn) {
        isHeadsetOn = headsetOn;
    }


    /**
     * 线控耳机是否插入
     *
     * @return
     */
    public boolean isHeadsetOn() {
        return isHeadsetOn ||
                mAudioManager.isWiredHeadsetOn();
    }

    /**
     * 还原音频mode，音频停止播放时调用
     */
    public void restoreAudioMode() {
        /** 20170217-mengbo-start: bug-7853 此处先简单处理，系统电话或voip响铃、通话中时，不设置音频模式，否则ace上表现，声音放大 review by liangliang **/
        if (!TelphoneState.getPhotoStateIsIdle(ActomaController.getApp()) ||
                VoipFunction.getInstance().hasActiveCall() ||
                VoipFunction.getInstance().isMediaPlaying()) {
            return;
        }
        /** 20170217-mengbo-end **/
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 打开听筒模式
     */
    public void setReceiverModeOn() {
        //设置为通话模式
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 关闭听筒模式
     */
    public void setReceiverModeOff() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 申请设备电源锁，灭屏
     */
    public void wakeLockBrightAcquire() {
        if (mWakeLock != null && !mWakeLock.isHeld()) {
            isWakeAcquire = true;
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }
    }

    /**
     * 释放电源设备锁，亮屏
     */
    public void wakeLockBrightRelease() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            isWakeAcquire = false;
            mWakeLock.release();
        }
    }

    public void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            isWakeAcquire = false;
            mWakeLock.release(); // 释放设备电源锁
        }
    }

    /**
     * 是否熄灭屏幕
     *
     * @return
     */
    public boolean isWakeAcquire() {
        return isWakeAcquire;
    }
}
