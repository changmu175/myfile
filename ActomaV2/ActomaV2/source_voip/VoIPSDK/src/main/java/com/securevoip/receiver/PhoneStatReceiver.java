package com.securevoip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.xdja.dependence.uitls.LogUtil;

import webrelay.VOIPManager;

/**
 * Description:
 * Company    : 信大捷安
 * Author     : wxf@xdja.com
 * Date       : 2016/8/10 20:38
 * Modify     : mengbo@xdja.com 2016/8/20
 */
public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";
    private static boolean incomingFlag = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null || VOIPManager.getInstance().getTelephonyManager() == null) {
            return;
        }

        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            //去电处理
            onPhoneCallOutgoing();
        }else{
            int callState = VOIPManager.getInstance().getTelephonyManager().getCallState();
            switch(callState){
                case TelephonyManager.CALL_STATE_IDLE:
                    incomingFlag = false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if(!incomingFlag){
                        //来电处理
                        onPhoneCallComing();
                        incomingFlag = true;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
        }
    }

    private void onPhoneCallOutgoing(){
        LogUtil.getUtils(TAG).d("#-#-#- PhoneStatReceiver onPhoneCallOutgoing");
        VOIPManager.getInstance().hangup();
    }

    private void onPhoneCallComing(){
        LogUtil.getUtils(TAG).d("#-#-#- PhoneStatReceiver onPhoneCallComing");
        VOIPManager.getInstance().hangup();
    }
}
