package com.xdja.presenter_mainframe;

import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import com.xdja.safeauth.log.Log;

/**
 * Created by ALH on 2017/2/21.
 * 该类用来管理WakeLock
 */

public final class WakeLockManager {
    private static final String TAG = "WakeLockManager";
    private static final int MSG_CANCAL_WAKELOCK = 0;
    private static final int DELAYED_TIME = 15000;
    //private static WakeLockManager sWakeLockManager;
    private PowerManager.WakeLock mWakeLock;
    private int mAcquireCount = 0;
    private boolean mIsRelease = false;
    //强制10秒后释放
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CANCAL_WAKELOCK: {
                    Log.v(TAG , "MSG_CANCAL_WAKELOCK : " + mIsRelease);
                    if (!mIsRelease) forceRelease();
                    break;
                }
            }
        }
    };

    private WakeLockManager(){

    }

    private final void initialize() {
        if (mWakeLock == null) {
            Log.v(TAG, "initialize Enter");
            PowerManager powerManager = (PowerManager) ActomaApplication.getInstance().getSystemService(Service.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            mWakeLock.setReferenceCounted(false);
            Log.v(TAG, "initialize End");
        }
    }

    private static class WakeLockManagerHolder{
        private static WakeLockManager sWakeLockManager = new WakeLockManager();
    }

    public static synchronized final WakeLockManager getInstance() {
        return WakeLockManagerHolder.sWakeLockManager;
//        if (sWakeLockManager == null){
//            synchronized (WakeLockManager.class){
//                WakeLockManager wakeLockManager = sWakeLockManager;
//                if (wakeLockManager == null){
//                    synchronized (WakeLockManager.class){
//                        wakeLockManager = new WakeLockManager();
//                    }
//                    sWakeLockManager = wakeLockManager;
//                }
//            }
//        }
//        return sWakeLockManager;
    }

    public final void acquire() {
        Log.e(TAG , "acquire Enter : " + mWakeLock);
        synchronized (WakeLockManager.class) {
            initialize();
            if (mWakeLock != null) {
                mHandler.removeMessages(MSG_CANCAL_WAKELOCK);
                mIsRelease = false;
                mAcquireCount++;
                if (!mWakeLock.isHeld()) mWakeLock.acquire();
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_CANCAL_WAKELOCK), DELAYED_TIME);
            }
        }
        Log.e(TAG , "acquire End : " + mAcquireCount + " , " + mIsRelease);
    }

    public final void release() {
        Log.v(TAG, "release Enter : " + mWakeLock + " , " + mIsRelease + " , " + mAcquireCount);
        synchronized (WakeLockManager.class) {
            if (mIsRelease || mWakeLock == null || mAcquireCount <= 0) return;
            if (--mAcquireCount <= 0) {
                mAcquireCount = 0;
                mWakeLock.release();
                mIsRelease = true;
            }
        }
        Log.v(TAG , "release End : " + mAcquireCount + " , " + mIsRelease);
    }

    public final void forceRelease() {
        Log.v(TAG , "forceRelease Enter : " + mWakeLock);
        synchronized (WakeLockManager.class) {
            if (mWakeLock != null) {
                mAcquireCount = 0;
                mWakeLock.release();
                mIsRelease = true;
            }
        }
        Log.v(TAG , "forceRelease End : " + mAcquireCount + " , " + mIsRelease);
    }

}
