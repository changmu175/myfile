package com.securevoip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackDoorReceiver extends BroadcastReceiver {

    public static final String BACK_DOOR_ACTION = "";

    public BackDoorReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        ToastUtil.showNoRepeatToast(context, "哈哈");
//        Intent launchIntent = new Intent(context, BackDoorActivity.class);
//        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(launchIntent);

    }

}
