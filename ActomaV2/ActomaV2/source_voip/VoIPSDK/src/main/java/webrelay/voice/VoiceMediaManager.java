package webrelay.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Message;

import com.csipsimple.api.MediaState;
import com.csipsimple.service.MediaManager;
import com.csipsimple.utils.bluetooth.BluetoothWrapper;
import com.csipsimple.utils.headset.HeadsetWrapper;
import com.securevoip.pninter.PNMessageManager;
import com.xdja.dependence.uitls.LogUtil;

import webrelay.WeakReferenceHandler;

/**
 * Created by admin on 16/3/26.
 */
public class VoiceMediaManager implements BluetoothWrapper.BluetoothChangeListener, HeadsetWrapper.HeadsetConnectChangeListener  {

    private final static String TAG = "VoiceMediaManager";

    private boolean userWantBluetooth = false;
    private boolean userWantSpeaker = false;
    private boolean userWantMicrophoneMute = false;
    private boolean userWantHeadset = false;

    private AudioManager audioManager;
    private PlayVoiceService playVoiceService;
    //Bluetooth related
    private BluetoothWrapper bluetoothWrapper;
    private HeadsetWrapper headsetWrapper;

    public VoiceMediaManager(PlayVoiceService playVoiceService){
       init(playVoiceService);
    }

    private void init(PlayVoiceService playVoiceService){
        this.playVoiceService=playVoiceService;
        audioManager= (AudioManager) playVoiceService.getSystemService(Context.AUDIO_SERVICE);
        if(bluetoothWrapper == null) {
            bluetoothWrapper = BluetoothWrapper.getInstance( this.playVoiceService);
            //[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
            bluetoothWrapper.setBluetoothChangeListener(this);
            //[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
            bluetoothWrapper.register();
        }

        if(headsetWrapper == null){
            headsetWrapper = HeadsetWrapper.getInstance(this.playVoiceService);
            headsetWrapper.setHeadsetConnectChangeListener(this);
            headsetWrapper.register();
        }
    }

    /**
     * 是否打开外放
     * @param on
     */
    public void  setSpeakerphoneOn(boolean on){
        userWantSpeaker = on;
        audioManager.setSpeakerphoneOn(userWantSpeaker);
        if (userWantSpeaker) { //Speaker 模式和 Bluetooh模式互斥
            userWantBluetooth=false;
        }

        /**20160618-mengbo-start:加入延迟500ms,解决呼叫界面快速切换speaker和听筒，此时声音无法和按钮切换状态保持一致问题**/
        try{
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }
        /**20160618-mengbo-end**/

        updateMediaState();
    }

    public void setMode(int mode){
       audioManager.setMode(mode);
    }

    public int getMode(){
        return audioManager.getMode();
    }

    /**
     * 是否静音
     * @param on
     */
    public void setMicrophoneMute(boolean on) {
        if(on != userWantMicrophoneMute ) {
            userWantMicrophoneMute = on;
            updateMediaState();
        }

    }


    /**
     * 是否蓝牙耳机
     * @param on
     */
    public void setBluetoothOn(boolean on){
        if(on != userWantBluetooth ) {
            userWantBluetooth = on;
            if (userWantBluetooth){
                setSpeakerphoneOn(false);
            }
        }
        //[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
       // bluetoothWrapper.setBluetoothChangeListener(this);
        //[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
        bluetoothWrapper.setBluetoothOn(on);
        /** 20161025-mengbo-start: 连接、断开蓝牙后，延时起作用，更新媒体状态 **/
//        updateMediaState();
        voiceMediaHandler.sendEmptyMessageDelayed(0,delay);
        /** 20161025-mengbo-end **/
    }

    /**
     * 线控耳机是否插入
     * @param on
     */
    public void setHeadsetOn(boolean on){
        if(on != userWantHeadset ) {
            LogUtil.getUtils(TAG).d("#-#-VoiceMediaManager setHeadsetOn userWantHeadset:" + on);
            userWantHeadset = on;
            /** 20161010-mengbo-start: 耳机插入后，关闭扬声器 **/
            if (userWantHeadset){
                setSpeakerphoneOn(false);
            }
            /** 20161010-mengbo-end **/
        }
        updateMediaState();
    }

    /**
     * 得到当前媒体状态
     * @return
     */
    @SuppressLint("SimplifiableIfStatement")
    public MediaState getMediaState() {
        MediaState mediaState = new MediaState();

        // Micro
        mediaState.isMicrophoneMute = userWantMicrophoneMute;
        mediaState.canMicrophoneMute = true; /*&& !mediaState.isBluetoothScoOn*/ //Compatibility.isCompatible(5);

        // Speaker
        mediaState.isSpeakerphoneOn = userWantSpeaker;
        mediaState.canSpeakerphoneOn = true; //Compatibility.isCompatible(5);

        //Bluetooth
        if (bluetoothWrapper!=null){
            mediaState.isBluetoothScoOn = userWantBluetooth && bluetoothWrapper.isBluetoothOn();
            mediaState.canBluetoothSco = bluetoothWrapper.canBluetooth();
        }else {
            mediaState.isBluetoothScoOn =false;
            mediaState.canBluetoothSco = false;
        }

        // Headset
        if (headsetWrapper!=null){
            mediaState.isHeadset = headsetWrapper.isHeadsetConnected();
        }else {
            mediaState.isHeadset =false;
        }

        LogUtil.getUtils(TAG).d("#-#-VoiceMediaManager getMediaState mediaState.isHeadset :" +mediaState.isHeadset+" userWantHeadset："+userWantHeadset
                +" headsetWrapper.isHeadsetConnected():"+headsetWrapper.isHeadsetConnected());

        return mediaState;
    }


    /**
     * 更新当前设备状态  包括 蓝牙耳机、外放、 静音等；
     */
    private void  updateMediaState(){
        MediaState mediaState =this.playVoiceService.getMediaState();
        PNMessageManager.getInstance().mediaStateChanged(mediaState);
    }

    /**
     * 蓝牙由不可用变为可用时调用
     */
    private void bluetoothScoEnable(){
        PNMessageManager.getInstance().voiceBluetoothScoEnable();
    }

    public boolean isUserWantSpeaker(){
        return userWantSpeaker;
    }


    @Override
    public void onBluetoothStateChanged(int status) {

//        if (bluetoothWrapper!=null && bluetoothWrapper.isBluetoothOn())
//        {
//            setMode(AudioManager.MODE_NORMAL);
//        }else {
//            setMode(curMode);
//        }
        LogUtil.getUtils(TAG).d("onBluetoothStateChanged----------status " + status);
        if(status == MediaManager. BT_HEADSET_CON_VAL) {
            //蓝牙耳机连接成功
            voiceMediaHandler.sendEmptyMessageDelayed(0,delay);

        } else if(status ==MediaManager. BT_HEADSET_DISCON_VAL) {
            //蓝牙耳机断开连接
            updateMediaState();
        } else {
            //在蓝牙耳机连接成功的情况下，关闭或打开蓝牙耳机
            updateMediaState();
        }

    }

    @Override
    public void onHeadsetConnected() {
        LogUtil.getUtils(TAG).d("#-#-VoiceMediaManager onHeadsetConnected");

        userWantHeadset = true;
        updateMediaState();
    }

    @Override
    public void onHeadsetDisconnected() {
        LogUtil.getUtils(TAG).d("#-#-VoiceMediaManager onHeadsetDisconnected");

        userWantHeadset = false;
        updateMediaState();
    }

private VoiceMediaHandler voiceMediaHandler=new VoiceMediaHandler(this);

/** 20161025-mengbo-start: 蓝牙连接、断开延时起作用 **/
private static final int  delay=1500;
//private static final int  delay=2000;
/** 20161025-mengbo-end **/

 static class VoiceMediaHandler extends WeakReferenceHandler<VoiceMediaManager>{

    public VoiceMediaHandler(VoiceMediaManager reference) {
        super(reference);
    }

    @Override
    protected void handleMessage(VoiceMediaManager reference, Message msg) {
        /** 20161025-mengbo-start: 蓝牙连接、断开延时起作用 **/
        reference.updateMediaState();
        /** 20161025-mengbo-end **/
    }
}

}
