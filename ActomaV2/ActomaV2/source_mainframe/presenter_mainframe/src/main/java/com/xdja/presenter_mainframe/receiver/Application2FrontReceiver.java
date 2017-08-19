package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.frame.presenter.ActivityStack;

public class Application2FrontReceiver extends BroadcastReceiver {
    public static final String ACTION_APPLICATION_2_FRONT = "com.xdja.application2front";

    public Application2FrontReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //判空 对第三方发的广播
        if(ActivityStack.getInstanse().getTopActivity()!=null){
            Intent resultIntent = new Intent(context, ActivityStack.getInstanse().getTopActivity().getClass());
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(resultIntent);
        }

    }
}
