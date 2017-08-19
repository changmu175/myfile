package com.securevoip.pninter;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;

import java.util.ArrayList;

/**
 * Created by xjq on 15-12-16.
 */
public class PNMessageManager {
    private ArrayList<SipStateListener> sipStateListeners = new ArrayList<>();

    private static PNMessageManager manager = new PNMessageManager();

    public static PNMessageManager getInstance(){
        return manager;
    }

    public void addSipStateListener(SipStateListener linstener) {
        if(!sipStateListeners.contains(linstener)) {
            sipStateListeners.add(linstener);
        }
    }

    public void removeSipStateListener(SipStateListener listener) {
        if(sipStateListeners.contains(listener)) {
            sipStateListeners.remove(listener);
        }
    }

    public void callStateChanged(SipCallSession session) {
        for(SipStateListener listener:sipStateListeners) {
            listener.onCallStateChanged(session);
        }
    }

    public void regStateChanged(boolean success, int code) {
        for(SipStateListener listener:sipStateListeners) {
            listener.onRegStateChanged(success, code);
        }
    }

    public void mediaStateChanged(MediaState mediaState) {
        for(SipStateListener listener:sipStateListeners) {
            listener.onMediaStateChanged(mediaState);
        }
    }



    public void voiceBluetoothScoEnable(){
        for(SipStateListener listener:sipStateListeners) {
            listener.onVoiceBluetoothScoEnable();
        }
    }


    public void serviceSateChanged(boolean on) {
        for(SipStateListener listener:sipStateListeners) {
            listener.onServiceStateChanged(on);
        }
    }
}
