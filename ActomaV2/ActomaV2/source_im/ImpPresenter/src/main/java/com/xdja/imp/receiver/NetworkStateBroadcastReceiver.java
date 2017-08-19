package com.xdja.imp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xdja.comm.event.BusProvider;

/**
 * Created by 4King on 2016/10/10.
 */

public class NetworkStateBroadcastReceiver extends BroadcastReceiver{
    //正常状态
    public static final int NORMAL = 0;
    //连接不到服务器
    public static final int NO_SERVER = 1;
    //网络不可用
    public static final int NET_DISABLED =2;
    private static final String FLAG_KEY = "im.extra.network.state";
    private static int flag = 0;
    //是否是第一次提示网络状态不佳，第一次提示时不弹出Toast
    public static boolean isFirstChange = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        int lastFlag = flag;
        flag = intent.getIntExtra(FLAG_KEY , -1);
        isFirstChange = !(lastFlag != NORMAL && (flag == lastFlag));
        NetworkStateEvent event = new NetworkStateEvent();
        event.setState(flag);
        BusProvider.getMainProvider().post(event);
    }
    public static int getState(){
        return flag;
    }
}
