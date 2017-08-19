package com.xdja.imp.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by XURJ on 2016/3/15.
 */
public class AudioFunctions {
    /**
     * @param bMute   true时关闭音乐
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(boolean bMute) {
        boolean bool;
        AudioManager am = (AudioManager) ActomaController.getApp().getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        LogUtil.getUtils().d("pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }
}
