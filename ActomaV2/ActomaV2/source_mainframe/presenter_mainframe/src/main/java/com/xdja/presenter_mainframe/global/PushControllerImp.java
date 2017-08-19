package com.xdja.presenter_mainframe.global;

import android.content.Context;
import android.text.TextUtils;

//import com.securevoipcommon.VoipFunction;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.pushsdk.PushClient;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.global</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/28</p>
 * <p>Time:10:45</p>
 */
public class PushControllerImp implements PushController {

    /**
     * 推送标识的前数据
     */
    private String PUSH_CLIENT;

    /**
     * 推送的ip
     */
    private String CLIENTIP;
    /**
     * 推送的端口
     */
    private String CLIENTPORT;
    /**
     * 框架主题
     */
    private String TOPIC_FRAME;

    /**
     * 升级主题
     */
    private String TOPIC_AUTOUPDATE;

    /**
     * 好友主题
     */
    private String TOPIC_FRIEND;

    /**
     * 群组主题
     */
    private String TOPIC_GROUP;

    /**
     * 账户主题
     */
    private String TOPIC_ACCOUNT;

    /**
     * 集团主题
     */
    private String TOPIC_DEPART;

    /**
     * 第三方加密主题
     */
    private String TOPIC_TRDENC;

    /**
     * IM主题
     */
    private String TOPIC_IM;

    /**
     * 单人电话主题
     */
    private String TOPIC_VOIPSINGLE;
    //[S]add by tangsha for third encrypt
    /**
     * 第三方加密
     */
    private String TOPIC_THIRDENCRYPT;
    //[E]add by tangsha for third encrypt
    private Context context;

    private Map<String, Provider<String>> stringMap;

    /**
     * 框架订阅的主题
     */
    private ArrayList<String> topics;

    @Inject
    public PushControllerImp(Map<String, Provider<String>> stringMap,
                             @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                             Context context,
                             @Named(DiConfig.PN_PROPERTIES_NAME)
                             Map<String, String> configs) {
        this.stringMap = stringMap;
        this.context = context;
        if (configs != null) {
            this.CLIENTIP = configs.get("NPSWebApiServiceIp");
            this.CLIENTPORT = configs.get("NPSWebApiServicePort");
            this.TOPIC_FRAME = configs.get("TOPIC_FRAME");
            this.TOPIC_AUTOUPDATE = configs.get("TOPIC_AUTOUPDATE");
            this.TOPIC_FRIEND = configs.get("TOPIC_FRIEND");
            this.TOPIC_GROUP = configs.get("TOPIC_GROUP");
            this.TOPIC_DEPART = configs.get("TOPIC_DEPART");
            this.TOPIC_ACCOUNT = configs.get("TOPIC_ACCOUNT");
            this.TOPIC_TRDENC = configs.get("TOPIC_TRDENC");
            this.TOPIC_IM = configs.get("TOPIC_IM");
            this.TOPIC_VOIPSINGLE = configs.get("TOPIC_VOIPSINGLE");
            //[S]add by tangsha for third encrypt
            this.TOPIC_THIRDENCRYPT = configs.get("TOPIC_THIRDENCRYPT");
            //[E]add by tangsha for third encrypt
            this.PUSH_CLIENT = configs.get("PUSH_CLIENT");
        }
    }

    @Override
    public boolean startPush() {
        if (TextUtils.isEmpty(this.CLIENTIP)
                ||TextUtils.isEmpty(this.CLIENTPORT)
                ||TextUtils.isEmpty(this.CLIENTPORT)
                ||TextUtils.isEmpty(this.TOPIC_FRAME)
                ||TextUtils.isEmpty(this.TOPIC_AUTOUPDATE)
                ||TextUtils.isEmpty(this.TOPIC_FRIEND)
                ||TextUtils.isEmpty(this.TOPIC_GROUP)
                ||TextUtils.isEmpty(this.TOPIC_DEPART)
                ||TextUtils.isEmpty(this.TOPIC_ACCOUNT)
                ||TextUtils.isEmpty(this.TOPIC_TRDENC)
                ||TextUtils.isEmpty(this.TOPIC_IM)
                ||TextUtils.isEmpty(this.TOPIC_VOIPSINGLE)
                //[S]add by tangsha for third encrypt
                ||TextUtils.isEmpty(this.TOPIC_THIRDENCRYPT)
                //[E]add by tangsha for third encrypt
                ||TextUtils.isEmpty(this.PUSH_CLIENT)) {
            LogUtil.getUtils().e("推送初始化参数异常");
            return false;
        }


        String deviceId = this.stringMap.get(CacheModule.KEY_DEVICEID).get();
        if (TextUtils.isEmpty(deviceId)) {
            LogUtil.getUtils().e("获取到的卡ID为空");
            return false;
        }
        deviceId = deviceId.toLowerCase();
        PushClient.init(this.context, PUSH_CLIENT + deviceId, CLIENTIP, CLIENTPORT, "");
        subTopics(deviceId);
        int result = PushClient.startPush(this.context);
        return result == 0;
    }

    private void subTopics(String cardId) {
        topics = new ArrayList<>();
        topics.add(PUSH_CLIENT + cardId + TOPIC_FRAME);
        topics.add(PUSH_CLIENT + cardId + TOPIC_AUTOUPDATE);
        topics.add(PUSH_CLIENT + cardId + TOPIC_FRIEND);
        topics.add(PUSH_CLIENT + cardId + TOPIC_GROUP);
        topics.add(PUSH_CLIENT + cardId + TOPIC_DEPART);
        topics.add(PUSH_CLIENT + cardId + TOPIC_ACCOUNT);
        topics.add(PUSH_CLIENT + cardId + TOPIC_TRDENC);
        topics.add(PUSH_CLIENT + cardId + TOPIC_IM);
        topics.add(PUSH_CLIENT + cardId + TOPIC_VOIPSINGLE);
        //[S]add by tangsha for third encrypt
        topics.add(PUSH_CLIENT + cardId + TOPIC_THIRDENCRYPT);
        //[E]add by tangsha for third encrypt
        PushClient.subTopic(context, topics, 0);
    }

    @Override
    public boolean releasePush() {
        PushClient.unsubscribe(context, topics,null);
        int release = PushClient.release(context);
        return release == 0;
    }
}
