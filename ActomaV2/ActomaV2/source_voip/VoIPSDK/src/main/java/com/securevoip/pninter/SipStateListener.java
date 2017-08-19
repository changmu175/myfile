package com.securevoip.pninter;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;

/**
 * Created by xjq on 15-12-17.
 */
public interface SipStateListener {
    void onCallStateChanged(SipCallSession session);
    void onRegStateChanged(boolean success, int code);
    void onMediaStateChanged(MediaState mediaState);
    void onVoiceBluetoothScoEnable();
    void onNetworkStateChanged(boolean connected, String networkType);
    void onCallParamChanged(int code);
    void onServiceStateChanged(boolean on);
}
