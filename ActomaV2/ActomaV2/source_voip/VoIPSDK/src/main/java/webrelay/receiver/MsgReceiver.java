package webrelay.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xdja.dependence.uitls.LogUtil;

import webrelay.VOIPPush;

/**
 * Created by admin on 16/3/17.
 */
public class MsgReceiver extends BroadcastReceiver  {

    final private static String TAG = "MsgReceiver";

    public static final String TEST_ACTION = "com.xdja.apushsdk";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case TEST_ACTION:
                String topic = intent.getStringExtra("topic");
                String msg = intent.getStringExtra("c");
                VOIPPush.getInstance().onMsgReceived(context, topic, msg);
                LogUtil.getUtils(TAG).d("MsgReceiver--topic=" + topic + " msg=" + msg);
                break;
            default :
                break;
        }
    }


}
