package com.securevoip.voip;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zjc on 2015/8/27.
 */
public class NotificationCancelService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
