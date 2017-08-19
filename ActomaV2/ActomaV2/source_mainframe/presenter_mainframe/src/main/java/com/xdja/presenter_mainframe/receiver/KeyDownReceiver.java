package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.util.Function;

import webrelay.VOIPManager;


/**
 * Created by licong on 2016/10/10.
 *
 * 海信手机下键广播接收者
 */
public class KeyDownReceiver extends BroadcastReceiver {

    private static final String XDJA_END_CALL_FROM_KEY = "com.android.intent.action.XDJA_END_CALL";
    private static final String XDJA_KEY_DOWN_CALL = "com.xdja.actoma.key.down";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(XDJA_END_CALL_FROM_KEY)){
            LogUtil.getUtils().i("--------收到广播了" + intent.getAction());

            if (Function.isContainsDevice(context)) {
                //如果当前界面的通话界面，发送广播处理业务 否则直接退出应用到后台
                 if (VOIPManager.getInstance().hasActiveCall()) {
                     context.sendBroadcast(new Intent(XDJA_KEY_DOWN_CALL));
                 } else {
                     ActivityStack.getInstanse().moveToBackAllActivities();
                 }
            }
        }
    }
}
