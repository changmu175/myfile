package com.securevoip.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.csipsimple.service.SipNotifications;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by zjc on 2015/8/27.
 */
public class ClearReceiver extends BroadcastReceiver {
     public static final String TAG = "ClearReceiver";
     public static final String ACTION_CANCEL_NOTIFICATION = "com.xdja.voip.cancel_missed_call_notification";
     public static final String FLAG_CANCEL_NOTIFICATION = "actomaAccount";

     @Override
    public void onReceive(Context context, Intent intent) {
          if (context != null && intent.getAction() != null) {
               switch (intent.getAction()) {
                    case ACTION_CANCEL_NOTIFICATION:
                         StringBuilder actomaAccount = new StringBuilder();
                         actomaAccount.append(intent.getStringExtra(FLAG_CANCEL_NOTIFICATION));
                         cancelMissedCallNotification(actomaAccount.toString());
                        LogUtil.getUtils().d(TAG + actomaAccount.append("：清除未接来电通知的账号").toString());
                         break;

                    default:

                         break;
               }
          }
    }

     private void cancelMissedCallNotification(String actomaAccount) {
          SipNotifications sn = new SipNotifications();
          sn.cancelMissedCall(actomaAccount);
     }

}
