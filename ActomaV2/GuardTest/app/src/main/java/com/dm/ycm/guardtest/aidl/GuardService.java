package com.dm.ycm.guardtest.aidl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycm on 2017/4/18.
 *
 */

public class GuardService extends Service {
    private final String tag = "aidl";
    private final String MQTTS_ACTION = "com.xdja.MqttService";
    private final String AIDLS_ACTION = "com.xdja.service.aidl";
    EditText editText;
    private MyBinder mBinder;
    private String pg = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        this.mBinder = new MyBinder();
        Log.d("dddd", "onCreate");
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        reStartNPCS();
        return super.onUnbind(intent);
    }

    private void reStartNPCS() {
        Intent in = new Intent();
        in.setAction("com.xdja.service.aidl");
        List<Intent> intents = getExplicitIntent(this, in);
        List<Integer> userId = new ArrayList<>();
        if (intents == null || intents.isEmpty()) {
            Log.d(tag, "系统中无NPC服务能够启动");
            return;
        }

        if (intents.size() == 1) {
            Log.d(tag, "系统中只有1个NPC服务能够启动");
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction("com.xdja.MqttService");
            startService(intent);
        } else {
            Log.d(tag, "系统中有多个NPC服务，启动一个服务");
            for (int i = 0; i < intents.size(); i++) {
                if (!this.pg.equals(intents.get(i).getComponent().getPackageName())) {
                    userId.add(getUserID(intents.get(i).getComponent().getPackageName()));
                } else {
                    intents.remove(i);
                }
            }
            int id = 0;
            int num = 0;
            for (int i = 0; i < userId.size(); i++) {
                if (id < userId.get(i)) {
                    id = userId.get(i);
                    num = i;
                }

                Intent intent2 = new Intent();
                intent2.setAction("com.xdja.MqttService");
                intent2.setPackage(((Intent)intents.get(num)).getComponent().getPackageName());
                startService(intent2);
            }
        }
        stopSelf();
    }

    public int getUserID(String packageName)
    {
        int userID = -1;
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            userID = pi.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return userID;
    }

    private static List<Intent> getExplicitIntent(Context context, Intent implicitIntent) {
        List<Intent> intents = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfos == null) {
            return null;
        }
        for (ResolveInfo  resolveInfo : resolveInfos) {
            String packageName = resolveInfo.serviceInfo.packageName;
            String className = resolveInfo.serviceInfo.name;
            ComponentName component = new ComponentName(packageName, className);
            Intent explicitIntent = new Intent(implicitIntent);
            explicitIntent.setComponent(component);
            intents.add(explicitIntent);
        }
        return intents;
    }

    public class MyBinder extends IMyService.Stub {

        @Override
        public int sendMSG(String topic, String message) throws RemoteException {
            return 0;
        }

        @Override
        public void isRunningNPCPackage(String packageName) throws RemoteException {

        }
    }
}
