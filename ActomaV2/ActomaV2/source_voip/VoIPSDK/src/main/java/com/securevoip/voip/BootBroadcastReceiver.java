package com.securevoip.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.csipsimple.service.SipService;

/**
 * 开机启动SipService
 * Created by zjc on 2015/8/17.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION)) {
//            Log.e("BootBroadcastReceiver", "开机广播启动了SipService");
                Intent serviceIntent = new Intent(context, SipService.class);
                context.startService(serviceIntent);
            }
        }


    }


}
