package com.xdja.contactcommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contactcommon.push.PushReceiver;

/**
 * Created by wanghao on 2015/9/8.
 *
 */
@Deprecated
public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_ACTION = "com.xdja.actoma.contact.action";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(PushReceiver.validateConfiguration()) {
            LogUtil.getUtils().i("定时启动 维护异常数据启动");
            new ContactModuleProxy().initContactsModule();
        }
    }
}
