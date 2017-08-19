/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 * <p/>
 * CSipSimple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * If you own a pjsip commercial license you can also redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License
 * as an android library.
 * <p/>
 * CSipSimple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csipsimple.service;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.utils.CallHandlerPlugin;
import com.csipsimple.utils.ExtraPlugins;
import com.csipsimple.utils.PhoneCapabilityTester;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.RewriterPlugin;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.dependence.uitls.LogUtil;

import webrelay.VOIPManager;

public class DeviceStateReceiver extends BroadcastReceiver {

     //private static final String ACTION_DATA_STATE_CHANGED = "android.intent.action.ANY_DATA_STATE";
     private static final String THIS_FILE = DeviceStateReceiver.class.getName();
     private static final String TAG = THIS_FILE;
     public static final String APPLY_NIGHTLY_UPLOAD = "com.csipsimple.action.APPLY_NIGHTLY";

     @Override
     public void onReceive(Context context, Intent intent) {
          if(intent.getAction() == null){
              return;
          }
          switch (intent.getAction()) {

               case SipManager.ACTION_SIP_ACTOMA_CALLING:
                    makeCall(context, intent);
                    break;

               case ConnectivityManager.CONNECTIVITY_ACTION:
                    //开机不启动sip service
//               case Intent.ACTION_BOOT_COMPLETED:
                    restartService(context);
                    break;

               case SipManager.INTENT_SIP_ACCOUNT_ACTIVATE:
                    afterActive(context, intent);
                    break;


               case Intent.ACTION_PACKAGE_ADDED:
               case Intent.ACTION_PACKAGE_REMOVED:
                    afterPackageChange();
                    break;

               default:

                    break;


          }


     }

     private void makeCall(Context context, Intent intent) {
          if (context == null) {
               return;
          }
          String num = intent.getStringExtra(SipManager.CALL_NUM);
          AccountBean accountBean = AccountServer.getAccount();
          String accName = accountBean.getAccount();
          VOIPManager.getInstance().makeCall(num, accName);
     }

     private void restartService(Context context) {
          PreferencesProviderWrapper prefWrapper = new PreferencesProviderWrapper(context);
          boolean connected = prefWrapper.isValidConnectionForIncoming();


          if (connected) {

               ConnectivityManager cm =
                       (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
               NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
               String networkType = null;
               if(activeNetwork != null)
                    networkType = activeNetwork.getTypeName();
               VOIPManager.getInstance().onNetworkStateChanged(connected, networkType);

               if (android.os.Build.MODEL.equalsIgnoreCase("HUAWEI eH880")) { // Mate8上需要发送此广播！！！
                    Intent intent2 = new Intent(SipManager.ACTION_NETWORK_CONNECT);
                    intent2.putExtra(SipManager.NETWORK_TYPE, networkType);
                    context.sendBroadcast(intent2, SipManager.PERMISSION_USE_SIP);
               }

               LogUtil.getUtils(THIS_FILE).d("xjq device state reciever network CONNECTED!");
//               Log.d(THIS_FILE, "Try to start service if not already started");
//               Intent sip_service_intent = new Intent(context, SipService.class);
//               context.startService(sip_service_intent);
          } else {
               LogUtil.getUtils(THIS_FILE).d("device state reciever network DISCONNECTED!");
          }
     }

     private void afterPackageChange() {
          CallHandlerPlugin.clearAvailableCallHandlers();
          RewriterPlugin.clearAvailableRewriters();
          ExtraPlugins.clearDynPlugins();
          PhoneCapabilityTester.deinit();
     }

     private void afterActive(Context context, Intent intent) {
          PreferencesProviderWrapper prefWrapper = new PreferencesProviderWrapper(context);
          context.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);

          long accId;
          accId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);

          if (accId == SipProfile.INVALID_ID) {
               // allow remote side to send us integers.
               // previous call will warn, but that's fine, no worries
               accId = intent.getIntExtra(SipProfile.FIELD_ID, (int) SipProfile.INVALID_ID);
          }

          if (accId != SipProfile.INVALID_ID) {
               boolean active = intent.getBooleanExtra(SipProfile.FIELD_ACTIVE, true);
               ContentValues cv = new ContentValues();
               cv.put(SipProfile.FIELD_ACTIVE, active);

               //20170224-mengbo : 动态获取URI
               //int done = context.getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, accId)
               //        , cv, null, null);
               int done = context.getContentResolver().update(ContentUris.withAppendedId(SipProfile.getBaseAccountIdUri(context), accId)
                       , cv, null, null);
               if (done > 0) {
                    if (prefWrapper.isValidConnectionForIncoming()) {
                         Intent sipServiceIntent = new Intent(context, SipService.class);
                         context.startService(sipServiceIntent);
                    }
               }
          }
     }

}