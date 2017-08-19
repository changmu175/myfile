package com.xdja.imsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.imsdk.ImSdkService;
import com.xdja.imsdk.constant.internal.Constant;

/**
 * 项目名称：ImSdk                   <br>
 * 类描述  ：Push连接状态广播接收器    <br>
 * 创建时间：2016/11/16 15:08        <br>
 * 修改记录：                        <br>
 *
 * @author liming@xdja.com         <br>
 * @version V1.1.7                 <br>
 */
public class NetworkStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String state = intent.getStringExtra(Constant.PUSH_CODE);
        Intent serviceIntent = new Intent(context, ImSdkService.class);
        serviceIntent.putExtra(Constant.IM_STATE_PARAM, state);
        serviceIntent.setAction(Constant.IM_ACTION_STATE);
        context.startService(serviceIntent);
    }
}
