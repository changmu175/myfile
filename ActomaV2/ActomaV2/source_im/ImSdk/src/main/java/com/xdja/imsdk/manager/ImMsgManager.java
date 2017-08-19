package com.xdja.imsdk.manager;

import android.os.SystemClock;
import android.text.TextUtils;

import com.xdja.google.gson.reflect.TypeToken;
import com.xdja.imsdk.constant.ImSdkConfig;
import com.xdja.imsdk.constant.StateCode;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.HttpApiConstant;
import com.xdja.imsdk.constant.internal.HttpApiConstant.ParseType;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.db.bean.LocalStateMsgDb;
import com.xdja.imsdk.db.bean.SyncIdDb;
import com.xdja.imsdk.db.helper.OptHelper;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.http.HttpUtils;
import com.xdja.imsdk.http.bean.Condition;
import com.xdja.imsdk.http.bean.ImErrorBean;
import com.xdja.imsdk.http.bean.ImRequestBean;
import com.xdja.imsdk.http.bean.ImResultBean;
import com.xdja.imsdk.http.bean.ImResultErrorBean;
import com.xdja.imsdk.http.bean.MsgBean;
import com.xdja.imsdk.http.bean.StateBean;
import com.xdja.imsdk.http.callback.IHttpCallback;
import com.xdja.imsdk.http.config.ImRequestConfig;
import com.xdja.imsdk.http.error.HttpErrorCode;
import com.xdja.imsdk.http.param.LoginPara;
import com.xdja.imsdk.http.param.MsgPara;
import com.xdja.imsdk.http.param.PullPara;
import com.xdja.imsdk.http.param.StatePara;
import com.xdja.imsdk.http.result.FailStateResult;
import com.xdja.imsdk.http.result.LoginResult;
import com.xdja.imsdk.http.result.NormalResult;
import com.xdja.imsdk.http.result.PullResult;
import com.xdja.imsdk.http.result.StateResult;
import com.xdja.imsdk.manager.callback.NetCallback;
import com.xdja.imsdk.manager.callback.ResultCallback;
import com.xdja.imsdk.util.JsonUtils;
import com.xdja.imsdk.util.ToolUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 项目名称：ImSdk             <br>
 * 类描述  ：后台同步消息管理类   <br>
 * 创建时间：2016/11/16 15:12  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImMsgManager {
    private static ImMsgManager imMsgManager;

    private String account;                                    // 账号
    private String cardId;                                     // 卡id
    private String ticket;                                     // ticket

    private ExecutorService pullPool;                          // 拉取消息任务线程池
    private ExecutorService parsePool;                         // 处理消息线程池
    private AtomicInteger pullPE = new AtomicInteger();        // 拉取消息指数
    private AtomicBoolean inSync = new AtomicBoolean();        // 拉取标识
    private AtomicBoolean newMsg = new AtomicBoolean();        // 收到过新消息广播标识
    private ResultCallback callback;                           // 回调接口
    private NetCallback net;                                   // 回调接口
    private Condition condition = new Condition(0, 0);         // 漫游、同步设置

    private long time = 0L;                                    // 时间差
    private long end = 0L;                                     // 拉取消息截至id
    private long max = 0L;                                     // 一次拉取过程中的最大的id
    private long old = 0L;                                     // 一次拉取过程中最旧的id

    private boolean preOk = true;                              // 上次拉取消息结束的状态是否正常

    private int count = 0;                                     // login次数


    /**
     * @return ImMsgManager实例
     */
    public static ImMsgManager getInstance(){
        synchronized(ImMsgManager.class) {
            if(imMsgManager == null){
                imMsgManager =  Factory.getInstance();
            }
        }
        return imMsgManager;
    }

    private static class Factory {
        static ImMsgManager getInstance() {
            return new ImMsgManager();
        }
    }

    /**
     * 初始化
     * @param account 账号
     * @param cardId 安全卡id
     * @param ticket ticket
     * @param callback 回调
     */
    public void init(String account, String cardId, String ticket,
                     ResultCallback callback, NetCallback net) {
        this.account = account;
        this.cardId = cardId;
        this.ticket = ticket;
        this.callback = callback;
        this.net = net;
        this.condition = new Condition(ImSdkConfigManager.getInstance().getSync(),
                ImSdkConfigManager.getInstance().getRoam());
        this.time = ImSdkConfigManager.getInstance().getDiff();
        this.pullPool = Executors.newSingleThreadExecutor();
        this.parsePool = Executors.newSingleThreadExecutor();
        setPulling(false);
        setNewMsg(false);
        initSyncIds();
        connectServer();
    }

    /**
     * 退出所有正在进行的业务
     */
    public void cancelAll() {
        shutdownAndAwaitTermination(pullPool);
        shutdownAndAwaitTermination(parsePool);
        imMsgManager = null;
    }

    /**
     * 发送文本消息
     * @param msgBean 消息体
     * @param id 消息id
     */
    public void sendText(MsgBean msgBean, long id) {
        sendNormal(msgBean, id + "");
    }

    /**
     * 发送状态消息
     * @param state 状态消息数据库实体
     */
    public void sendState(String state, long id) {
        sendStates(state, id);
    }

    /**
     * 接收到Push的新消息通知广播
     */
    public void handlePushNotice() {
        callbackNet(Constant.PUSH_CONNECTED);                    // 上报网络状态
        if (isPulling()) {
            setNewMsg(true);                                    // 有新消息通知
            return;
        }
        startSync();                                             // 开始同步消息
    }

    /**
     * 接收到Push建立长链接结果
     * @param state Push状态
     */
    public void connectPushResult(String state) {
        callbackNet(state);                                      // 上报网络状态
        if (Constant.PUSH_CONNECTED.equals(state)) {
            if (isPulling()) {
                return;
            }

            startSync();                                         // 开始同步消息
        }
    }

    /**
     * 拉取消息
     */
    public void syncIm() {
        connectServer();//同步时间
        pullMessage("");//同步拉取消息
    }

    /**
     * 获取和后台同步后的时间(微秒)，未同步时，先返回系统当前时间，同时触发同步
     * @return 当前时间
     */
    public long getCurrentN() {
        long result;
        if (time != 0) {
            result = SystemClock.elapsedRealtimeNanos() + time;
        } else {
            result = System.currentTimeMillis() * Constant.TIME_MULTIPLE;
        }
        return result;
    }

    /**
     * 纳秒
     * @return 当前时间
     */
    public long getCurrentM() {
        return getCurrentN()/Constant.TIME_MULTIPLE;
    }

    /**
     * 初始化同步消息id
     */
    private void initSyncIds() {
        List<SyncIdDb> ids = new ArrayList<>();
        ids.add(ModelMapper.getIns().getSync(HttpApiConstant.LONG_VALUE_0, HttpApiConstant.MAX));
        ids.add(ModelMapper.getIns().getSync(HttpApiConstant.LONG_VALUE_0, HttpApiConstant.LAST));
        ids.add(ModelMapper.getIns().getSync(HttpApiConstant.LONG_VALUE_0, HttpApiConstant.PROCESS));
        ids.add(ModelMapper.getIns().getSync(HttpApiConstant.LONG_VALUE_0, HttpApiConstant.STATE));

        ImSdkDbUtils.saveSyncIdBatch(ids);
    }

    /**
     * 连接后台，校验账号信息
     */
    private void connectServer() {
        if (time > 0) {
            return;
        }
        LoginPara login = new LoginPara(ticket, account, cardId);

        loginRequest(login, new IHttpCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                setCount(0);
                callbackNet(HttpApiConstant.HTTP_OK);
                executeParseTask(ParseType.LOGIN, jsonObject);
            }

            @Override
            public void onFailed(int code, JSONObject jsonObject) {
                processFailResult(ParseType.LOGIN, code, jsonObject);
            }

            @Override
            public void onNetChanged(int code, JSONObject jsonObject) {
                callbackNet(code);
            }
        });
    }

    /**
     * 发送消息
     * @param msgBean 文本消息
     * @param flagId 消息数据库id
     */
    private void sendNormal(MsgBean msgBean, String flagId) {
        // 警告：参数user是接收方账号
        MsgPara msg = new MsgPara(msgBean.getTo(), ticket, msgBean, flagId);

        sendNormalRequest(msg, new IHttpCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                setCount(0);
                callbackNet(HttpApiConstant.HTTP_OK);
                executeParseTask(ParseType.NORMAL, jsonObject);
            }

            @Override
            public void onFailed(int code, JSONObject jsonObject) {
                processFailResult(ParseType.NORMAL, code, jsonObject);
            }

            @Override
            public void onNetChanged(int code, JSONObject jsonObject) {
                callbackNet(code);
            }
        });
    }

    /**
     * 发送批量状态消息
     * @param states 状态消息数据库实体
     */
    private void sendStates(String states, long id) {
        StatePara state = getStatePara(states, id);

        sendStatesRequest(state, new IHttpCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                callbackNet(HttpApiConstant.HTTP_OK);
                executeParseTask(ParseType.STATE, jsonObject);
            }

            @Override
            public void onFailed(int code, JSONObject jsonObject) {
                processFailResult(ParseType.STATE, code, jsonObject);
            }

            @Override
            public void onNetChanged(int code, JSONObject jsonObject) {
                callbackNet(code);
            }
        });
    }

    /**
     * 拉取消息
     * @param startId 消息起始id
     */
    private void pullMessage(String startId) {
        String size = String.valueOf(computePullSize(getPE()));
        PullPara pull = new PullPara(account, cardId, startId, size, ticket, condition);

        setPulling(true);

        pullMessageRequest(pull,
                new IHttpCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        callbackNet(HttpApiConstant.HTTP_OK);
                        executeParseTask(ParseType.PULL, jsonObject);
                    }

                    @Override
                    public void onFailed(int code, JSONObject jsonObject) {
                        processFailResult(ParseType.PULL, code, jsonObject);
                    }

                    @Override
                    public void onNetChanged(int code, JSONObject jsonObject) {
                        callbackNet(code);
                    }
                });
    }

    /**
     * 账号校验请求
     * @param para 校验请求参数
     * @param callback 回调
     */
    private void loginRequest(LoginPara para, IHttpCallback callback) {
        ImRequestBean<LoginPara> requestBean = getRequestPara(para, HttpApiConstant.MSG_LOGIN);
        imRequest(requestBean, getRequestConfig(false, null), callback);
    }

    /**
     * 发送普通消息请求
     * @param para 普通消息请求参数
     * @param callback 回调
     */
    private void sendNormalRequest(MsgPara para, IHttpCallback callback) {
        connectServer();// before net request, check time distance with server.
        ImRequestBean<MsgPara> requestBean = getRequestPara(para, HttpApiConstant.MSG_SEND);
        imRequest(requestBean, getRequestConfig(true, para.getFlagid()), callback);
    }

    /**
     * 发送状态消息请求
     * @param para 状态消息请求参数
     * @param callback 回调
     */
    private void sendStatesRequest(StatePara para, IHttpCallback callback) {
        connectServer();// before net request, check time distance with server.
        ImRequestBean<StatePara> requestBean = getRequestPara(para, HttpApiConstant.MSG_SEND_BATCH);
        imRequest(requestBean, getRequestConfig(false, null), callback);
    }


    /**
     * 拉取消息请求
     * @param para 请求参数
     * @param callback 回调
     */
    private void pullMessageRequest(PullPara para, IHttpCallback callback) {
        connectServer();// before net request, check time distance with server.
        ImRequestBean<PullPara> requestBean = getRequestPara(para, HttpApiConstant.MSG_GET);
        imRequest(requestBean, getRequestConfig(false, null), callback);
    }

    /**
     * 网络请求
     * @param requestBean 请求参数
     * @param config 请求配置参数
     * @param callback 回调
     */
    private <T> void imRequest(ImRequestBean<T> requestBean, ImRequestConfig config,
                               IHttpCallback callback) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JsonUtils.getGson().toJson(requestBean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().sendPostRequest(jsonObject, config, callback);
    }

    /**
     * 组装请求配置参数
     * @param needRetry 重试
     * @param flagId 消息id
     * @return 配置参数
     */
    private ImRequestConfig getRequestConfig(boolean needRetry, String flagId) {
        ImRequestConfig config = new ImRequestConfig();
        config.setNeedRetry(needRetry);
        config.setOptions(HttpApiConstant.HTTP_CONFIG_TICKET, ticket);
        if (!TextUtils.isEmpty(flagId)) {
            config.setMsgId(flagId);
        }
        return config;
    }

    /**
     * 组装请求参数
     * @param t 请求内容
     * @param method 请求接口
     * @return 请求参数
     */
    private <T> ImRequestBean<T> getRequestPara(T t, String method) {
        ImRequestBean<T> bean = new ImRequestBean<>();
        bean.setParams(t);
        bean.setId(HttpApiConstant.ID);
        bean.setJsonrpc(HttpApiConstant.JSONRPC);
        bean.setMethod(method);
        return bean;
    }

    private StatePara getStatePara(String state, long id) {
        StatePara statePara = new StatePara();
        statePara.setFlagid(String.valueOf(id));
        statePara.setContent(state);
        statePara.setFst(String.valueOf(getCurrentM()));
        statePara.setFi(cardId);
        statePara.setI("-1");
        statePara.setSst("-1");
        statePara.setLc("-1");
        return statePara;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool == null) {
            return;
        }

        pool.shutdown();                    // Disable new tasks from being submitted
        pool.shutdownNow();                 // (Re-)Cancel if current thread also interrupted
        Thread.currentThread().interrupt(); // Preserve interrupt status
    }

    /**
     * 判断是否正在拉取消息
     * @return 结果
     */
    private boolean isPulling() {
        return inSync.get();
    }

    private void setPulling(boolean pulling) {
        inSync.set(pulling);
    }

    private boolean hasNewMsg() {
        return newMsg.get();
    }

    private void setNewMsg(boolean isNew) {
        newMsg.set(isNew);
    }

    private int getPE() {
        return pullPE.get();
    }

    private void setPE(int pull) {
        pullPE.set(pull);
    }

    private int getCount() {
        return count;
    }

    private void setCount(int num) {
        count = num;
    }

    /**
     * 计算拉取消息的大小
     * @return 拉取消息的大小
     */
    private int computePullSize(int exponent) {
        int pullSize = (int) Math.pow(HttpApiConstant.POWER_BASE_PULL, exponent);
        if (pullSize > HttpApiConstant.MAX_PULL_MSG_COUNT) {
            pullSize = HttpApiConstant.MAX_PULL_MSG_COUNT;
        }

        return pullSize;
    }

    /**
     * 下列情况触发：                         <br>
     * 1、接收到Push的新消息通知广播            <br>
     * 2、推送重连长链接建立成功                <br>
     * 3、ImSdk账号校验成功                   <br>
     * 后续动作：                             <br>
     * 1、发送本地状态消息                     <br>
     * 2、开始连接后台拉取消息任务              <br>
     */
    private void startSync() {
        callback.SendState();
        startPull();
    }

    /**
     * 开始拉取消息：                 <br>
     * 1、初始化成功，账号校验通过      <br>
     * 2、推送重连成功                <br>
     * 3、收到新消息的广播通知         <br>
     */
    private void startPull() {
        setPE(1);
        setNewMsg(false);
        executePullTask(HttpApiConstant.STRING_DEFAULT_EMPTY);
    }

    /**
     * 开始下一次循环拉取消息
     * @param startId 起始id
     */
    private void cyclePull(String startId) {
        pullPE.incrementAndGet();
        executePullTask(startId);
    }

    /**
     * 回调网络状态
     * @param code 状态码
     */
    private void callbackNet(int code) {
        net.NetChanged(String.valueOf(code));
    }

    /**
     * 回调网络状态
     * @param code 状态码
     */
    private void callbackNet(String code) {
        net.NetChanged(code);
    }

    /**
     * 回调异常状态码
     * @param code 状态码
     */
    private void callbackException(int code) {
        // Ticket过期
        if (code == HttpErrorCode.TICKET_EXPIRE) {
            callbackImSdk(StateCode.SDK_TICKET_EXPIRE);
        }

        // 发送消息如果失败，会调用解析方法刷新消息状态，最终仍然调用checkResultCallback
//        if (type != ParseJsonType.PARSE_TYPE_SEND_MESSAGE) {
//            checkResultCallback(false, ErrorCode.ECODE_SDK_CONNECTED_FAIL);
//        }
    }

    /**
     * 回调ImSdk状态
     * @param code 状态码
     */
    private void callbackImSdk(int code) {
        ImSdkCallbackManager.getInstance().callState(code);
    }

    /**
     *
     * @param id
     */
    private void callbackChange(String id) {
        // TODO: 2016/12/19 liming 可以优化
        MessageWrapper msg = ImSdkDbUtils.
                queryMessage(OptHelper.getIns().
                        getAMQuery(Long.valueOf(id)), OptType.MQuery.ALL);

        if (msg.getMsgEntryDb() == null) {
            return;
        }
        SessionWrapper session = ImSdkDbUtils.
                querySession(OptHelper.getIns().
                        getSMQuery(msg.getMsgEntryDb().getSession_flag()), OptType.SQuery.HAVE);

        ImSdkCallbackManager.getInstance().
                callChange(ModelMapper.getIns().mapSession(session), ModelMapper.getIns().mapMessage(msg));
    }

    /**
     * 保存sync id
     * @param save save
     */
    private void updateEnd(boolean save) {
        List<UpdateArgs> args = new ArrayList<>();
        long state = HttpApiConstant.LONG_VALUE_0;
        if (save) {
            UpdateArgs maxUpdate = OptHelper.getIns().getSyncUpdate(HttpApiConstant.MAX, max);
            UpdateArgs lastUpdate = OptHelper.getIns().getSyncUpdate(HttpApiConstant.LAST, old);

            args.add(maxUpdate);
            args.add(lastUpdate);
        } else {
            state = HttpApiConstant.LONG_VALUE_1;
        }

        UpdateArgs stateUpdate = OptHelper.getIns().getSyncUpdate(HttpApiConstant.STATE, state);
        args.add(stateUpdate);

        ImSdkDbUtils.updateBatch(args);
    }

    /**
     * 保存发送失败的状态消息
     * @param failStates failStates
     */
    private void saveFailState(List<FailStateResult> failStates) {
        List<StateBean> stateBeans = new ArrayList<>();
        if (failStates == null || failStates.isEmpty()) {
            return;
        }

        for(FailStateResult state:failStates) {
            if (state.getCode() == Constant.SERVER_ERROR ||
                    state.getCode() == Constant.SERVER_NO_WORKING) {
                StateBean stateBean = new StateBean();
                stateBean.setStat(state.getStat());
                stateBean.setC(state.getMsgid() + "");
                stateBean.setT(state.getT());
                stateBean.setTo(state.getTo());
                stateBean.setF(account);
                stateBeans.add(stateBean);
            }
        }

        if (!stateBeans.isEmpty()) {
            LocalStateMsgDb localStatus = new LocalStateMsgDb();
            localStatus.setContent(JsonUtils.getGson().toJson(stateBeans));
            localStatus.setSendTime(getCurrentM());
            ImSdkDbUtils.saveLocal(localStatus);
        }
    }

    /**
     * 消息发送成功，更新消息的server id, state, arrive time, attr(?)
     * @param result 结果
     */
    private void sendNorSuccess(NormalResult result) {
        String serverId = result.getMsgid();
        String _id = result.getFlagid();

        ImSdkDbUtils.update(OptHelper.getIns().getMSUpdate(_id, serverId));

        callbackChange(_id);
    }

    /**
     * updateAndCallbackMessageState
     * @param result result
     */
    private void sendStateSuccess(StateResult result) {
        long _id = result.getFlagid();
        ImSdkDbUtils.delete(OptHelper.getIns().getLSDel(String.valueOf(_id)));
        if (result.getCode() == Constant.SERVER_STATE_FAIL) {
            List<FailStateResult> failStates = result.getStatmsg();
            saveFailState(failStates);
        }
    }

    /**
     * updateSendMessageFail
     * @param id id
     * @param code code
     */
    private void sendNorFail(String id, int code) {
        ImSdkDbUtils.update(OptHelper.getIns().getMFUpdate(id, code));

        callbackChange(id);
    }

    /**
     * 直接删除
     * @param id id
     */
    private void sendStateFail(long id) {
        ImSdkDbUtils.delete(OptHelper.getIns().getLSDel(String.valueOf(id)));
    }



    /**
     * 在本次pull开始时，获取本次pull结束的结束id。
     * 1、如果startId不是空，表示本次pull是一次循环pull中的某一次拉取任务，
     *    结束id已经在第一次pull得到了。
     * 2、如果startId为空，表示本次pull是本次第一次请求。
     *
     * 结束id获取策略：
     * 通过判断已处理的最后的消息id和pull到的最后的id是否相等，来判断上次pull到的消息是否处理完毕。
     * 1、如果没有处理完毕，则本次pull到上次pull的最后的消息id时结束，即本次pull包含上次pull。
     * 2、如果处理完毕，则本次pull到上次pull的最大消息id时结束。
     * @param startId 起始id
     */
    private void queryEnd(String startId) {
        if (HttpApiConstant.STRING_DEFAULT_EMPTY.equals(startId)) {
            long max = ImSdkDbUtils.querySyncId(HttpApiConstant.MAX);
            long old = ImSdkDbUtils.querySyncId(HttpApiConstant.LAST);
            long process = ImSdkDbUtils.querySyncId(HttpApiConstant.PROCESS);
            long state = ImSdkDbUtils.querySyncId(HttpApiConstant.STATE);
            if (process == old) {
                end = max;
            } else {
                end = old;
            }

            preOk = state == HttpApiConstant.LONG_VALUE_0;
        }
    }

    /**
     * 一次和后台进行消息同步结束
     * 如果是非正常拉取消息的结束，不能保存已刷新的id，否则会可能造成漏消息
     * @param save 是否需要保存同步id。
     */
    private void syncEnd(boolean save) {
        updateEnd(save);
        setPE(1);
        setPulling(false);
        if (hasNewMsg()) {
            startPull();
        }
    }

    private void saveTime() {
        HashMap<String, String> timeMap = new HashMap<>();
        timeMap.put(ImSdkConfig.K_DIFF, String.valueOf(time));
        ImSdkConfigManager.getInstance().saveConfig(timeMap);
    }

    private boolean inSyncTime(MsgBean msg) {
        if (ImSdkConfigManager.getInstance().getSync() > 0) {
            long distance = getCurrentM() - msg.getSst();
            if (distance > 0 && distance < ImSdkConfigManager.getInstance().getSync()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isUnRecNormal(MsgBean msg) {
        return (!msg.isStateMsg() &&
                !msg.getF().equals(account) &&
                msg.getStat() == State.SENT);
    }

    private boolean isRecNormal(MsgBean msg) {
        return (!msg.isStateMsg() &&
                !msg.getF().equals(account) &&
                msg.getStat() > State.SENT);
    }

    private boolean isState(MsgBean msg) {
        return (msg.isStateMsg() &&
                msg.getStat() > State.SENT);
    }

    /**
     * 执行解析结果任务
     * @param type 任务类型
     * @param jsonObject 结果
     */
    private void executeParseTask(ParseType type, JSONObject jsonObject) {
        if (!parsePool.isShutdown()) {
            parsePool.execute(new ParseTask(type, jsonObject));
        }
    }

    /**
     * 执行拉取消息任务
     * @param startId 起始消息id
     */
    private void executePullTask(String startId) {
        if (!pullPool.isShutdown()) {
            pullPool.execute(new PullTask(startId));
        }
    }

    /**
     * 解析失败结果
     * @param type type
     * @param code code
     * @param jsonObject json
     */
    private void processFailResult(ParseType type, int code, JSONObject jsonObject) {
        switch (type) {
            case LOGIN:
                callbackException(code);
                count = count + 1;
                if (getCount() < 4) {
                    connectServer();
                } // TODO: 2016/12/9 liming 是否需要重置time为0
                break;
            case PULL:
                callbackException(code);
                processErrorResult(ParseType.PULL, jsonObject);
                syncEnd(false);
//                executeParseTask(ParseType.PULL, jsonObject);
                break;
            case NORMAL:
                callbackException(code);
                executeParseTask(ParseType.NORMAL, jsonObject);
                break;
            case STATE:
                callbackException(code);
                processErrorResult(ParseType.STATE, jsonObject);
//                executeParseTask(ParseType.STATE, jsonObject);
                break;
        }
    }

    private void processErrorResult(ParseType type, JSONObject jsonObject) {
        int code;
        ImErrorBean<ImResultErrorBean> errorBean = JsonUtils.getGson().fromJson(jsonObject.toString(),
                new TypeToken<ImErrorBean<ImResultErrorBean>>() {
                }.getType());

        if (errorBean == null) {
            code = StateCode.SDK_UNKNOWN;
        } else {
            if (errorBean.getError() == null) {
                code = StateCode.SDK_UNKNOWN;
            } else {
                code = errorBean.getError().getCode();
            }
        }

        switch (type) {
            case LOGIN:
                count = count + 1;
                if (getCount() < 4) {
                    connectServer();
                } // TODO: 2016/12/9 liming 是否需要重置time为0
                break;
            case PULL:
                break;
            case NORMAL:
                if (errorBean != null && errorBean.getError() != null) {
                    sendNorFail(errorBean.getError().getFlagid(), errorBean.getError().getCode());
                } else {
                    ImRequestBean<MsgPara> sendingPara = JsonUtils.getGson().fromJson(
                            jsonObject.toString(),
                            new TypeToken<ImRequestBean<MsgPara>>() {
                            }.getType());
                    if (sendingPara != null && sendingPara.getParams() != null) {
                        sendNorFail(sendingPara.getParams().getFlagid(), 0);
                    }
                }
                break;
            case STATE:
                if (errorBean != null && errorBean.getError() != null) {
                    if (errorBean.getError().getCode() == Constant.SERVER_PARA) {
                        // 服务器返回异常，即当前的批量状态消息未通过服务器校验，直接删除
                        long _id = Long.parseLong(errorBean.getError().getFlagid());
                        sendStateFail(_id);
                    }
                }
                break;
        }

        callbackImSdk(code);
    }

    /**
     * 解析账号校验结果
     * @param jsonObject 返回结果
     */
    private void parseLoginResult(JSONObject jsonObject) {
        ImResultBean<LoginResult> bean = JsonUtils.getGson().fromJson(jsonObject.toString(),
                new TypeToken<ImResultBean<LoginResult>>() {
                }.getType());

        if (bean == null) {
            callbackImSdk(StateCode.SDK_UNKNOWN);
        }

        if (bean != null && bean.getResult() != null) {
            time = ToolUtils.getTimeDistance(bean.getResult().getSst());
            saveTime();
            callbackImSdk(StateCode.SDK_CONNECTED_OK);
            startSync();
            return;
        }

        processErrorResult(ParseType.LOGIN, jsonObject);
    }

    /**
     * 解析拉取到的消息
     * @param jsonObject 返回结果
     */
    private void parsePulledResult(JSONObject jsonObject) {
        ImResultBean<PullResult> bean = JsonUtils.getGson().fromJson(jsonObject.toString(),
                new TypeToken<ImResultBean<PullResult>>() {
                }.getType());

        if (bean == null) {
            syncEnd(true);
            callbackImSdk(StateCode.SDK_UNKNOWN);
            return;
        }

        if (bean.getResult() != null) {
            List<MsgBean> list = bean.getResult().getData();

            if (list == null || list.isEmpty()) {
                syncEnd(true);
                return;
            }

            boolean syncDone = false;                             //是否结束循环拉取消息标识
            String nextStartId = "";                              //下次循环拉取的起始消息id
            List<MsgBean> callbackList = new ArrayList<>();

            // 检查消息拉取是否需要停止
            for (MsgBean msg : list) {
                boolean idTouched = (msg.getI() == end);          // 拉取的消息id与上次拉取的最大id相等
                boolean inSync = inSyncTime(msg);                 // 同步周期内
                boolean unRecNormal = isUnRecNormal(msg);         // 未接收过的普通消息
                boolean isReceived = isRecNormal(msg);            // 接收过的普通消息
                boolean isState = isState(msg);                   // 考虑到抄送，状态消息不做是否是自己发出去的验证

                if (!idTouched) {
                    if (inSync) {
                        callbackList.add(msg);

                        if (msg.getI() > max) {
                            max = msg.getI();
                        }
                    } else {
                        if (unRecNormal || isState) {
                            callbackList.add(msg);

                            if (msg.getI() > max) {
                                max = msg.getI();
                            }
                        }

                        if (isReceived && preOk) {
                            syncDone = true;
                            break;
                        }
                    }
                } else {
                    syncDone = true;
                    break;
                }
            }

            if (!callbackList.isEmpty()) {
                callback.ReceiveMessage(callbackList);
                // callback list order:[10，9，8...]
                old = callbackList.get(callbackList.size() - 1).getI();// TODO TEST

                nextStartId = String.valueOf(list.get(list.size() - 1).getI());
            } else {
                syncDone = true;
            }

//            if (list.size() < computePullSize(getPE())) {
//                syncDone = true;
//            }


            if (syncDone) {
                syncEnd(true);
                return;
            } else {
                cyclePull(nextStartId);// TODO: 2016/12/9 liming test
            }
            return;
        }

        processErrorResult(ParseType.PULL, jsonObject);
        syncEnd(true);
    }

    /**
     * 解析发送消息返回结果
     * @param jsonObject 返回结果
     */
    private void parseNormalResult(JSONObject jsonObject) {
        ImResultBean<NormalResult> bean = JsonUtils.getGson().fromJson(
                jsonObject.toString(),
                new TypeToken<ImResultBean<NormalResult>>() {
                }.getType());

        if (bean != null && bean.getResult() != null) {
            sendNorSuccess(bean.getResult());
            return;
        }

        processErrorResult(ParseType.NORMAL, jsonObject);
    }

    /**
     * 解析发送状态消息结果
     * @param jsonObject 返回结果
     */
    private void parseStateResult(JSONObject jsonObject) {
        ImResultBean<StateResult> bean = JsonUtils.getGson().fromJson(
                jsonObject.toString(),
                new TypeToken<ImResultBean<StateResult>>() {
                }.getType());
        if (bean != null && bean.getResult() != null) {
            sendStateSuccess(bean.getResult());
            return;
        }

        processErrorResult(ParseType.STATE, jsonObject);
    }

    /**
     * 解析收到的请求结果
     */
    private class ParseTask implements Runnable{
        private ParseType parseType;
        private JSONObject jsonObject;

        private ParseTask(ParseType type, JSONObject jsonObject) {
            this.parseType = type;
            this.jsonObject = jsonObject;
        }

        @Override
        public void run() {
            switch (parseType) {
                case LOGIN:
                    parseLoginResult(jsonObject);
                    break;
                case PULL:
                    parsePulledResult(jsonObject);
                    break;
                case NORMAL:
                    parseNormalResult(jsonObject);
                    break;
                case STATE:
                    parseStateResult(jsonObject);
                default:
                    break;
            }
        }
    }

    /**
     * 拉取消息任务
     */
    private class PullTask implements Runnable{
        private String startId;                       // 拉取消息起始id

        private PullTask(String startId) {
            this.startId = startId;
        }
        @Override
        public void run() {
            queryEnd(startId);                          // 获取本次pull结束的标识id
            pullMessage(startId);
        }
    }
}
