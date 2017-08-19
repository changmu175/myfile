package com.xdja.imp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xdja.comm.uitl.CommonUtils;
import com.xdja.frame.data.cache.SharedPreferencesUtil;
import com.xdja.imp.ISimcUiGuardAidlInterface;

/**
 * Created by ALH on 2017/3/13.
 */

public class SimcUiGuardService extends Service {
    private static final String TAG = "SimcUiGuardService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "H>>> SimcUiGuardService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "H>>> SimcUiGuardService onBind");
        return new GuardBinder();
    }

    public class GuardBinder extends ISimcUiGuardAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString)
                throws RemoteException {
        }
    }

    private final void startSimcUiService() {
        if (!CommonUtils.isServiceRunning(getBaseContext(), SimcUiService.class.getName()) && !SharedPreferencesUtil
                .getNormalStopService(getBaseContext())) {
            Log.v(TAG, "H>>>> SimcUiGuardService startSimcUiService");
            Intent i = new Intent(getBaseContext(), SimcUiService.class);
            startService(i);
            stopSelf();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "H>>> SimcUiGuardService onUnbind");
        startSimcUiService();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "H>>> SimcUiGuardService onDestroy");
        super.onDestroy();
    }
}
