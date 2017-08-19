package com.xdja.contactcommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contactcommon.push.PushReceiver;

/**
 * Created by wal@xdja.com on 2016/10/27.
 *
 * android.net.conn.CONNECTIVITY_CHANGE
 */
@Deprecated
public class NetChangeUpdateReceiver extends BroadcastReceiver {
    private final  int NET_CHANGE_UPDATE_NUMBER = 1;
    private final String ADJUST_CONTACT_DATA_TIME="adjust_contact_contact_data_time";
    private String TAG = "ActomaContact NetChangeUpdateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            if(PushReceiver.validateConfiguration()) {
                if (ContactModuleService.isNetConnect(context)){
                    LogUtil.getUtils().e(TAG+"ContactUpdate 网络切换 维护数据启动");
                    long now = System.currentTimeMillis();
                    String last_update_value = PreferencesServer.getWrapper(context).gPrefStringValue(ADJUST_CONTACT_DATA_TIME+ ContactUtils.getCurrentAccount());
                    if (ObjectUtil.stringIsEmpty(last_update_value)){
                        PreferencesServer.getWrapper(context).setPreferenceStringValue
                                (ADJUST_CONTACT_DATA_TIME+ ContactUtils.getCurrentAccount(),String.valueOf(now));
                        new ContactModuleProxy().initContactsModule();
                    }else{
                        long time = now - Long.parseLong(last_update_value);
                        boolean bool = time > NET_CHANGE_UPDATE_NUMBER*60*60*1000;
                        if (bool){
                            new ContactModuleProxy().initContactsModule();
                            PreferencesServer.getWrapper(context).setPreferenceStringValue
                                    (ADJUST_CONTACT_DATA_TIME+ ContactUtils.getCurrentAccount(),String.valueOf(now));
                        }
                    }
                }

            }
        }

    }
}
