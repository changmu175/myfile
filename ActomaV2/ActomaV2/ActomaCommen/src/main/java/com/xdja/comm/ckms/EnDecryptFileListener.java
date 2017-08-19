package com.xdja.comm.ckms;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xdja.sks.IEncDecListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xienana on 2016/8/8.
 */
public class EnDecryptFileListener implements IEncDecListener {
    private static String TAG = "anTongCkms EnDecryptFileListener";

    private CountDownLatch latch;
    private int retCode;

    public EnDecryptFileListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onOperStart() throws RemoteException {

    }

    @Override
    public void onOperProgress(long current, long total) throws RemoteException {

    }

    @Override
    public void onOperComplete(int resCode) throws RemoteException {
        setRetCode(resCode);
        Log.i(TAG,"onOperComplete retCode = "+retCode);
        latch.countDown();
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }
}
