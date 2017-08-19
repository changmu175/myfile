package webrelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.text.TextUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.pushsdk.PushClient;
import com.xdja.pushsdk.npc.service.MqttServiceConstants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import util.JsonUtil;
import util.SPUtil;
import webrelay.bean.CallSession;
import webrelay.bean.DescBean;
import webrelay.bean.FailedBean;
import webrelay.bean.MsgType;
import webrelay.bean.SuccessBean;
import webrelay.bean.TicketError;
import webrelay.inter.PushListener;

/**
 * Created by guoyaxin on 2015/12/11.
 * VOIP推送消息接口
 */
public class VOIPPush {

    private static final String TAG = "VOIPPush";

    private static VOIPPush voipPush = new VOIPPush();

    public static VOIPPush getInstance() {
        return voipPush;
    }

    private MsgComingHandler msgComingHandler;

    public VOIPPush init(Context cxt, String tfCardId) {
        String cardStrLower = tfCardId.toLowerCase();
        PushClient.init(cxt, "xdja/d/" + cardStrLower);
        String voipTopic = "xdja/d/" + cardStrLower + "/voipserver";
        // 存储voip topic xjq
        SPUtil.saveVoipTopic(cxt, voipTopic);
        PushClient.subTopic(cxt, voipTopic, 0);
        msgComingHandler = new MsgComingHandler(this);
        return voipPush;
    }

    //mengbo@xdja.com 2016-08-05 add. fix bug 1275
    public void realease(Context cxt) {
        String voipTopic = SPUtil.getVoipTopic(cxt);
        PushClient.unsubscribe(cxt, voipTopic, null);
    }
    //mengbo@xdja.com 2016-08-05 add. fix bug 1275

    static class MsgComingHandler extends WeakReferenceHandler<VOIPPush> {

        public MsgComingHandler(VOIPPush reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(VOIPPush reference, Message msg) {
            reference.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        String info = (String) msg.obj;
        LogUtil.getUtils(TAG).d("VOIPPush:" + info + "");

        //2016-08-18 mengbo start modify. 推送内容发送异常不做处理
        CallSession callSession;
        try {
            callSession = GsonUtil.getInstance().jsonToObject(info, CallSession.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //2016-08-18 mengbo end

        if (callSession != null) {
            String msgType = callSession.getType();
            if (MsgType.MSG_MAKE_CALL.mType.equals(msgType)) {
                if (pushListener == null)
                    return;
                pushListener.onCallComing(callSession);
            } else if (MsgType.MSG_CALLER_REJECT.mType.equals(msgType)) {
                if (pushListener == null)
                    return;
                pushListener.callerReject(callSession);
            } else if (MsgType.MSG_CALLED_SUBSCRIBER_REJECT.mType.equals(msgType)) {
                if (pushListener == null)
                    return;
                pushListener.calleeReject(callSession);
            }
        }
    }

    private PushListener pushListener;

    public void addPushListener(PushListener pushListener) {
        this.pushListener = pushListener;
    }

    /**
     * 消息过来时回调
     *
     * @param topic
     * @param info
     */

    public void onMsgReceived(Context cxt, String topic, String info) {
        String voipTopic = SPUtil.getVoipTopic(cxt);
        if (!TextUtils.isEmpty(voipTopic) && voipTopic.equals(topic)) {
            //wxf@xdja.com 2016-08-02 add. fix bug 2302 . review by mengbo. Start
            if (msgComingHandler != null) {
                Message msg = msgComingHandler.obtainMessage();
                msg.obj = info;
                msgComingHandler.sendMessage(msg);
            } else {
                LogUtil.getUtils(TAG).d("onMsgReceived, but msgComingHandler is null");
            }
            //wxf@xdja.com 2016-08-02 add. fix bug 2302 . review by mengbo. End
        }
    }

    /**
     * 接收到push长链接成功的广播。
     * 10000：push长链接成功成功
     * 10001，10003：push断开
     * 10002：暂未使用
     */

    //推送上线成功
    private String STATUS_SUCCESS = "10000";

    //推送上线失败
    private String STATUS_FAILED_1 = "10001";
    private String STATUS_FAILED_2 = "10003";

    //该状态码未使用
    private String STATUS_UNUSED = "10003";


    /**
     * 当前状态推送在线状态 0 在线, -1 离线
     */
    private int cur_state = 0; // 默认在线

    /**
     * 获取当前推送在线状态
     *
     * @return
     */
    public int getCurPushState() {
        return cur_state;
    }


    /**
     * 推送sdk是否上线监听
     */
    public class PushStateReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
//               String state = intent.getStringExtra(MqttServiceConstants.PUSH_CODE);
//
//
//               if (state.equals(STATUS_SUCCESS)) {
//                    cur_state = 0;
//                    VOIPManager.getInstance().initCall();
//                    LogU.LogVOIPPushOnLine("PushStateReceiver", "  onLine ;; cur_state=" + cur_state);
//               } else if ((cur_state == 0) && (state.equals(STATUS_FAILED_1) || state.equals(STATUS_FAILED_2))) {
//                    cur_state = -1;
//                    LogU.LogVOIPPushOnLine("PushStateReceiver", "  offLine ;; cur_state=" + cur_state);
//               }
//
        }
    }

    private PushStateReceiver pushStateReceiver;

    /**
     * 注册PushStateReceiver
     *
     * @param cxt
     */
    public void registerPushStateReceiver(Context cxt) {
        pushStateReceiver = new PushStateReceiver();
        IntentFilter filter = new IntentFilter(MqttServiceConstants.PUSH_STATE);
        cxt.registerReceiver(pushStateReceiver, filter);
    }

    /**
     * 取消注册
     *
     * @param cxt
     */
    public void unregisterPushStateReceiver(Context cxt) {
        if (pushStateReceiver == null)
            return;
        cxt.unregisterReceiver(pushStateReceiver);
    }

    public void getAs(final Context cxt, String src) {
        String voipIP = SPUtil.getVoIPLocalAddr(cxt);
        if (voipIP != null)
            return;
        Callback callback = new Callback() {
            /**
             * Called when the request could not be executed due to cancellation, a connectivity problem or
             * timeout. Because networks can fail during an exchange, it is possible that the remote server
             * accepted the request before the failure.
             *
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {

            }

            /**
             * Called when the HTTP response was successfully returned by the remote server. The callback may
             * proceed to read the response body with {@link Response#body}. The response is still live until
             * its response body is closed with {@code response.body().close()}. The recipient of the callback
             * may even consume the response body on another thread.
             * <p/>
             * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
             * not necessarily indicate application-layer success: {@code response} may still indicate an
             * unhappy HTTP response code like 404 or 500.
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                LogUtil.getUtils(TAG).d("initCall:" + result);

                JSONObject jsonObject = JsonUtil.getJsonObject(result);
                if (jsonObject == null)
                    return;
                if (jsonObject.has("error")) {
                    return;
                }
                SuccessBean successBean = JsonUtil.jsonToObject(result, SuccessBean.class);
                String desc = successBean.getResult().getDesc();
                DescBean descBean = JsonUtil.jsonToObject(desc, DescBean.class);
                String asServerAddr = descBean.getHost() + ":" + descBean.getPort();
                SPUtil.saveVoIPLocalAddr(cxt, asServerAddr);
            }
        };

        JsonCallback<SuccessBean, FailedBean, TicketError> jsonCallback = new JsonCallback<SuccessBean, FailedBean, TicketError>() {
            @Override
            public void onNetworkError(Request request, Exception e) {

            }

            @Override
            public void onNetworkError(Exception e) {

            }

            @Override
            public void onSuccess(SuccessBean successBean) {
                String desc = successBean.getResult().getDesc();
                DescBean descBean = JsonUtil.jsonToObject(desc, DescBean.class);
                String asServerAddr = descBean.getHost() + ":" + descBean.getPort();
                SPUtil.saveVoIPLocalAddr(cxt, asServerAddr);
            }

            @Override
            public void onFailure(FailedBean failedBean) {

            }

            @Override
            public void onTicketError(TicketError ticketError) {

            }
        };
        String ticket = VOIPManager.getInstance().getTicket();
        // 首先获取AS服务器地址，并发送register消息
        VOIPBase.getInstance().getAddress(cxt, src, ticket, jsonCallback);
    }

}
