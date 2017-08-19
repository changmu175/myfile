package com.xdja.imsdk;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.ImMsgManager;
import com.xdja.imsdk.manager.ImSdkManager;
import com.xdja.imsdk.model.InitParam;
import com.xdja.imsdk.receiver.NetworkStatusReceiver;
import com.xdja.pushsdk.npc.service.MqttServiceConstants;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：ImSdk服务        <br>
 * 创建时间：2016/11/16 15:05  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImSdkService extends Service {
    private Thread initThread;
    private NetworkStatusReceiver networkStatusReceiver;
    private AtomicBoolean initIsDone;
    private static InitParam initParam;
    private String cardId;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.getLogger().i("onCreate ...");

        initIsDone = new AtomicBoolean(false);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (Constant.IM_ACTION_INIT.equals(intent.getAction())) {
            initParam = intent.getParcelableExtra(Constant.IM_INIT_PARAM);
            if (initParam != null) {
                this.cardId = initParam.getTfcardId();
                startInitImSdk();
                return START_STICKY;
            }
        }

        if (getInitState() && Constant.IM_ACTION_STATE.equals(intent.getAction())) {
            String state = intent.getStringExtra(Constant.IM_STATE_PARAM);
            ImMsgManager.getInstance().connectPushResult(state);
            return START_STICKY;
        }

        if (getInitState() && Constant.IM_ACTION_TOPIC.equals(intent.getAction())) {
            String topic = intent.getStringExtra(Constant.IM_TOPIC_PARAM);
            if (!getPushTopic().equals(topic)) {
                return START_STICKY;
            }
            ImMsgManager.getInstance().handlePushNotice();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.getLogger().i("onDestroy " + getInitState() + "...");

        this.unregisterReceiver(networkStatusReceiver);
        stopInitThread();
        setInitIsDone(false);
        ImSdkManager.getInstance().releaseAll();
    }

    private void startInitImSdk() {
        if (initParam != null) {
            this.cardId = initParam.getTfcardId();

            try {
                if (initThread == null) {
                    initThread = new Thread(new InitSdkRunnable());
                    initThread.setName("InitThread");
                    initThread.start();
                } else if (!initThread.isAlive()) {
                    initThread.start();
                }
            } catch (Exception e) {
                Logger.getLogger().d("Im init exception...");
                initThread = null;
            }

        }
    }

    /**
     * 注册广播接收器监听网络变化
     */
    private void registerBroadcastReceiver(){
        if (networkStatusReceiver != null) {
            return;
        }

        networkStatusReceiver = new NetworkStatusReceiver();
        IntentFilter filter = new IntentFilter(MqttServiceConstants.PUSH_STATE);
        this.registerReceiver(networkStatusReceiver, filter);
    }

    /**
     * 获取订阅的推送主题
     * @return 主题
     */
    private String getPushTopic() {
        return Constant.IM_ACCOUNT_PREFIX + cardId + Constant.IM_ACCOUNT_SUFFIX;
    }

    /**
     * 获取当前初始化状态
     * @return 返回结果
     */
    private boolean getInitState() {
        return initIsDone != null && initIsDone.get();
    }

    private void setInitIsDone(boolean done) {
        if (initIsDone != null) {
            initIsDone.set(done);
        }
    }

    private void stopInitThread() {
        if (initThread != null && initThread.isAlive() && !getInitState()) {
            try {
                Thread.sleep(300);// TODO: 2016/9/14 防止刚初始化即被销毁，则数据库数据库未创建的异常
            } catch (InterruptedException e) {
                Logger.getLogger().e("Im init fail");
                initThread = null;
            }
            try {
                initThread.interrupt();// TODO: 2016/9/14 线程未退出
            } catch (Exception e) {
                Logger.getLogger().e("Im init fail");
                initThread = null;
            }
        }
        initThread = null;
    }

    protected class InitSdkRunnable implements Runnable {
        @Override
        public void run() {
            try {
                ImSdkManager.getInstance().init(ImSdkService.this, initParam);
            } catch (Exception e) {
                Logger.getLogger().d("Im init fail");
            } finally {
                setInitIsDone(true);
            }
        }
    }
}
