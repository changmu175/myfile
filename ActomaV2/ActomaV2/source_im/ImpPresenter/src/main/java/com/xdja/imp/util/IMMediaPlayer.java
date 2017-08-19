package com.xdja.imp.util;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.SettingServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.simcui.recordingControl.manager.MediaManager;

import java.io.File;
import java.io.FileInputStream;

public class IMMediaPlayer {
    private static MediaPlayer mMediaPlayer = null;

    private static String playingVoiceFile = "";

    private static long msgID;

    private static final Object object = new Object();

    /**
     * 播放音频消息
     * <p/>
     * 消息类型 1 自己发送 2接收
     */
    public static void startPlay(String filePath, long msgId, boolean mine) {
        //modify by zya@xdja.com,review by zya@xdja.com,20160825 ,发送方和接收方文件的处理
        if(mine && (filePath.endsWith(ConstDef.FILE_ENCRPTY_SUFFIX))){
            filePath = filePath.substring(0, filePath.lastIndexOf("."));
        }//end

        playVoice(filePath, msgId, 0);
    }

    /**
     * 重新开始播放
     */
    public static void startToRePlay() {
        int duration;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            duration = mMediaPlayer.getCurrentPosition();
            //只有在播放时，才进行重播
            playVoice(playingVoiceFile, msgID, duration);
        }
    }


    /**
     * 停止播放
     */
    public static void stopPlay() {
        //[S]modify by lll@xdja.com fixed bug 6844 2016/12/9
        //先进行模式恢复
        MediaManager.getInstance().restoreAudioMode();
        //[E]modify by lll@xdja.com fixed bug 6844 2016/12/9
        //再停止播放
        stopPlaying(VoicePlayState.STOP);
    }

    /**
     * 发送播放语音状态的广播
     *
     * @param state
     */
    private static void sendPlayStateBroadcast(VoicePlayState state) {
        Intent intent = new Intent();
        intent.setAction(IMAction.VOICE_CHAT_ITEM_BROCAST_STATECHANGE);
        intent.putExtra(IMExtraName.FILENAME, playingVoiceFile);
        intent.putExtra(IMExtraName.VOICE_PLAY_STATE, state.getKey());
        intent.putExtra(IMExtraName.MSGID, msgID);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 处理播放结束
     *
     * @author wzg
     * @since 2014-4-14 下午9:40:59
     */
    private static void stopPlaying(VoicePlayState state) {
        synchronized (object) {
            if (mMediaPlayer != null) {
                try {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMediaPlayer = null;
                AudioFunctions.muteAudioFocus(false);
            }
            // 发送结束播放广播
            sendPlayStateBroadcast(state);
            playingVoiceFile = "";
            msgID = -1;
        }
    }

    /**
     * 开始重新播放
     * @param filePath
     * @param msgId
     * @param duration
     */
    private static void playVoice(String filePath, long msgId, int duration) {
        // 在开始播放之前，先发送广播关闭上一个播放动画
        synchronized (object) {
            // 如果正在播放，则先停止播放
            if (mMediaPlayer != null) {
                try {
                    if (mMediaPlayer.isPlaying()) {
                        //[S]modify by lll@xdja.com fixed bug 6859 2016/12/9
                        if (duration > 0) { //重播（语音闪信不应该销毁）
                            stopPlaying(VoicePlayState.ERROR);
                        } else {
                            stopPlaying(VoicePlayState.STOP);
                        }
                        //[E]modify by lll@xdja.com fixed bug 6859 2016/12/9
                        if (!SettingServer.isReceiverModeOn() ) {
                            MediaManager.getInstance().restoreAudioMode();
                        } else {
                            MediaManager.getInstance().setReceiverModeOff();
                        }
                    } else {
                        mMediaPlayer.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMediaPlayer = null;
            }
            mMediaPlayer = new android.media.MediaPlayer();

            ///播放模式设置
            if (SettingServer.isReceiverModeOn() ||
                    MediaManager.getInstance().isReceiverMode()) {
                MediaManager.getInstance().setReceiverModeOn();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                MediaManager.getInstance().setReceiverModeOff();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            //播放模式设置结束
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mp) {
                    stopPlaying(VoicePlayState.COMPLETION);
                    MediaManager.getInstance().restoreAudioMode();
                }
            });

            mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
                    stopPlaying(VoicePlayState.ERROR);
                    MediaManager.getInstance().restoreAudioMode();
                    return false;
                }
            });

            //如果正在播放当前消息，停止播放
            if (TextUtils.equals(playingVoiceFile, filePath)) {
                stopPlay();
                AudioFunctions.muteAudioFocus(false);
                MediaManager.getInstance().restoreAudioMode();
                return;
            }

            AudioFunctions.muteAudioFocus(true);
            FileInputStream fileInputStream = null;
            try {
                File file = new File(filePath);
                fileInputStream = new FileInputStream(file);
                mMediaPlayer.setDataSource(fileInputStream.getFD());
                mMediaPlayer.prepare();
                if (duration > 0) {
                    mMediaPlayer.seekTo(duration);
                }
                mMediaPlayer.start();
                playingVoiceFile = filePath;
                msgID = msgId;
                // 发送播放语音动画广播
                sendPlayStateBroadcast(VoicePlayState.PLAYING);
            } catch (Exception e) {
                e.printStackTrace();
                stopPlaying(VoicePlayState.ERROR);
                //fix bug 7819 remove by zya 20170104
                //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //end by zya
                MediaManager.getInstance().restoreAudioMode();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        LogUtil.getUtils().e(e.getMessage());
                    }
                }
            }
        }
    }


    public static boolean getVoiceMessageIsPlaying(String filePath, long messageId) {
        //modify by zya@xdja.com,语音文件判断是否正在播放
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(playingVoiceFile)) {
            return false;
        }

        synchronized (object) {
            //发送方文件不带后缀，接收方带后缀
            return (TextUtils.equals(filePath, playingVoiceFile) || filePath.contains(playingVoiceFile))
                    && (msgID == messageId);
        }
        //end
    }

    /**
     * 获取当前正在播放的文件
     * @return playingVoiceFile 文件绝对路径
     */
    public static String getPlayingFile() {
        return playingVoiceFile;
    }

    /**
     * 获取当前正在播放的音频对应的消息Id
     * @return msgID 消息Id
     */
    public static long getPlayingMessageId(){
        return msgID;
    }

    /**
     * 判断当前是否在播放
     * @return
     */
    public static boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying() &&
                !TextUtils.isEmpty(playingVoiceFile);
    }
}