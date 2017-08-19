package com.xdja.imsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdja.imsdk.ImSdkService;
import com.xdja.imsdk.constant.internal.Constant;

/**
 * 项目名称：ImSdk                    <br>
 * 类描述  ：Push消息通知广播接收器     <br>
 * 创建时间：2016/11/16 15:09        <br>
 * 修改记录：                         <br>
 *
 * @author liming@xdja.com          <br>
 * @version V1.1.7                  <br>
 */
public class PushNoticeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        if (Constant.PUSH_ACTION.equals(intent.getAction())) {
            String topic = intent.getStringExtra(Constant.PUSH_TOPIC);
            Intent serviceIntent = new Intent(context, ImSdkService.class);
            serviceIntent.putExtra(Constant.IM_TOPIC_PARAM, topic);
            serviceIntent.setAction(Constant.IM_ACTION_TOPIC);
            context.startService(serviceIntent);
        }
    }
}
