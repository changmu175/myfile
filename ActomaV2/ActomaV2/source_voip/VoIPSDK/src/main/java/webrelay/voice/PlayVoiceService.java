package webrelay.voice;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.csipsimple.api.MediaState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import webrelay.WeakReferenceHandler;

/**
 * Created by gouhao on 2016/3/22.
 */
public class PlayVoiceService extends Service implements IPlayVoiceOperate,IVoiceMediaOperate{
    private static final String TAG = "PlayVoiceService";
    public static final int WHAT_DELAY = 111;

    private MediaPlayer mediaPlayer;

    private int  loopCount;

    private int delay = 0;

    private boolean isReplay=false;

    private MyBinder myBinder = new MyBinder();
    private Callback callback;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public void setCallback(Callback c) {
        this.callback = c;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public void play(AssetFileDescriptor fileDescriptor, int loopCount, int delay) {
        playVoice(fileDescriptor,  loopCount, delay);
    }


    @Override
    public void stopPlay() {
        if (isPlay()){
            /** 20160912-mengbo-start: modify. 异常捕获，java.lang,IllegelStateException, MediaPlayer._stop
             * ,可能mediaplayer未初始化，或release了**/
            try{
                mediaPlayer.stop();
            }catch(Exception e){
                e.printStackTrace();
            }
            /** 20160912-mengbo-end **/
        }
        needToPlay=false;
    }

    //当前模式
    private int  curMode;

    //解决处于缓冲阶段的音频无法停止掉的问题
    private boolean needToPlay=false;

    public void playVoice(Object obj, int loopCount, int delay) {
        if (loopCount==-1) {
            isReplay=true;
        }else {
            isReplay=false;
        }
        needToPlay=true;
        this.loopCount = loopCount;
        this.delay = delay;
        try {
            initMediaPlayer();
            if (isPlay())mediaPlayer.stop();
            voiceMediaManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            setMediaData(obj);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaData(Object obj) {
        try {
            if(obj instanceof String) {
                mediaPlayer.setDataSource((String)obj);
            } else if(obj instanceof AssetFileDescriptor) {
                AssetFileDescriptor fd = (AssetFileDescriptor) obj;
                mediaPlayer.setDataSource(fd.getFileDescriptor(),
                        fd.getStartOffset(), fd.getLength());
            } else if(obj instanceof Uri) {
                mediaPlayer.setDataSource(getBaseContext(), (Uri)obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void release(){
        if (mediaPlayer==null)
            return;
        stopPlay();
        try {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void seekTo(int msec){
        mediaPlayer.seekTo(msec);
    }

    @Override
    public void pause() {
        try {
            mediaPlayer.pause();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private VoiceMediaManager voiceMediaManager;
    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        voiceMediaManager=new VoiceMediaManager(this);
    }

    private void initMediaPlayer(){
        if(mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(completionListener);
        mediaPlayer.setOnPreparedListener(preparedListener);
        mediaPlayer.setOnErrorListener(errorListener);
        mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
    }

    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            startAfterPrepared();
        }
    };

    private void startAfterPrepared() {
        if (needToPlay){
            //mengbo 2016-08-30 start add. 过滤弱网带抖动延迟情况下，出现的 IIIegalStateException crash
            try{
                mediaPlayer.start();
            }catch(Exception e){
                e.printStackTrace();
            }
            //mengbo 2016-08-30 end
         } else {
           stopPlay();
        }
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            startAfterPrepared();
        }
    };



    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(isReplay) {
                replay();
            } else {
                loopCount--;
                if(loopCount > 0) {
                    replay();
                } else {
                    if(callback != null) callback.playFinish();
                    stopPlay();
                }
            }
        }
    };

    private void replay() {
        getExecutorService().execute(new Run() {
            @Override
            public void doRun() {
                try {
                    Thread.sleep(delay);
                    myHandler.obtainMessage(WHAT_DELAY).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isPlay(){

        if (mediaPlayer==null)
            return false;
        try {
            return mediaPlayer.isPlaying();
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    public class MyBinder extends Binder {
        public PlayVoiceService getService() {
            return PlayVoiceService.this;
        }
    }

    public interface Callback{
        void playFinish();
    }


    private ExecutorService getExecutorService(){
        return threadPool;
    }


    /**
     * 是否打开外放
     * @param on
     */
    @Override
    public void  setSpeakerphoneOn(final boolean on){
        if (voiceMediaManager==null)
            return;
         getExecutorService().execute(new Run() {
             @Override
             public void doRun() {
                 voiceMediaManager.setSpeakerphoneOn(on);
             }
         });
    }


    /**
     * 是否静音
     * @param on
     */
    @Override
    public void setMicrophoneMute(final boolean on) {
        if (voiceMediaManager==null)
            return;
        getExecutorService().execute(new Run() {
            @Override
            public void doRun() {
                voiceMediaManager.setMicrophoneMute(on);
            }
        });
    }


    /**
     * 是否蓝牙耳机
     * @param on
     */
    @Override
    public void setBluetoothOn(final boolean on){
        if (voiceMediaManager==null)
            return;
        getExecutorService().execute(new Run() {
            @Override
            public void doRun() {
                voiceMediaManager.setBluetoothOn(on);
            }
        });
    }

    /**
     * 线控耳机是否插入
     * @param on
     */
    @Override
    public void setHeadsetOn(final boolean on) {
        getExecutorService().execute(new Run() {
            @Override
            public void doRun() {
                voiceMediaManager.setHeadsetOn(on);
            }
        });

    }

    /**
     * 获取当前媒体状态
     */
    public MediaState getMediaState(){
      return voiceMediaManager.getMediaState();
    }

    private abstract class Run implements Runnable{
        public abstract void doRun();
        @Override
        public void run() {
            doRun();
        }
    }

    private MyHandler myHandler=new MyHandler(this);
    static class MyHandler extends WeakReferenceHandler<PlayVoiceService>{

        public MyHandler(PlayVoiceService reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(PlayVoiceService reference, Message msg) {
            switch(msg.what) {
                case WHAT_DELAY:
                    try {
                        reference.seekTo(0);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;


            }
        }
    }

    public VoiceMediaManager getVoiceMediaManager(){
        return voiceMediaManager;
    }

}
