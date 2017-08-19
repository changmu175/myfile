package com.dm.ycm.guardtest;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.dm.ycm.guardtest.aidl.GuardService;
import com.dm.ycm.guardtest.aidl.IMyService;

import java.lang.ref.WeakReference;

/**
 * Created by ycm on 2017/4/18.
 */

public class MqttService extends Service{
    private IMyService iMyService;
    private MqttServiceHandler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new MqttServiceHandler(this);
        bindService(new Intent(MqttService.this, GuardService.class), serviceConnection, BIND_AUTO_CREATE);
        Toast.makeText(this, "MqttService is creat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "MqttService is start", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "MqttService is onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyService = IMyService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };


    private class MqttServiceHandler extends Handler {
        WeakReference<MqttService> mWeakReference;
        public MqttServiceHandler(MqttService weak) {
            if (weak != null) mWeakReference = new WeakReference<>(weak);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            intent.setClass(MqttService.this, GuardService.class);
            startService(intent);
        }
    }
}
