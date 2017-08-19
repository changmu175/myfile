package com.securevoip.voip;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.csipsimple.api.SipCallSession;
import com.xdja.dependence.uitls.LogUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/4.
 */

@SuppressLint("AndroidLintRegistered")
public class PlayAlertToneService extends Service {
    private final static String THIS_FILE = "PlayAlertToneService";

    public static final String OPEN = "open";
    public static final String PLAY_ID = "play_id";
    public static final String USE_SPEAKER = "use_speaker";
    public static final String USE_BLUETOOTH = "use_bluetooth";

    private Timer stopServiceTimer;

    private MediaPlayer mediaPlayer;
    /**
     * 是否再播放一次
     */
    private boolean playOneMoreTime = true;
    private int playID = 0;
    private AudioManager audioManager;
    private boolean useBluetooth;
    private boolean useSpeaker;

    public PlayAlertToneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            if (null != stopServiceTimer) {
                stopServiceTimer.cancel();
                stopServiceTimer = null;
            }
            //true播放，false停止播放
            if (intent.getBooleanExtra(OPEN, false)) {
                playID = intent.getIntExtra(PLAY_ID, 0);
                useSpeaker = intent.getBooleanExtra(USE_SPEAKER, false);
                useBluetooth = intent.getBooleanExtra(USE_BLUETOOTH, false);
                if (playID != 0) {
                    playAlertTone(this, playID, useSpeaker, useBluetooth);
                }
            } else {
                stopAlertTone();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放提示音
     *
     * @param context
     * @param playID
     * @param useSpeaker 一般情况下是false，不使用扬声器。如果对方未接听，超时挂断前自己按下扬声器，这个地方是true，使用扬声器。
     */
    private void playAlertTone(final Context context, final int playID, boolean useSpeaker, boolean useBluetooth) {
        //每次播放时，状态是true，代表可以播放两次。
        playOneMoreTime = true;
        //if (audioManager == null) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        //}
        try {
//            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor afd = null;
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                if (playID == SipCallSession.StatusCode.BUSY_HERE) {
                    //mediaPlayer = MediaPlayer.create(context, R.raw.busy_here_486);
//                    afd = context.getResources().openRawResourceFd(R.raw.actoma_486_11);
//                    afd = assetManager.openFd("busy_here_486.wav");
                } else if (playID == SipCallSession.StatusCommentReplace.REQUEST_TIMEOUT_CODE) {
                    //mediaPlayer = MediaPlayer.create(context, R.raw.request_timeout_408);
//                    afd = context.getResources().openRawResourceFd(R.raw.actoma_408_9);
//                    afd = assetManager.openFd("request_timeout_408.wav");
                } else if (playID == SipCallSession.StatusCode.NOT_FOUND) {
                    //mediaPlayer = MediaPlayer.create(context, R.raw.not_fount_404);
//                    afd = context.getResources().openRawResourceFd(R.raw.actoma_404_9);
//                    afd = assetManager.openFd("not_fount_404.wav");
                }
                if (afd != null) {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //zjc 20151119 现在一旦开始播放蓝牙耳机是无法切换模式的，所以先注释掉这块
        if (!useBluetooth) {
            if (useSpeaker) {
                //扬声器模式
                //audioManager.setMode(AudioManager.MODE_NORMAL);
                //audioManager.setSpeakerphoneOn(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//                LogUtil.e(THIS_FILE, MediaManager.getCurrentSpeakerMode(audioManager.isSpeakerphoneOn()));
            } else {
                //听筒模式
//            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//            audioManager.setMode(AudioManager.MODE_IN_CALL);
//            audioManager.setSpeakerphoneOn(false);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//                LogUtil.e(THIS_FILE, MediaManager.getCurrentSpeakerMode(audioManager.isSpeakerphoneOn()));
            }
        }
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

        //获取音频焦点，如果不获取，则其他软件播放音乐情况下播放提示音，会有各种异常
        requestAudioFocus();

        try {
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (playOneMoreTime) {
                            //播放一次之后，再播放一次，共两次
                            if (null != mediaPlayer) {
                                mediaPlayer.start();
                            }
                        } else {
                            //播放完成之后，MediaPlayer置空，重置模式
                            stopAlertTone();
                        }
                        playOneMoreTime = false;
                    }
                });
                LogUtil.getUtils().e(THIS_FILE + "prepare()执行了");
//                这个地方一定要用try-catch包围，抛出IllegalStateException异常，否则会在prepare时崩溃，例如，确定对方不在线的情况下，连点两次拨打
//                原因大致如下文所属
//                http://lovelease.iteye.com/blog/2105616
                mediaPlayer.prepare();
                //休眠是为了防止爆音
                Thread.sleep(500);
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止提示音
     */
    private void stopAlertTone() {
        if (mediaPlayer != null) {
            boolean isPlaying = false;
            try {
                isPlaying = mediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
//                mediaPlayer = null;
//                mediaPlayer = new MediaPlayer();
            }
            if (isPlaying) {
                mediaPlayer.stop();
                /*StopHandler handler = new StopHandler();
                handler.sendEmptyMessage(0);*/
                if (stopServiceTimer == null) {
                    stopServiceTimer = new Timer();
                    //若20秒内没有下一次提示音播放，服务自动停止
                    stopServiceTimer.schedule(new StopTimerTask(), 20000);
                }
            }
            mediaPlayer.release();
            mediaPlayer = null;
            //zjc 20151119 现在有时会影响到蓝牙耳机的模式，暂时找不到原因，先去掉所有手动设置模式的地方
            //setAudioRingMode();
        }
        //播放完成或者结束播放之后停止service，减少不必要的消耗
        //stopSelf();
    }

    /**
     * 设置成正常的响铃模式
     */
    private void setAudioRingMode() {
        //挂断电话之后，把声音模式恢复成响铃模式；
        //注意，播放提示音的时候是音乐模式，安卓音频模式切换需要时间，所以，在电话挂断回到通话记录界面的前两秒左右如果调节声音依旧是音乐模式。
        if (!useBluetooth) {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);
        }
        audioManager.abandonAudioFocus(afChangeListener);
//        LogUtil.e(THIS_FILE, "设置正常模式之后： " + MediaManager.getCurrentAudioMode(audioManager.getMode()));
    }

    /**
     * .
     * <p/>
     * <p/>
     * 获取音频焦点
     *
     * @return
     */
    private boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                // Stop playback
                audioManager.abandonAudioFocus(afChangeListener);
                stopAlertTone();
            }

        }
    };

    private class StopTimerTask extends TimerTask {
        @Override
        public void run() {
            stopSelf();
        }
    }

}
