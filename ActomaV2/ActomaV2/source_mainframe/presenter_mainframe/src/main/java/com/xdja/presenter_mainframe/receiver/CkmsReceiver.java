package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;


/**
 * Created by tangsha on 2016/8/5.
 */
public class CkmsReceiver extends BroadcastReceiver {
    private String TAG = "CkmsReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isExpired = intent.getBooleanExtra(CkmsGpEnDecryptManager.CKMS_REFRESH_EXTRA_EXPIRED,false);
        Log.d(TAG,"onReceive "+isExpired);
        CkmsRefreshTask task = CkmsRefreshTask.getInstance(context,isExpired);
        task.execute();
    }

   }
