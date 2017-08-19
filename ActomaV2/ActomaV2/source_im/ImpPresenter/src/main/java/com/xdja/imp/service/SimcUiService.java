package com.xdja.imp.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.event.TicketAuthCompleteEvent;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.ActionUtil;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.cache.SharedPreferencesUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.imp.IMAccountLifeCycle;
import com.xdja.imp.ISimcUiGuardAidlInterface;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.CardEntity;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.cache.UserEntity;
import com.xdja.imp.data.error.OkException;
import com.xdja.imp.data.error.OkHandler;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.persistent.PreferencesUtil;
import com.xdja.imp.data.persistent.PropertyUtil;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.di.component.DaggerUseCaseComponent;
import com.xdja.imp.di.component.UseCaseComponent;
import com.xdja.imp.di.component.UserComponent;
import com.xdja.imp.domain.interactor.def.AddUserAccount;
import com.xdja.imp.domain.interactor.def.CallBackRegist;
import com.xdja.imp.domain.interactor.def.CallBackUnRegist;
import com.xdja.imp.domain.interactor.def.ClearAllMsgInTalk;
import com.xdja.imp.domain.interactor.def.DeleteAllDraft;
import com.xdja.imp.domain.interactor.def.DeleteNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.DeleteSession;
import com.xdja.imp.domain.interactor.def.DeleteSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.DeleteTopSetting;
import com.xdja.imp.domain.interactor.def.GetAllMissedCount;
import com.xdja.imp.domain.interactor.def.InitIMProxy;
import com.xdja.imp.domain.interactor.def.ReleaseIMProxy;
import com.xdja.imp.domain.interactor.im.AddCustomMsgUseCase;
import com.xdja.imp.domain.interactor.im.ClearAllDataUseCase;
import com.xdja.imp.domain.interactor.mx.GetSingleSessionConfigUseCase;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.event.SessionChangedEvent;
import com.xdja.imp.handler.OkHandlerImp;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.util.MsgDisplay;
import com.xdja.imp.util.NotificationUtil;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;


/**
 * Created by xrj on 2015/9/1.
 */
public class SimcUiService extends Service {
    private static final String TAG = "SimcUiService";
    private SimcUiServiceReceiver receiver;
    private static boolean isRun = false;
    private static boolean isLogin = false;

    //事件总线
    @Inject
    BusProvider busProvider;
    @Inject
    PropertyUtil propertyUtil;
    @Inject
    UserCache userCache;
    @Inject
    CardCache cardCache;
    @Inject
    AddUserAccount addUserAccount;
    //注册回调句柄
    @Inject
    CallBackRegist callBackRegist;
    //注销回调句柄
    @Inject
    CallBackUnRegist callBackUnRegist;
    //IMSDK初始化
    @Inject
    InitIMProxy initIMProxy;
    //释放IMSDK资源
    @Inject
    ReleaseIMProxy releaseIMProxy;

    @Inject
    Lazy<AddCustomMsgUseCase> addCustomMsg;

    @Inject
    Lazy<DeleteSession> deleteSession;

    @Inject
    Lazy<ClearAllDataUseCase> clearAllMessages;

    @Inject
    Lazy<GetAllMissedCount> getAllMissedCountLazy;

    @Inject
    Lazy<ContactService> contactService;

    @Inject
    Lazy<DeleteAllDraft> deleteAllDraftUseCase;

    @Inject
    Lazy<ClearAllMsgInTalk> clearAllMsgInTalk;

    @Inject
    Lazy<DeleteSingleSessionConfig> deleteSingleSessionConfig;

    /**
     * 获取单个会话的配置信息，主要是用于获取免打扰状态
     */
    @Inject
    Lazy<GetSingleSessionConfigUseCase> getSingleSession;

    @Inject
    Lazy<DeleteTopSetting> deleteTopSetting;

    @Inject
    Lazy<DeleteNoDisturbSetting> deleteNoDisturbSetting;

    private UseCaseComponent useCaseComponent;

    private OkHandler<OkException> okHandler;

    private static ImUICallback mUiCallback;

    private ISimcUiGuardAidlInterface mISimcUiGuardAidlInterface;

    private SimcUiHandler mSimcUiHandler;

    private boolean mIsInit = false;

    public interface ImUICallback {
        void onInitFinished();
    }

    public static void addUiCallback(ImUICallback cb) {
        mUiCallback = cb;
    }

    public static void removeUiCallback(ImUICallback cb) {
        LogUtil.getUtils().e("removeUiCallback mUiCallback ");
        if (mUiCallback != null && mUiCallback.equals(cb)) {
            mUiCallback = null;
        }
    }
    private final static String SILENT_LOGIN_ACTION = "com.xdja.action.silentlogin";
    private boolean init(Application application) {
        if (null == IMAccountLifeCycle.imAccountLifeCycle || null == IMAccountLifeCycle.imAccountLifeCycle.getComponent()) {
            //容错处理，回退到登陆界面
            //Intent intent = Intent.makeMainActivity(
//            new ComponentName(getPackageName(), "com.xdja.presenter_mainframe.presenter.activity
// .LauncherPresenter"));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.putExtra("exit", true);
//            startActivity(intent);
            LogUtil.getUtils().e("未查询到用户信息注入提供对象 init stopSelf, isLogin : " + isLogin);
            Log.v(TAG , "H>>> SimcUiService stop self because user is null");
            sendBroadcast(new Intent(SILENT_LOGIN_ACTION));
            SharedPreferencesUtil.setNormalStopService(getApplicationContext() , true);
            stopSelf();
            return false;
        } else {
            UserComponent userComponent =
                    IMAccountLifeCycle.imAccountLifeCycle.getComponent();
            useCaseComponent = DaggerUseCaseComponent.builder()
                    .userComponent(userComponent)
                    .build();
            Log.d("SimcUiService", "useCaseComponent="+useCaseComponent);

            this.userCache = this.useCaseComponent.userCache();
            MsgDisplay msgDisplay = this.useCaseComponent.msgDisplay();
            this.okHandler = new OkHandlerImp<>(msgDisplay);
            return true;
        }
    }

    private void storeAccountData(final String account, final String cardId, final String ticket) {

        Log.d(TAG, "storeAccountData ConstDef.PRONAME=" + account);

        if (null != addUserAccount) {
            addUserAccount.add(ConstDef.PRONAME).execute(new OkSubscriber<Boolean>(okHandler) {
                @Override
                public void onNext(Boolean b) {
                    super.onNext(b);
                	Log.d(TAG, "存储用户名成功");
                }
            });
        }
        CardEntity cardEntity = new CardEntity();
        cardEntity.setCardId(cardId);
        if (null != cardCache) {
            cardCache.put(cardEntity);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setTicket(ticket);
        userEntity.setAccount(account);
        if (null != userCache) {
            userCache.put(userEntity);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG , "H>>> SimcUiService onCreate");

        isLogin = false;
        //初始化
        mIsInit = init(getApplication());
        Log.d(TAG, "useCaseComponent = " + useCaseComponent);
        if (null != useCaseComponent) {
            //初始化注入
            useCaseComponent.inject(this);
        }
        if (null != busProvider) {
            //初始化事件总线
            busProvider.register(this);
        }
        registerBroadcastReceiver();
        if (mIsInit) {
            Log.v(TAG , "H>>> SimcUiService bindService : SimcUiGuardService");
            mSimcUiHandler = new SimcUiHandler(this);
            bindService(new Intent(this, SimcUiGuardService.class), mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG , "H>>> SimcUiService onServiceConnected");
            mISimcUiGuardAidlInterface = ISimcUiGuardAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG , "H>>> SimcUiService onServiceDisconnected : " + mSimcUiHandler);
            if (mSimcUiHandler != null) {
                mSimcUiHandler.sendEmptyMessageDelayed(MSG_BIND_SERVICE, DELAYED);
            }
        }
    };

    private final int MSG_BIND_SERVICE = 0;
    private final int DELAYED = 10 * 1000;

    @SuppressLint("HandlerLeak")
    private class SimcUiHandler extends Handler {
        WeakReference<SimcUiService> mWeakReference;
        public SimcUiHandler(SimcUiService weak) {
            if (weak != null) mWeakReference = new WeakReference<>(weak);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null || CommonUtils.isServiceRunning(SimcUiService
                    .this.getBaseContext(), SimcUiGuardService.class.getName())) {
                Log.v(TAG , "H>>> SimcUiService handleMessage return");
                return;
            }
            Log.v(TAG , "H>>> SimcUiService start SimcUiGuardService");
            Intent intent = new Intent(SimcUiService.this, SimcUiGuardService.class);
            startService(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.getUtils().e("SimcUiService onStartCommand intent="+intent+", isLogin="+isLogin+",flags="+flags);
        String account = "";
        String cardId = "";
        String ticket = "";
        if (intent != null) {
            if ("com.xdja.imp.service.ClearAllMessages".equals(intent.getAction())) {
                dropAllMessages();
                return super.onStartCommand(intent, flags, startId);
            }
            account = intent.getStringExtra(ConstDef.TAG_ACCOUNT);
            cardId = intent.getStringExtra(ConstDef.TAG_CARDID);
            ticket = intent.getStringExtra(ConstDef.TAG_TICKET);
        }


        LogUtil.getUtils().e("SimcUiService onStartCommand "+", account="+account+"," +
                "cardId="+cardId+", ticket="+ticket+","+ConstDef.PRONAME);

        if (TextUtils.isEmpty(account)) {
            AccountBean accountBean = AccountServer.getAccount();
            if(accountBean == null) {
                Log.e(TAG, "没有获取到合法的安通帐号！");
                return super.onStartCommand(intent, flags, startId);
            }

            account = accountBean.getAccount();
            cardId = TFCardManager.getTfCardId();
            ticket = PreferencesServer.getWrapper(this).gPrefStringValue("ticket");
        }
        //TODO:IM发消息，服务器校验卡ID，大小写敏感，统一小写
        if (cardId != null) {
            cardId = cardId.toLowerCase();
        }
        if (ticket != null) {
            ticket = ticket.toLowerCase();
        }
        if (!TextUtils.isEmpty(account)) {
            ConstDef.PRONAME = account;
            storeAccountData(account, cardId, ticket);
        }
        isRun = true;
        LogUtil.getUtils().e("SimcUiService isLogin = " + isLogin + ", account = " + account +
                ", initIMProxy = " + initIMProxy + ", isRun = "+isRun);
        if (!isLogin && null != initIMProxy) {
            initIMProxy.execute(new IntegerResultSubScriber(getResources().getString(R.string.init_im_sdk)));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void refreshTabNoReadCount(int unreadCount) {
        TabTipsEvent event = new TabTipsEvent();
        if (unreadCount > 0) {
            if (unreadCount > 99) {
                event.setContent(Html.fromHtml("<b>...</b>"));
            } else {
                event.setContent(String.valueOf(unreadCount));
            }
            event.setIsShowPoint(true);
        } else {
            event.setIsShowPoint(false);
        }
        event.setIndex(TabTipsEvent.INDEX_CHAT);
        if (null != busProvider) {
            busProvider.post(event);
        }
    }


    @Override
    public void onDestroy() {
        Log.v(TAG , "H>>> SimcUiService onDestroy");
        super.onDestroy();
        isRun = false;
        isLogin = false;
        LogUtil.getUtils().e("SimcUiService onDestroy release sdk, isLogin = " + isLogin+" , isRun = "+isRun);

        if (null != busProvider) {
            busProvider.unregister(this);
        }
        if (null != callBackUnRegist) {
            callBackUnRegist.execute(new IntegerResultSubScriber(getResources().getString(R.string.all_callback_destroy)));
        }
        if (null != releaseIMProxy) {
            releaseIMProxy.execute(new IntegerResultSubScriber(getResources().getString(R.string.sdk_release)));
        }
        unRegisterBroadcastReceiver();
        if (mIsInit) {
            Log.v(TAG , "H>>> SimcUiService unbindService");
            unbindService(mServiceConnection);
        }
        //add by李盈青
        //清空登录的缓存信息
        //end
    }

    public static boolean isLogin() {
        return isLogin;
    }

    private void startGetTicketService() {
        final String ACTION_AUTH_TICKET = "com.xdja.actoma.service.authticket";
        try {
            Intent intent = new Intent(this, Class.forName("com.xdja.actoma.service.GuardService"));
            intent.setAction(ACTION_AUTH_TICKET);
            startService(intent);
        } catch (ClassNotFoundException ex) {
//            LogUtil.getUtils().e(ex.getMessage());
        }
    }

    private void registerBroadcastReceiver() {
        if (receiver == null) {
            receiver = new SimcUiServiceReceiver();

            IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction(XmppServiceConfig.tokenInvalidAction);//ticket超期
            // intentFilter.addAction(XmppServiceConfig.SIMC_ACTION_CONNECT_STATE_CHANGED);
            registerReceiver(receiver, intentFilter);
        }
    }

    /**
     * 注销广播
     */
    private void unRegisterBroadcastReceiver() {
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        } finally {
            receiver = null;
        }
    }

    private void dropAllMessages() {
        if (null == clearAllMessages) {
            return;
        }
        clearAllMessages.
                get().
                deleteAllSession().
                execute(new IntegerResultSubScriber(getResources().getString(R.string.delete_all_chat_message)) {
                    //fix bug 3683 by licong, reView by gbc, 2016/9/7
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);

                        if (null != deleteAllDraftUseCase) {
                            //删除所有草稿
                            deleteAllDraftUseCase
                                    .get()
                                    .execute(new OkSubscriber<Boolean>(okHandler) {
                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            super.onNext(aBoolean);
                                            if (aBoolean) {
                                                NotificationUtil.getInstance(getApplicationContext()).clearPNNotification();
                                            }
                                            final PreferencesUtil preferenceUtil = new PreferencesUtil(getApplicationContext());
                                            preferenceUtil.setPreferenceBooleanValue(ConstDef.HAS_LOAD_ANTONG_MSG, false);
                                            postAllMsgDeletedEvent();

                                            //广播删除聊天记录结果
                                            Intent intent = new Intent(ActionUtil.ACTION_DROP_MESSAGE);
                                            String result = getResources().getString(R.string.delete_all_chat_message_success);
                                            if (!aBoolean) {
                                                result = getResources().getString(R.string.delete_all_chat_message_fail);
                                            }
                                            intent.putExtra("result", result);
                                            sendBroadcast(intent);

                                            //add by zya@xdja.com,20161109
                                            userCache.clearCacheText();
                                            //end by zya@xdja.com
                                        }
                                    });
                        }
                    }
                });

    }


    private class SimcUiServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
        }
    }


    private void postSessionDeleteEvent(String account, int type) {
        String sessionFlag = account+"_"+type;
        TalkListBean talkListBean = new TalkListBean();
        talkListBean.setTalkFlag(sessionFlag);
        IMProxyEvent.DeleteTalkEvent talkEvent
                = new IMProxyEvent.DeleteTalkEvent();
        talkEvent.setTalkId(talkListBean.getTalkFlag());
        talkEvent.setTalkListBean(talkListBean);
        if (null != busProvider) {
            busProvider.post(talkEvent);
        }
    }

    private void postAllMsgDeletedEvent() {
        try {
            //构建事件对象
            IMProxyEvent.RefreshTalkListEvent talkEvent
                    = new IMProxyEvent.RefreshTalkListEvent();

//            //打印事件对象
//            LogUtil.getUtils().d(talkEvent.toString());
            //发送事件
            //fix bug by licong, review by zya,2016/8/9
            talkEvent.setDeleteAllMsg(true);
            if (null != busProvider) {
                busProvider.post(talkEvent);
            }

        } catch (Exception ex) {
//            //处理异常信息
//            LogUtil.getUtils().e(ex.getMessage());
        }
    }

    /**
     * 该页面通用的整型返回值的结果监听
     */
    class IntegerResultSubScriber extends OkSubscriber<Integer> {

        private String operateName = "";

        public IntegerResultSubScriber(String operateName) {
            super(okHandler);
            this.operateName = operateName;
        }

        @Override
        public void onError(Throwable e) {
            //super.onError(e);
            LogUtil.getUtils().e(operateName + "发生错误，错误信息：" + e.getMessage());
        }

        @Override
        public void onNext(Integer integer) {
            super.onNext(integer);
//            LogUtil.getUtils().d(operateName + (integer == 0 ? "成功" : "失败"));
        }
    }

    /*======================================= 以下为Otto接收事件处理 ==============================================*/
    @Subscribe
    public void onReceiveTicketAuthErrorEvent(TicketAuthErrorEvent errorEvent) {
        //退出登录，停止IM服务
        SharedPreferencesUtil.setNormalStopService(getBaseContext(), true);
        stopSelf();
    }

    @Subscribe
    public void onReceiveTicketAuthCompleteEvent(TicketAuthCompleteEvent ticketAuthCompleteEvent) {
        String ticket = ticketAuthCompleteEvent.getNewTicket();
        String cardId = TFCardManager.getTfCardId();
        //TODO:IM发消息，服务器校验卡ID，大小写敏感，统一小写
        if (cardId != null) {
            cardId = cardId.toLowerCase();
        }
        storeAccountData(ConstDef.PRONAME, cardId, ticket);

        Log.d(TAG, " receive ticket auth success event!!, ticket="+ticket);
        if (null != initIMProxy) {
            initIMProxy.execute(new IntegerResultSubScriber(getResources().getString(R.string.init_im_sdk)));
        }
    }

    //群成员数量变更或者创建群组的回调
    @Subscribe
    public void onReceiveGroupSystemMessageEvent(ContactProxyEvent.GroupSystemMessageEvent groupSystemMessageEvent){
        //TODO 自定义消息入库
        Log.d(TAG, "onReceiveGroupSystemMessageEvent in");
        if (!isLogin) {
            Log.d(TAG,"isLogin false");
            return;
        }

        String talkId = groupSystemMessageEvent.getGroupId();
        String content = groupSystemMessageEvent.getMessageStr();
        TalkMessageBean talkMessageBean = new TalkMessageBean();
		//fix bug 6455 by zya,20161129
        contactService.get().getGroupInfoFromServer(talkId);
		//end
        talkMessageBean.setTo(talkId);
        talkMessageBean.setGroupMsg(true);
        talkMessageBean.setMessageType(ConstDef.MSG_TYPE_PRESENTATION);
        talkMessageBean.setContent(content);
        if (null == addCustomMsg) {
            Log.e(TAG, "addCustomMsg  case is null");
            return;
        }
        addCustomMsg.get().add(talkMessageBean).execute(new Subscriber<TalkMessageBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,  "添加自定义消息异常");
            }

            @Override
            public void onNext(TalkMessageBean talkMessageBean) {
                Log.i(TAG,  "添加自定义消息成功");
            }
        });

    }

    //发送方收到对方同意请求回调
    @Subscribe
    public void onReceiveAcceptFrientEvent(ContactProxyEvent.ReceiveAcceptFrientEvent receiveAcceptFrientEvent){
        //TODO 自定义消息入库
        if (!isLogin) {
            return;
        }
        String talkId = receiveAcceptFrientEvent.getAccount();
        String content = receiveAcceptFrientEvent.getMessage();
        TalkMessageBean talkMessageBean = new TalkMessageBean();
        talkMessageBean.setTo(talkId);
        talkMessageBean.setGroupMsg(false);
        talkMessageBean.setContent(content);

        talkMessageBean.setMessageType(ConstDef.MSG_TYPE_PRESENTATION);
        if (null == addCustomMsg) {
            Log.e(TAG, "addCustomMsg  case is null");
            return;
        }
        addCustomMsg.get().add(talkMessageBean).execute(new Subscriber<TalkMessageBean>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  Log.i(TAG,  "添加自定义消息异常");
              }

              @Override
              public void onNext(TalkMessageBean talkMessageBean) {
                  Log.i(TAG, "添加自定义消息成功");
              }
          });
    }

    //退出并解散群时需要删除本地的会话记录
    @Subscribe
    public  void onReceiveClearTalkMessage(ContactProxyEvent.QuitGroupNeedClearMessageEvent clearMessageEvent){
        //TODO 清空对应的消息
        if (!isLogin) {
            return;
        }
        String groupId = clearMessageEvent.getoupId();
        if (null == deleteSession) {
            Log.e(TAG, "addCustomMsg  case is null");
            return;
        }
        //删除会话
        deleteSession
                .get()
                .delete(groupId, ConstDef.CHAT_TYPE_P2G)
                .execute(new OkSubscriber<Integer>(okHandler));
        //发送事件
        postSessionDeleteEvent(groupId, ConstDef.CHAT_TYPE_P2G);
    }

    //解除好友关系，删除本地会话记录
    @Subscribe
    public void onReceiveDeletFriendClearTalkEvent(ContactProxyEvent.DeletFriendClearTalkEvent deletFriendClearTalkEvent){
        if (!isLogin) {
            return;
        }
        String account  = deletFriendClearTalkEvent.getAccount();
        final String talkFlag= ToolUtil.getSessionTag(account, ConstDef.CHAT_TYPE_P2P);
        //start fix bug 5487 by licong, reView by zya, 2016/11/3
        if (!contactService.get().isExistDepartment(account)) {
            if (null != deleteSession) {
                //删除会话
                deleteSession
                        .get()
                        .delete(account, ConstDef.CHAT_TYPE_P2P)
                        .execute(new OkSubscriber<Integer>(okHandler) {
                            @Override
                            public void onNext(Integer integer) {
                                super.onNext(integer);

                               if (null != clearAllMsgInTalk) {
                                    //清空消息
                                    clearAllMsgInTalk
                                            .get()
                                            .clear(talkFlag)
                                            .execute(new OkSubscriber<Integer>(okHandler));
                                }
                            }
                        });
            }
            //end fix bug 5487 by licong, reView by zya, 2016/11/3
/*

            if (null != deleteSingleSessionConfig) {
                //删除会话设置
                deleteSingleSessionConfig
                        .get()
                        .delete(ToolUtils.getSessionTag(account, ConstDef.CHAT_TYPE_P2P))
                        .execute(new OkSubscriber<Boolean>(okHandler));
            }
*/

           //[S] fix bug by licong, 2016/11/9
            deleteTopSetting.get()
                    .delete(talkFlag,true)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            deleteNoDisturbSetting
                                    .get()
                                    .delete(talkFlag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                                    .execute(new OkSubscriber<Boolean>(null) {
                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            super.onNext(aBoolean);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            LogUtil.getUtils().e(this.getClass().getSimpleName() +
                                                    "onReceiveDeletFriendClearTalkEvent delete nodisturb error" +
                                                    Arrays.toString(e.getStackTrace()));
                                        }
                                    });

                        }
                    });
            //[E] fix bug by licong, 2016/11/9
            postSessionDeleteEvent(account, ConstDef.CHAT_TYPE_P2P);
        } else {
            // add by zya@xdja.com,20161011,fix bug 4392
            //删除是集团通讯录成员的好友
            IMProxyEvent.DeleteTalkEventWithDepartment talkEventWithOutDepartment =
                    new IMProxyEvent.DeleteTalkEventWithDepartment();
            if(null != busProvider) {
                busProvider.post(talkEventWithOutDepartment);
            }//end by zya@xdja.com
        }
    }

    @Subscribe
    public void onClearAllMessageInTalk(SessionChangedEvent.MessageCleardEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onClearAllMessageInTalk--------");
        LogUtil.getUtils().d("onClearAllMessageInTalk");
    }

    @Subscribe
    public void refreshSingleTalk(final IMProxyEvent.RefreshSingleTalkEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------refreshSingleTalk--------");
        LogUtil.getUtils().d("refreshSingleTalk");
    }

    @Subscribe
    public void onDeleteTalk(IMProxyEvent.DeleteTalkEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onDeledteTalk--------");
        LogUtil.getUtils().d("onDeleteTalk");
    }

    @Subscribe
    public void onCreateNewTalk(final IMProxyEvent.CreateNewTalkEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onCreateNewTalk--------");
        LogUtil.getUtils().d("onCreateNewTalk");
    }


    @Subscribe
    public void getSingleDistrub(final IMProxyEvent.GetSingleListBeanDisturb event) {
        if (event == null) {
            return;
        }
        final List<TalkMessageBean> msgBeanList = event.getTalkMessageBeansList();
        final TalkListBean listBean = event.getListBean();

        if (listBean == null || msgBeanList == null) {
            return;
        }

        final ContactInfo contactInfo;
        if (listBean.getTalkType() == ConstDef.CHAT_TYPE_P2G) {
            contactInfo = contactService.get().getGroupInfo(listBean.getTalkerAccount());
        } else {
            contactInfo = contactService.get().getContactInfo(listBean.getTalkerAccount());
        }

        getSingleSession
                .get()
                .get(ToolUtil.getSessionTag(listBean.getTalkerAccount(), listBean.getTalkType()))
                .execute(new Subscriber<SessionConfig>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e("gggbbbccc , " + e);
                    }

                    @Override
                    public void onNext(SessionConfig sessionConfig) {
                        //传入当前聊天对象的talkId
                        //start fix bug 4544 by licong , reViIdew by zya, 2016/9/28
                        NotificationUtil.getInstance(getApplicationContext()).remindMessage(contactInfo,
                                sessionConfig,
                                listBean,
                                msgBeanList,
                                userCache.getIMPartener());
                        //end fix bug 4544 by licong , reView by zya, 2016/9/28
                    }
                });

    }



    @Subscribe
    public void onInitFinished(IMProxyEvent.OnInitFinishedEvent event) {
        LogUtil.getUtils().e(this.getClass().getSimpleName() + "--------onInitFinished--------");
        if (null == callBackRegist) {
            return;
        }
        callBackRegist
                .registAllCallBack()
                .execute(
                        new IntegerResultSubScriber(getResources().getString(R.string.work_callback_init)) {
                            @Override
                            public void onNext(Integer integer) {
                                super.onNext(integer);
                                LogUtil.getUtils().e("callBackRegist init end isLogin = " + isLogin +",  isRun = "+isRun);
                                if (!isRun) {
                                    return;
                                }
                                isLogin = true;
                                if (mUiCallback != null) {
                                    mUiCallback.onInitFinished();
                                }
                            }
                        }
                );
    }

}
