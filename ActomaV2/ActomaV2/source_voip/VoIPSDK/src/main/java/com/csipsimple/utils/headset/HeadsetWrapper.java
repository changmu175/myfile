package com.csipsimple.utils.headset;

import android.content.Context;

/**
 * Created by MengBo on 2016/9/4.
 */
public abstract class HeadsetWrapper {

    public interface HeadsetConnectChangeListener {
        void onHeadsetConnected();
        void onHeadsetDisconnected();
    }

    //instance用于通话接听之后的单例，soundInstance用于通话接通之前的单例
    private static HeadsetWrapper instance;
    protected Context context;


    protected HeadsetConnectChangeListener headsetConnectChangeListener;

    public static HeadsetWrapper getInstance(Context context) {
        if(instance == null) {
            instance = new com.csipsimple.utils.headset.HeadsetUtils(context);
        }

        return instance;
    }

    public void setHeadsetConnectChangeListener(HeadsetConnectChangeListener l) {
        headsetConnectChangeListener = l;
    }

    public abstract void register();
    public abstract void unregister();
    public abstract boolean isHeadsetConnected();
}
