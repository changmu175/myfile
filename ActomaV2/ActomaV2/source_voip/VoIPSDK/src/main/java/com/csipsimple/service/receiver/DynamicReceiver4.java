/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csipsimple.service.receiver;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.service.SipNotifications;
import com.csipsimple.service.SipService;
import com.csipsimple.service.SipService.SameThreadException;
import com.csipsimple.service.SipService.SipRunnable;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import webrelay.VOIPManager;
import webrelay.bean.State;

@SuppressLint("SynchronizeOnNonFinalField")
public class DynamicReceiver4 extends BroadcastReceiver {

    private static final String THIS_FILE = "DynamicReceiver";

    /**
     * 清除所有未接来电的广播
     */
    public static String CANCEL_MISSED_CALL_NOTIFICATION = "android.custom.notification_cancel";

    /**
     * 清除与某个人的未接来电广播
     */
    public static String CANCEL_ONE_MISSED_CALL_NOTIFICATION = "android.custom.clear_peer_notification";

    // Comes from android.net.vpn.VpnManager.java
    // Action for broadcasting a connectivity state.
    public static final String ACTION_VPN_CONNECTIVITY = "vpn.connectivity";
    /** Key to the connectivity state of a connectivity broadcast event. */
    public static final String BROADCAST_CONNECTION_STATE = "connection_state";
    
    private SipService service;
    
    
    // Store current state
    private String mNetworkType;
    private boolean mConnected = false;
    private String mRoutes = "";
    
    private boolean hasStartedWifi = false;


    private Timer pollingTimer;

    
    /**
     * Check if the intent received is a sticky broadcast one 
     * A compat way
     * @param it intent received
     * @return true if it's an initial sticky broadcast
     */
    public boolean compatIsInitialStickyBroadcast(Intent it) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(it.getAction())) {
            if(!hasStartedWifi) {
                hasStartedWifi = true;
                return true;
            }
        }
        return false;
    }
    
    public DynamicReceiver4(SipService aService) {
        service = aService;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Run the handler in SipServiceExecutor to be protected by wake lock
        service.getExecutor().execute(new SipRunnable() {
            public void doRun() throws SameThreadException {
                onReceiveInternal(context, intent, compatIsInitialStickyBroadcast(intent));
            }
        });
    }
    
    

    /**
     * Internal receiver that will run on sip executor thread
     * @param context Application context
     * @param intent Intent received
     * @throws SameThreadException
     */
    private void onReceiveInternal(Context context, Intent intent, boolean isSticky) throws SameThreadException {
        String action = intent.getAction();
        LogUtil.getUtils(THIS_FILE).d("Internal receive " + action);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            onConnectivityChanged(activeNetwork, isSticky);
        } else if (action.equals(SipManager.ACTION_SIP_ACCOUNT_CHANGED)) {
            final long accountId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);
            // Should that be threaded?
            if (accountId != SipProfile.INVALID_ID) {
                SipProfile account = service.getAccount(accountId);
                if(account.id == SipProfile.INVALID_ID) {
                    account = service.getDefaultAccount();
                }

                if (account != null) {
                    LogUtil.getUtils(THIS_FILE).d("Enqueue set account registration");
                    service.setAccountRegistration(account, account.active ? 1 : 0, true);
                }
            }
        } else if (action.equals(SipManager.ACTION_SIP_READD_DEFAULT_ACCOUNT)) { // 添加默认账户 xjq 2015-09-12

                final SipProfile account = service.getDefaultAccount();
                if (account != null) {
                    LogUtil.getUtils(THIS_FILE).d("xjq READD DEFAULT ACCOUNT");
                    service.setAccountRegistration(account, account.active ? 1 : 0, true);
                } else {
                    LogUtil.getUtils(THIS_FILE).d(" xjq ERROR!! Default account NULL");
                }

        } else if (action.equals(SipManager.ACTION_SIP_ACCOUNT_DELETED)){
            final long accountId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);
            if(accountId != SipProfile.INVALID_ID) {
                final SipProfile fakeProfile = new SipProfile();
                fakeProfile.id = accountId;
                service.setAccountRegistration(fakeProfile, 0, true);
            }
        } else if (action.equals(SipManager.ACTION_SIP_CAN_BE_STOPPED)) {
            service.cleanStop();
        } else if (action.equals(SipManager.ACTION_SIP_REQUEST_RESTART)){
            service.restartSipStack();
        } else if(action.equals(ACTION_VPN_CONNECTIVITY)) {
            onConnectivityChanged(null, isSticky);
        } else if(action.equals(Intent.ACTION_SCREEN_OFF)) { // 屏幕锁屏
            service.stopRing();
        } else if(action.equals(Intent.ACTION_SCREEN_ON)) { // 屏幕点亮
            // reserved do nothing now
        } else if (action.equals(CANCEL_MISSED_CALL_NOTIFICATION)) {
            SipNotifications nm = new SipNotifications();
            nm.cancelAllMissedCall();
            //跳转到通话记录的fragment
            NotificationIntent notificationIntent = new NotificationIntent("com.xdja.actoma.presenter.activity.MainFramePresenter");
            Bundle bundle = new Bundle();
            bundle.putInt("pageIndex", TabTipsEvent.INDEX_VOIP);
            notificationIntent.putExtras(bundle);
            context.sendBroadcast(notificationIntent);
        } else if (action.equals(CANCEL_ONE_MISSED_CALL_NOTIFICATION)) {
            if (!TextUtils.isEmpty(intent.getStringExtra(CANCEL_ONE_MISSED_CALL_NOTIFICATION))) {
                String account = intent.getStringExtra(CANCEL_ONE_MISSED_CALL_NOTIFICATION);
                //清除该条的未接来电
                NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(account, NotifiParamUtil.MISSED_CALL_NOTIF_ID);
            }
        } else if(action.equals(SipManager.ACTION_NETWORK_CONNECT)) { // xjq
                String networkType = intent.getStringExtra(SipManager.NETWORK_TYPE);
                onNetworkConnect(networkType);
        } else if(action.equals(SipManager.ACTION_STOP_SIPSERVICE)) { // xjq

            Log.d("voip_disconnect", "DynamicReceiver4 onReceiveInternal action : ACTION_STOP_SIPSERVICE");

            Log.d("voip_disconnect", "***** Stop sip service begin time:" + System.currentTimeMillis());


            if (service.stopSipStack()) {

                Log.d("voip_disconnect", "DynamicReceiver4 onReceiveInternal stop sipstack success, start to stop service self");

                service.stopSelf();
            }

            Log.d("voip_disconnect", "***** Stop sip service end time:" + System.currentTimeMillis());

        }
    }

    @SuppressLint("StringBufferReplaceableByStringBuilder")
    private static final String PROC_NET_ROUTE = "/proc/net/route";
    private String dumpRoutes() {
        String routes = "";
        FileReader fr = null;
        try {
            fr = new FileReader(PROC_NET_ROUTE);
            if(fr != null) {
                StringBuffer contentBuf = new StringBuffer();
                BufferedReader buf = new BufferedReader(fr);
                String line;
                while ((line = buf.readLine()) != null) {
                    contentBuf.append(line+"\n");
                }
                routes = contentBuf.toString();
                buf.close();
            }
        } catch (FileNotFoundException e) {
            LogUtil.getUtils(THIS_FILE).e("No route file found routes");
        } catch (IOException e) {
            LogUtil.getUtils(THIS_FILE).e("Unable to read route file");
        }finally {
            try {
            	if(fr!=null){
            		fr.close();
            	}
            } catch (IOException e) {
                LogUtil.getUtils(THIS_FILE).e("Unable to close route file");
            }
        }
        
        // Clean routes that point unique host 
        // this aims to workaround the fact android 4.x wakeup 3G layer when position is retrieve to resolve over 3g position
        String finalRoutes = routes;
        if(!TextUtils.isEmpty(routes)) {
            String[] items = routes.split("\n");
            List<String> finalItems = new ArrayList<>();
            int line = 0;
            for(String item : items) {
                boolean addItem = true;
                if(line > 0){
                    String[] ent = item.split("\t");
                    if(ent.length > 8) {
                        String maskStr = ent[7];
                        if(maskStr.matches("^[0-9A-F]{8}$")) {
                            int lastMaskPart = Integer.parseInt(maskStr.substring(0, 2), 16);
                            if(lastMaskPart > 192) {
                                // if more than 255.255.255.192 : ignore this line
                                addItem = false;
                            }
                        }else {
                            LogUtil.getUtils(THIS_FILE).w("The route mask does not looks like a mask" + maskStr);
                        }
                    }
                }
                
                if(addItem) {
                    finalItems.add(item);
                }
                line ++;
            }
            finalRoutes = TextUtils.join("\n", finalItems); 
        }
        
        return finalRoutes;
    }

    
    /**
     * Treat the fact that the connectivity has changed
     * @param info Network info
     * @param isSticky
     * @throws SameThreadException
     */
    private void onConnectivityChanged(NetworkInfo info, boolean isSticky) throws SameThreadException {
        // We only care about the default network, and getActiveNetworkInfo()
        // is the only way to distinguish them. However, as broadcasts are
        // delivered asynchronously, we might miss DISCONNECTED events from
        // getActiveNetworkInfo(), which is critical to our SIP stack. To
        // solve this, if it is a DISCONNECTED event to our current network,
        // respect it. Otherwise get a new one from getActiveNetworkInfo().
        if (info == null || info.isConnected() ||
                !info.getTypeName().equals(mNetworkType)) {
            ConnectivityManager cm = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = cm.getActiveNetworkInfo();
        }

        boolean connected = (info != null && info.isConnected() && service.isConnectivityValid());
        String networkType = connected ? info.getTypeName() : "null";
        String currentRoutes = dumpRoutes();
        String oldRoutes;
        synchronized (mRoutes) {
            oldRoutes = mRoutes;
        }

        // Ignore the event if the current active network is not changed.
//        if (connected == mConnected && networkType.equals(mNetworkType) && currentRoutes.equals(oldRoutes)) {
//            return;
//        }


        if (!networkType.equals(mNetworkType)) {
            LogUtil.getUtils(THIS_FILE).d("onConnectivityChanged(): " + mNetworkType +
                    " -> " + networkType);
        } else {
            LogUtil.getUtils(THIS_FILE).d("Route changed : " + mRoutes + " -> " + currentRoutes);
        }
        // Now process the event
        synchronized (mRoutes) {
            mRoutes = currentRoutes;
        }
        mConnected = connected;
        mNetworkType = networkType;

        // 网络中断恢复重新启动协议栈。Add by xjq, 2015-08-22
        if (!isSticky) {
            if (connected) {
                service.restartSipStack();
            } else {
                LogUtil.getUtils(THIS_FILE).d("We are not connected, stop");

                Log.d("voip_disconnect", "DynamicReceiver4 onConnectivityChanged network too poor to stop sipservice");

                if (service.stopSipStack()) {
                    service.stopSelf();
                }
            }
        }


        // 告知VoIP Manager网络状态发生改变 xjq
        if(!connected) { // 只告知网络断开的事件
            VOIPManager.getInstance().onNetworkStateChanged(connected, networkType);
        }

        // mengbo@xdja.com 2016-8-11 start 加入isSticky判断，呼叫时不应执行，断线重连执行
        if(mConnected && !isSticky) {
            /** 20161103-mengbo-start: 加入INCOMING判断，来电断线重连，不需要注册 **/
            if(VOIPManager.getInstance().hasActiveCall()
                    && VOIPManager.getInstance().getCurSession().getState() != State.INCOMING) {
                LogUtil.getUtils(THIS_FILE).e("DynamicReceiver4 onConnectivityChanged connected so custAccountStatus to reconnect");
                service.setCustAccountStatus(1);
            }
            /** 20161103-mengbo-end **/
        }
        // mengbo@xdja.com 2016-8-11 end

        // 设置回话重连标识。xjq 2015-09-03
        SipCallSession callInfo = null;
        if(service != null && service.getUAStateReceiver() != null) {
            callInfo = service.getUAStateReceiver().getCallConfirmed();
        }
        if (callInfo != null) {
            if (mConnected) {
                callInfo.setReconnect(true);// set reconnect flag
            }

        }


    }


    /**
     * 网络恢复 xjq
     * @throws SameThreadException
     */
    public void onNetworkConnect(String networkType) throws SameThreadException {
        // 告知VoIP Manager网络状态发生改变 xjq
        VOIPManager.getInstance().onNetworkStateChanged(true, networkType);

        // 设置回话重连标识。xjq 2015-09-03
        SipCallSession callInfo = null;
        if(service != null && service.getUAStateReceiver() != null) {
            callInfo = service.getUAStateReceiver().getCallConfirmed();
        }
        if (callInfo != null) {
            LogUtil.getUtils(THIS_FILE).e("DynamicReceiver4 onNetworkConnect so custAccountStatus to reconnect");

            /** 20160917-mengbo-start: 加入活跃会话判断，使用流量的时候，当普通电话来电，会断开流量，挂断时，不应执行断线重连**/
            if(VOIPManager.getInstance().hasActiveCall()) {
                service.setCustAccountStatus(1);
                callInfo.setReconnect(true);// set reconnect flag
            }
            /** 20160917-mengbo-end **/
        }
    }

    /**
     * 字符串按行解析到list
     * @param s  要解析的字符串
     * @param lines 存储解析结果
     * @return void
     */  
    private void stringToLines(String s, List<String> lines){
    	BufferedReader bf = new BufferedReader(new StringReader(s));
    	String tmp = null;
    	try {
			while((tmp = bf.readLine()) != null) {
				lines.add(tmp);
			}
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //end add by xjq 20140808 增加比较两个路由路径是否相同的函数
    
    public void startMonitoring() {
        int pollingIntervalMin = service.getPrefs().getPreferenceIntegerValue(SipConfigManager.NETWORK_ROUTES_POLLING);

        LogUtil.getUtils(THIS_FILE).d("Start monitoring of route file ? " + pollingIntervalMin);
        if(pollingIntervalMin > 0) {
            pollingTimer = new Timer("RouteChangeMonitor", true);
            pollingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    String currentRoutes = dumpRoutes();
                    String oldRoutes;
                    synchronized (mRoutes) {
                        oldRoutes = mRoutes;
                    }
                    if(!currentRoutes.equalsIgnoreCase(oldRoutes)) {
                        LogUtil.getUtils(THIS_FILE).d("Route changed");
                        // Run the handler in SipServiceExecutor to be protected by wake lock
                        service.getExecutor().execute(new SipRunnable()  {
                            public void doRun() throws SameThreadException {
                                onConnectivityChanged(null, false);
                            }
                        });
                    }
                }
            }, new Date(), pollingIntervalMin * 60 * 1000);
        }
    }
    
    public void stopMonitoring() {
        if(pollingTimer != null) {
            pollingTimer.cancel();
            pollingTimer.purge();
            pollingTimer = null;
        }
    }
}
