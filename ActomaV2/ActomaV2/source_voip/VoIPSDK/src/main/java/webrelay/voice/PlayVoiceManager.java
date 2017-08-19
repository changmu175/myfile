package webrelay.voice;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.os.Message;

import com.csipsimple.api.MediaState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import webrelay.WeakReferenceHandler;

/**
 * Created by gouhao on 2016/3/22.
 */
@SuppressLint("SynchronizeOnNonFinalField")
public class PlayVoiceManager implements IPlayVoiceOperate,IVoiceMediaOperate{
    private static final String TAG = "PlayVoiceManager";
    private static PlayVoiceManager instance;

    private Context context;
    private PlayVoiceService voiceService;
    private List<Callback> callbackList;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            voiceService = ((PlayVoiceService.MyBinder)iBinder).getService();
            voiceService.setCallback(new PlayVoiceService.Callback() {
                @Override
                public void playFinish() {
                    synchronized (callbackList) {
                        for(Callback c : callbackList) {
                            c.playFinish();
                        }
                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            voiceService = null;
        }
    };


    public void addCallback(Callback c) {
        synchronized (callbackList) {
            if(!callbackList.contains(c)) {
                callbackList.add(c);
            }
        }
    }

    public void removeCallback(Callback c) {
        synchronized (callbackList) {
            callbackList.remove(c);
        }
    }

    private PlayVoiceManager(Context context) {
        this.context = context;
        callbackList = new ArrayList<>();
        /** 20170323-mengbo-start: 加入避免进程被杀后报错 **/
        if(context != null){
            context.bindService(new Intent(context, PlayVoiceService.class),
                    serviceConnection, Context.BIND_AUTO_CREATE);
        }
        /** 20170323-mengbo-end **/
    }

    public static PlayVoiceManager getInstance(Context context) {
        if(instance == null) {
            instance = new PlayVoiceManager(context);
        }
        return instance;
    }

    public void unbindService() {
        context.unbindService(serviceConnection);
        instance = null;
        callbackList = null;
    }

    @Override
    public void play(AssetFileDescriptor fileDescriptor, int loopCount, int delay) {
        if (voiceService==null)
            return;
        voiceService.play(fileDescriptor, loopCount, delay);
    }

    @SuppressLint("SimplifiableIfStatement")
    public boolean isPlaying() {
        if (voiceService != null){
            return voiceService.isPlay();
        }else{
            return false;
        }
    }

    @Override
    public void stopPlay() {
        myHandler.removeMessages(TAG_PLAY);
        if (voiceService == null)
            return;
        voiceService.stopPlay();
    }


    @Override
    public void pause() {
        if (voiceService==null)
            return;
        voiceService.pause();
    }

    @Override
    public void setSpeakerphoneOn(boolean on) {
        if (voiceService==null)
            return;
        voiceService.setSpeakerphoneOn(on);
    }

    @Override
    public void setMicrophoneMute(boolean on) {
        if (voiceService==null)
            return;
        voiceService.setMicrophoneMute(on);
    }

    @Override
    public void setBluetoothOn(boolean on) {
        if (voiceService==null)
            return;
        voiceService.setBluetoothOn(on);
    }

    @Override
    public void setHeadsetOn(boolean on) {
        if (voiceService==null)
            return;
        voiceService.setHeadsetOn(on);
    }


    @Override
    public MediaState getMediaState() {
        if (voiceService==null)
            return null;
       return voiceService.getMediaState();
    }

    public interface Callback{
        void playFinish();

//        void bindServiceSuccess();
//
//        void unbindService();
    }


    //assets资源名称
    public static String FILE_RING = "ring.mp3";

    private Context cxt;
    private String fileName;
    private int num;

    private static final int TAG_PLAY = 0;

    /**
     * 设置播放资源fileName
     *
     * @param fileName
     */
    public void playVoice(Context cxt, String fileName, int num, boolean firstDelay) {
        this.cxt = cxt.getApplicationContext();
        this.fileName = fileName;
        this.num = num;
        if (firstDelay) {
            myHandler.sendEmptyMessageDelayed(TAG_PLAY, 2000);
        } else {
            playVoice(this.cxt, fileName, num);
        }

    }

    private void play(){
        playVoice(cxt, fileName, num);
    }


    private void playVoice(Context cxt, String fileName, int num) {
        // 清除延时播放的message xjq
        myHandler.removeMessages(TAG_PLAY);
        try {
            AssetManager assetManager = cxt.getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
            play(fileDescriptor, num, 0);
            fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {

        setSpeakerphoneOn(false);
        setMicrophoneMute(false);
        setBluetoothOn(false);
        //wxf@xdja.com
        setHeadsetOn(false);
    }


    private MyHandler myHandler=new MyHandler(this);
    static class   MyHandler extends WeakReferenceHandler<PlayVoiceManager> {


        public MyHandler(PlayVoiceManager reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(PlayVoiceManager reference, Message msg) {
            if (msg.what == 0 ) {
                reference.play();
            }
        }
    }

    public PlayVoiceService getVoiceService(){
        return voiceService;
    }

}
