package com.xdja.comm.uitl;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by XURJ on 2016/2/23.
 */
public class TelphoneState {
    public static boolean getPhotoStateIsIdle(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE;// modified by ycm for lint 2017/02/13
    }
}
