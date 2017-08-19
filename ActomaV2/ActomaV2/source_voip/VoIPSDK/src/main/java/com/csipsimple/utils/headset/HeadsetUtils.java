package com.csipsimple.utils.headset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by MengBo on 2016/9/4.
 */
public class HeadsetUtils  extends HeadsetWrapper {

    private static final String THIS_FILE = "HeadsetUtils";
    private Context context;

    private boolean isConnected = false;

    public HeadsetUtils(Context context){
        this.context = context;
    }

    private BroadcastReceiver mediaStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action == null){
                return;
            }
            LogUtil.getUtils(THIS_FILE).e("HEADSET--onReceive--intent.getAction():" + action);
            if(Intent.ACTION_HEADSET_PLUG.equals(action)){
                if(intent.hasExtra("state")){
                    if(intent.getIntExtra("state",0) == 0){
                        LogUtil.getUtils(THIS_FILE).e("HEADSET--onReceive--HEADSET_STATE_DISCONNECTED");
                        isConnected = false;
                        if(headsetConnectChangeListener != null) {
                            headsetConnectChangeListener.onHeadsetDisconnected();
                        }
                    }else if(intent.getIntExtra("state",0) == 1){
                        LogUtil.getUtils(THIS_FILE).e("HEADSET--onReceive--HEADSET_STATE_CONNECTED");
                        isConnected = true;
                        if(headsetConnectChangeListener != null) {
                            headsetConnectChangeListener.onHeadsetConnected();
                        }
                    }
                }
            }
        }
    };

    public void register() {
        LogUtil.getUtils(THIS_FILE).d("Register headset media receiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(mediaStateReceiver, intentFilter);
    }

    public void unregister() {
        try {
            LogUtil.getUtils(THIS_FILE).d("Unregister headset media receiver");
            context.unregisterReceiver(mediaStateReceiver);
        }catch(Exception e) {
            e.printStackTrace();
            LogUtil.getUtils(THIS_FILE).w("Failed to unregister media state receiver");
        }
    }

    @Override
    public boolean isHeadsetConnected() {
        return isConnected;
    }
}
