package com.xdja.imp.presenter.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.CloseAppEvent;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.event.UpdateContactShowNameEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.persistent.PreferencesUtil;
import com.xdja.imp.data.persistent.PropertyUtil;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.interactor.def.AddCustomTalk;
import com.xdja.imp.domain.interactor.def.CallBackRegist;
import com.xdja.imp.domain.interactor.def.DeleteSession;
import com.xdja.imp.domain.interactor.def.DeleteTopSetting;
import com.xdja.imp.domain.interactor.def.GetAllMissedCount;
import com.xdja.imp.domain.interactor.def.GetSessionConfigs;
import com.xdja.imp.domain.interactor.def.GetSessionList;
import com.xdja.imp.domain.interactor.def.GetSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.InitIMProxy;
import com.xdja.imp.domain.interactor.def.MatchSessionConfig;
import com.xdja.imp.domain.interactor.def.QueryUserAccount;
import com.xdja.imp.domain.interactor.def.SaveTopSetting;
import com.xdja.imp.domain.interactor.def.SendMessage;
import com.xdja.imp.domain.interactor.def.SynchronizationService;
import com.xdja.imp.domain.interactor.im.AddCustomMsgUseCase;
import com.xdja.imp.domain.interactor.mx.SaveDraftUseCase;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.event.SessionChangedEvent;
import com.xdja.imp.frame.imp.presenter.IMFragmentPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.activity.AnTongTeamOperationPresenter;
import com.xdja.imp.presenter.activity.ChatDetailActivity;
import com.xdja.imp.presenter.adapter.ChatListAdapterPresenter;
import com.xdja.imp.presenter.command.ChatListCommand;
import com.xdja.imp.receiver.NetworkStateEvent;
import com.xdja.imp.service.SimcUiService;
import com.xdja.imp.ui.ViewChatList;
import com.xdja.imp.ui.vu.ChatListVu;
import com.xdja.imp.util.Functions;
import com.xdja.imp.util.XToast;
import com.xdja.imp.widget.ChatListPopupWindow;
import com.xdja.proxy.imp.MxModuleProxyImp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;

/**
 * <p>Summary:会话列表</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.fragment</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:15:03</p>
 */
public class ChatListFragmentPresenter extends IMFragmentPresenter<ChatListCommand, ChatListVu>
        implements ChatListCommand,
            SimcUiService.ImUICallback,
            ChatListPopupWindow.PopupWindowEvent<TalkListBean> {
    //IMSDK初始化
    @Inject
    InitIMProxy initIMProxy;

    @Inject
    PropertyUtil propertyUtil;

    //获取会话列表
    @Inject
    GetSessionList getSessionList;

    //注册回调句柄
    @Inject
    CallBackRegist callBackRegist;

    //事件总线
    @Inject
    BusProvider busProvider;

    //用户信息
    @Inject
    UserCache userCache;

    //安全卡信息
    @Inject
    CardCache cardCache;

    //获取会话配置
    @Inject
    Lazy<GetSessionConfigs> getSessionConfigs;

    //发送消息
    @Inject
    Lazy<SendMessage> sendMessage;

    //获取单个会话配置
    @Inject
    Lazy<GetSingleSessionConfig> getSingleSessionConfig;

    @Inject
    //匹配会话配置
    Lazy<MatchSessionConfig> matchSessionConfig;

    //删除会话
    @Inject
    Lazy<DeleteSession> deleteSession;
    @Inject
    Lazy<SaveDraftUseCase> saveDraft;
    @Inject
    Lazy<DeleteTopSetting> deleteTopSetting;

    @Inject
    Lazy<SaveTopSetting> saveTopSetting;

    @Inject
    Lazy<AddCustomTalk> addCustomSessionLazy;

    @Inject
    Lazy<GetAllMissedCount> getAllMissedCountLazy;

    //会话集合
    private final List<TalkListBean> dataSource = new ArrayList<>();

    //会话适配器
    private ChatListAdapterPresenter adapterPresenter = null;

    @Inject
    Lazy<QueryUserAccount> queryUserAccount;


    @Inject
    Lazy<SynchronizationService> serviceLazy;

    @Inject
    AddCustomMsgUseCase addCustomMsgUseCase;

    private int unReadCount = 0;//未读消息数量

    //自定义安通+团队会话账号
    private static final int CLEARFALG_SUCCESS = 1;  //删除会话，成功
    private static final int CLEARFALG_FAIL = 2;     //删除会话，失败

    private MyHandler handler = new MyHandler(new WeakReference<>(this));


    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        LogUtil.getUtils().i("onBindView");

        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        //初始化注入
        useCaseComponent.inject(this);
        //初始化事件总线
        busProvider.register(this);
		//sync message session load action 20160723
        SimcUiService.addUiCallback(this);


        //  加载配置文件 (异常之后重新登录)
        if (ConstDef.PRONAME.length() <= 0) {
            queryUserAccount.get().getUseAccount().execute(new OkSubscriber<String>(null) {
                @Override
                public void onNext(String Str) {
                    super.onNext(Str);
                    // 异常终止后重新初始化SimcUiService和IMSDK
                    MxModuleProxyImp.getInstance().
                            startMXService(ActomaController.getApp().getApplicationContext(), "", "", "");
//                    initIMProxy.execute(new IntegerResultSubScriber("初始化IMSDK"));
                }
            });
        }

        //初始化适配器
        adapterPresenter = new ChatListAdapterPresenter(dataSource, busProvider);
        this.useCaseComponent.inject(adapterPresenter);
        //设置适配器绑定的单个项
        adapterPresenter.setListView(getVu().getDisplayList());
        //设置适配器绑定的Activity
        adapterPresenter.setActivity(getActivity());
        //初始化ListView
        getVu().initListView(adapterPresenter);
        //加载个人图像，提高进入会话详情界面后展示个人图像的速度
        getVu().loadSelfImage();
		//sync message session load action 20160723
        LogUtil.getUtils().d("isLogin = " + SimcUiService.isLogin());
        if (SimcUiService.isLogin()) {
            //fix bug by licong, review by zya,2016/8/9
            startLoadSessions(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceLazy.get().execute(new OkSubscriber<Integer>(null){
            @Override
            public void onNext(Integer integer) {
                super.onNext(integer);
                LogUtil.getUtils().d("网络加载成功");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
		//sync message session load action 20160723
        SimcUiService.removeUiCallback(this);
        //add by zya@xdja.com,fix bug 1834
        if (null != adapterPresenter) {
            adapterPresenter.unregister();
        }
        if (null != busProvider) {
            busProvider.unregister(this);
        }
        getVu().dismissPopuDialog();
    }

    private void startLoadSessions(boolean deleteAllMsg) {
        int count = 1;
        final PreferencesUtil preferenceUtil = new PreferencesUtil(getActivity().getApplicationContext());
        //TODO jff 4.15修改
        boolean hasLoadAntongMessage = preferenceUtil.gPrefBooleanValue(ConstDef.HAS_LOAD_ANTONG_MSG, false);
        //fix bug by licong, review by zya,2016/8/9
        if (deleteAllMsg) {
            TalkListBean tBean = new TalkListBean();
            tBean.setTalkFlag("-10000_100");
            count = dataSource.get(dataSource.indexOf(tBean)).getNotReadCount();
        }//end

        if (!hasLoadAntongMessage) {

            TalkListBean talkListBean = new TalkListBean();
            String antongTeamSession = "-10000";
            talkListBean.setTalkerAccount(antongTeamSession);
            //todo gbc
            String aboutAtUrl = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl") + "team.html";
            talkListBean.setContent(aboutAtUrl);
            //talkListBean.setContent("http://pages.test.safecenter.com/team.html$安通+是什么?");
            talkListBean.setNotReadCount(count);
            talkListBean.setTalkType(ConstDef.CHAT_TYPE_ACTOMA);
            talkListBean.setLastMsg(null);
//            talkListBean.setTime(System.currentTimeMillis());

            addCustomSessionLazy.get().add(talkListBean).execute(new OkSubscriber<TalkListBean>(this.okHandler) {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    //super.onError(e);
                    LogUtil.getUtils().e("增加自定义会话结果失败:" + e);
                }

                @Override
                public void onNext(TalkListBean talkListBean1) {
                    super.onNext(talkListBean1);
//                    LogUtil.getUtils().d("增加自定义会话结果:" + talkListBean1.toString());

                    preferenceUtil.setPreferenceBooleanValue(ConstDef.HAS_LOAD_ANTONG_MSG, true);

                    getSessionList
                            .setParam("", 0)
                            .execute(new GetSessionListSubscriber());
                }
            });

        } else {
            getSessionList
                    .setParam("", 0)
                    .execute(new GetSessionListSubscriber());
        }
    }

    @NonNull
    @Override
    protected Class<? extends ChatListVu> getVuClass() {
        return ViewChatList.class;
    }

    @NonNull
    @Override
    protected ChatListCommand getCommand() {
        return this;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onListItemClick(int position) {
        TalkListBean bean = dataSource.get(position);
        LogUtil.getUtils().d("ChatListFragmentPresenter position = " + position);

        if (bean != null) {
            Intent intent;
            switch (bean.getTalkType()) {
                case ConstDef.CHAT_TYPE_ACTOMA:
                    intent = new Intent(getActivity(), AnTongTeamOperationPresenter.class);
                    intent.putExtra(ConstDef.TAG_TALKERID, bean.getTalkerAccount());
                    intent.putExtra(ConstDef.AN_TONG_NOTIFICATION_URL, bean.getContent());
                    startActivity(intent);
                    break;
                default:
                    if(ConstDef.CHAT_TYPE_P2P == bean.getTalkType()){
                        ContactUtils.startFriendTalk(getActivity(),bean.getTalkerAccount());
                    }else if(ConstDef.CHAT_TYPE_P2G == bean.getTalkType()){
                        ContactUtils.startGroupTalk(getActivity(),bean.getTalkerAccount());
                    }else{
                        intent = new Intent(getActivity(), ChatDetailActivity.class);
                        intent.putExtra(ConstDef.TAG_TALKERID, bean.getTalkerAccount());
                        intent.putExtra(ConstDef.TAG_TALKTYPE, bean.getTalkType());
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onListItemLongClick(int position) {
        LogUtil.getUtils().e("onListItemLongClick");
        TalkListBean listBean =
                dataSource.get(position);
        getVu().popuOptionWindow(listBean, this);
        return true;
    }

    @Override
    public void deleteSession(final TalkListBean toDeleteList) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(toDeleteList.getTalkFlag());
        getVu().showProgressDialog(getResources().getString(R.string.common_clean_all_chat_messages_progress_text));
        //删除会话
        deleteSession
                .get()
                .delete(ids)
                .execute(new DeleteSessionSubscriber(toDeleteList));
        //start: add by ycm for delete single draft 2016/9/13
        if (toDeleteList.isHasDraft()) {
            saveDraft
                    .get()
                    .save(toDeleteList.getTalkFlag(), "", 0L)
                    .execute(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.getUtils().d(e.toString());
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {

                        }
                    });
        }
        //end: add by ycm for delete single draft 2016/9/13
    }

    @Override
    public void settingTop(final TalkListBean bean) {
        if (bean == null) return;
        //start fix bug 3807 by licong, reView by zya, 2016/9/29
        if (!Functions.isAnyNetworkConnected(getActivity())) {
            new XToast(getActivity()).display(R.string.network_unable);
            return;
        }//end fix bug 3807 by licong, reView by zya, 2016/9/29


        saveTopSetting
                .get()
                .save(bean.getTalkFlag(), !bean.isShowOnTop())
                .execute(
                        new OkSubscriber<Boolean>(this.okHandler) {
                            @Override
                            public void onNext(Boolean aBoolean) {
                                super.onNext(aBoolean);
//                                LogUtil.getUtils().d("设置置顶" + (aBoolean ? "成功" : "失败"));
                                bean.setShowOnTopTime(System.currentTimeMillis());
                                bean.setShowOnTop(!bean.isShowOnTop());
                                adapterPresenter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.getUtils().d(e.toString());
                            }
                        }
                );
    }

    //fix bug 3166 by licong, reView by zya, 2016/8/23
    //取消置顶
    @Override
    public void deleteTop(final TalkListBean bean) {
        if (bean == null) return;
        //start fix bug 3807 by licong, reView by zya, 2016/9/29
        if (!Functions.isAnyNetworkConnected(getActivity())) {
            new XToast(getActivity()).display(R.string.network_unable);
            return;
        }//end fix bug 3807 by licong, reView by zya, 2016/9/29

        deleteTopSetting
                .get()
                .delete(bean.getTalkFlag(), !bean.isShowOnTop())
                .execute(new OkSubscriber<Boolean>(this.okHandler) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        super.onNext(aBoolean);

                        //发送更新事件
                        bean.setShowOnTopTime(System.currentTimeMillis());
                        bean.setShowOnTop(!bean.isShowOnTop());
                        adapterPresenter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().d(e.toString());
                    }
                });
    }


    @Override
    public boolean isShowTop(TalkListBean dataSource) {
        return dataSource != null && dataSource.isShowOnTop();
    }

    //add by zya@xdja.com.安通+退出操作;20160804,view by gr
    @Subscribe
    public void onReceiveCloseEvent(CloseAppEvent event){
        //需要执行的代码
        LogUtil.getUtils().i("zhu->onReceiveCloseEvent");
        MxModuleProxyImp.getInstance().stopService(ActomaController.getApp().getApplicationContext());
    }//end


    private static class MyHandler extends Handler {
        private final WeakReference<ChatListFragmentPresenter> mActivity;
        public  MyHandler(WeakReference<ChatListFragmentPresenter> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatListFragmentPresenter activity = mActivity.get();
            if (activity != null) {
                int code = msg.what;
                if (code == CLEARFALG_SUCCESS) {
                    LogUtil.getUtils().d("code : CLEARFALG_SUCCESS");
                } else if (code == CLEARFALG_FAIL) {
                    Toast.makeText(activity.getActivity(), R.string.clearFail, Toast.LENGTH_SHORT).show();
                }

                activity.getVu().dismissDialog();
            }
        }

    }
    /*---------------------------- 一下代码用于处理事件总线收到的事件 ------------------------------*/
    @Subscribe
    public void onCreateNewTalk(final IMProxyEvent.CreateNewTalkEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onCreateNewTalk--------");
        this.getActivity().runOnUiThread(new CreateNewTalkRunnable(event));
    }

    @Subscribe
    public void onDeleteTalk(IMProxyEvent.DeleteTalkEvent event) {
        //LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onDeledteTalk--------");
        final TalkListBean talk = event.getTalkListBean();

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doRemoveFromDataSource(talk);
            }
        });
    }

    //add by zya@xdja.com,20161011,fix bug 4392
    @Subscribe
    public void onDeleteTalkWithDepartment(IMProxyEvent.DeleteTalkEventWithDepartment event) {
        //LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onDeleteTalkWithDepartment--------");
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterPresenter.notifyDataSetChanged();
            }
        });
    }//end by zya@xdja.com

    @Subscribe
    public void refreshSingleTalk(final IMProxyEvent.RefreshSingleTalkEvent event) {
        //LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------refreshSingleTalk--------");
        this.getActivity().runOnUiThread(new RefreshSingleTalkRunnable(event));
    }

    @Subscribe
    public void refreshTalkList(IMProxyEvent.RefreshTalkListEvent event) {
        //LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------refreshTalkList--------");
        //fix bug by licong, review by zya,2016/8/9
        startLoadSessions(event.isDeleteAllMsg());
    }

    @Subscribe
    public void onChangeTalkTopState(SessionChangedEvent.TopStateChangedEvent event) {
        //LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onChangeTalkTopState--------");
        if (event == null) return;
        String id = event.getFlag();

        TalkListBean target = findTalkById(id);

        if (target != null) {
            target.setShowOnTop(event.isTop());
            target.setShowOnTopTime(System.currentTimeMillis());
            adapterPresenter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onChangeTalkNotifyState(SessionChangedEvent.NodisturbStateChangedEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onChangeTalkNotifyState--------");

        if (event == null) return;
        String id = event.getFlag();

        TalkListBean target = findTalkById(id);

        if (target != null) {
            target.setNewMessageIsNotify(!event.isNoDisturb());
            adapterPresenter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onClearAllMessageInTalk(SessionChangedEvent.MessageCleardEvent event) {
//        LogUtil.getUtils().d(this.getClass().getSimpleName() + "--------onClearAllMessageInTalk--------");

        if (event == null) return;
        String id = event.getFlag();

        TalkListBean target = findTalkById(id);

        if (target != null) {
            target.setContent("");
            target.setLastMsgType(0);
            target.setLastMsg(null);
            target.setNotReadCount(0);
            Toast.makeText(getActivity(), getResources().getString(R.string.chat_settings_clean_all_chat_messages_text_success), Toast.LENGTH_LONG).show();
            adapterPresenter.notifyDataSetChanged();
            getAllMissedCount();
        }
    }

    //add by zya@xdja.com,fix bug 2044.review by guorong,20160905
    //同意好友请求
    @Subscribe
    public void ReceiveAcceiptFriend(ContactProxyEvent.AcceptFriendEvent acceptFriendEvent){

        String content = acceptFriendEvent.getMessageStr();
        String talkId = acceptFriendEvent.getAccount();
        final String flag = ToolUtil.getSessionTag(talkId, ConstDef.CHAT_TYPE_P2P);

        TalkMessageBean talkMessageBean = new TalkMessageBean();
        talkMessageBean.setTo(talkId);
        talkMessageBean.setGroupMsg(false);
        talkMessageBean.setContent(content);

        talkMessageBean.setMessageType(ConstDef.MSG_TYPE_PRESENTATION);
        addCustomMsgUseCase.add(talkMessageBean).execute(new Subscriber<TalkMessageBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().i("添加自定义消息异常");
            }

            @Override
            public void onNext(TalkMessageBean talkMessageBean) {
                LogUtil.getUtils().i("添加自定义消息成功");
                //adapterPresenter.refreshItem(flag,ConstDef.CHAT_TYPE_P2P);
            }
        });
    }//end

    @Subscribe
    public void NetworkStateChange(NetworkStateEvent event){
        getVu().changeViewSate(event.getState());
    }

    @Subscribe
    public void GroupInfoUpdate(ContactProxyEvent.GetGroupInfoEvent getGroupInfoEvent){
        String groupId = getGroupInfoEvent.getoupId();
        String flag = ToolUtil.getSessionTag(groupId, ConstDef.CHAT_TYPE_P2G);
        //刷新对应的会话列表
        adapterPresenter.refreshItem(flag, ConstDef.CHAT_TYPE_P2G);
    }

    //群组更新
    @Subscribe
    public  void GroupMemberUpdate(ContactProxyEvent.GroupUpdateEvent groupUpdateEvent){
        String groupId = groupUpdateEvent.getoupId();
        String flag = ToolUtil.getSessionTag(groupId, ConstDef.CHAT_TYPE_P2G);
        //刷新对应的会话列表
        adapterPresenter.refreshItem(flag, ConstDef.CHAT_TYPE_P2G);
    }

    /*//同意好友请求
    @Subscribe
    public void ReceiveAcceiptFriend(ContactProxyEvent.AcceptFriendEvent acceptFriendEvent){
        String accountId = acceptFriendEvent.getAccount();
        String flag = ToolUtil.getSessionTag(accountId, ConstDef.CHAT_TYPE_P2P);

        //刷新对应的会话列表
        adapterPresenter.refreshItem(flag,ConstDef.CHAT_TYPE_P2P);
    }*/

    //备注更新回调事件分发
    @Subscribe
    public void ReceiveRemarkUpdateEvent(ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent){
        String account = remarkUpdateEvent.getAccount();
        //String showName = remarkUpdateEvent.getShowName();
        //刷新对应的会话列表

        adapterPresenter.refreshItem(ToolUtil.getSessionTag(account,ConstDef.CHAT_TYPE_P2P),ConstDef.CHAT_TYPE_P2P);
        adapterPresenter.refreshItem(ToolUtil.getSessionTag(account,ConstDef.CHAT_TYPE_P2G),ConstDef.CHAT_TYPE_P2G);
    }

    @Subscribe
    public void ReceiveNickNameUpdateEvent(ContactProxyEvent.NickNameUpdateEvent nickNameUpdateEvent){
        // 刷新对应的会话列表
        adapterPresenter.notifyDataSetChanged();
    }

    @Subscribe
    public void ReceiveNickNameUpdateEvent(UpdateContactShowNameEvent nickNameUpdateEvent){
        // 刷新对应的会话列表
        adapterPresenter.notifyDataSetChanged();
    }

    private TalkListBean findTalkById(String flag) {
        if (dataSource.isEmpty()) {
            return null;
        }

        TalkListBean listBean;
        for (int i = 0; i < dataSource.size(); i++) {
            listBean = dataSource.get(i);
            if (listBean != null && listBean.getTalkFlag().equalsIgnoreCase(flag)) {
                return listBean;
            }
        }

        return null;
    }

    private void doRemoveFromDataSource(final TalkListBean bean) {
        if (dataSource.isEmpty() || bean == null) return;

        int targetPosition = -1;
        for (int i = 0; i < dataSource.size(); i++) {
            if (dataSource.get(i) == null) continue;
            String flag = dataSource.get(i).getTalkFlag();
            if (flag.equals(bean.getTalkFlag())) {
                targetPosition = i;
                break;
            }
        }

        if (targetPosition >= 0 && dataSource.size() > targetPosition) {
            dataSource.remove(targetPosition);
            adapterPresenter.notifyDataSetChanged();
            getAllMissedCount();
        }
    }

    private void getAllMissedCount() {
        int unReadCount = 0;
        if(dataSource.size()>0){
            for(int i=0;i<dataSource.size();i++){
                final TalkListBean listBean=dataSource.get(i);
                if(listBean.isNewMessageIsNotify()){
                    unReadCount+=listBean.getNotReadCount();
                }
            }
        }
        refreshTabNoReadCount(unReadCount);
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
        this.busProvider.post(event);
    }

    @Override
    public void onInitFinished() {
        //fix bug by licong, review by zya,2016/8/9
        startLoadSessions(false);
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

    /**
     * 获取会话配置结果监听
     */
    class GetSessionConfigsSubscriber extends OkSubscriber<List<SessionConfig>> {

        public GetSessionConfigsSubscriber() {
            super(okHandler);
        }

        @Override
        public void onNext(List<SessionConfig> configs) {
            super.onNext(configs);
            if (configs != null) {
                //start: add by ycm for bug 2619  review by liming //
                int configListSize = configs.size();
                for (int i = 0; i < configListSize; i++) {
                    sessionConfig2TalkListBean(configs.get(i));
                }
                //end: add by ycm for bug 2619  review by liming //
                //fix bug 3821 by licong, reView by zya,2016/9/9
                adapterPresenter.notifyDataSetChanged();

                matchSessionConfig
                        .get()
                        .setConfigs(configs, dataSource)
                        .execute(
                                new OkSubscriber<List<TalkListBean>>(okHandler) {
                                    @Override
                                    public void onNext(List<TalkListBean> talkListBeen) {
                                        super.onNext(talkListBeen);
                                        dataSource.clear();
                                        if (talkListBeen != null && !talkListBeen.isEmpty()) {
                                            dataSource.addAll(talkListBeen);
                                        }

                                        //add by zya@xdja.com bug NACTOMA-338
                                        LogUtil.getUtils().i("GetSessionConfigsSubscriber->bean:" + dataSource.toString());

                                        adapterPresenter.notifyDataSetChanged();
//                                        LogUtil.getUtils().d("获取会话配置之后大小为：" + dataSource.size());
                                        getAllMissedCount();
                                    }
                                }
                        );


            } else {
                LogUtil.getUtils().d("获取到会话配置为空");
                getAllMissedCount();
            }
        }
        //start: add by ycm for bug 2619  review by liming //
        private void sessionConfig2TalkListBean(SessionConfig config){
            TalkListBean talkListBean = new TalkListBean();
            talkListBean.setTalkFlag(config.getFlag());
            if (!dataSource.contains(talkListBean)) {
                if (!TextUtils.isEmpty((config.getDraft())) ) {
                    String account = config.getFlag().split("_")[0];
                    talkListBean.setTalkerAccount(account);
                    talkListBean.setTalkFlag(config.getFlag());
                    talkListBean.setDraftTime(config.getDraftTime());
                    talkListBean.setHasDraft(true);
                    talkListBean.setDraft(config.getDraft());
                    talkListBean.setNotReadCount(0);
                    talkListBean.setShowOnTop(config.isTop());
                    talkListBean.setNewMessageIsNotify(config.isNoDisturb());
                    talkListBean.setTalkerAccount(account);
                    talkListBean.setTalkType(Integer.parseInt(config.getFlag().split("_")[1]));
                    dataSource.add(talkListBean);
                }
            }
        }
        //end: add by ycm for bug 2619  review by liming //

        @Override
        public void onError(Throwable e) {
            //super.onError(e);
            LogUtil.getUtils().e("获取会话配置发生错误，错误信息：" + e.getMessage());
        }
    }

    /**
     * 获取会话列表结果监听
     */
    class GetSessionListSubscriber extends OkSubscriber<List<TalkListBean>> {
        public GetSessionListSubscriber() {
            super(okHandler);
        }

        @Override
        public void onError(Throwable e) {
            //super.onError(e);
            LogUtil.getUtils().e("获取会话列表发生错误，错误信息：" + e.getMessage());
        }

        @Override
        public void onNext(List<TalkListBean> talkListBean) {
            super.onNext(talkListBean);
            if (talkListBean == null || talkListBean.isEmpty()) {
                LogUtil.getUtils().i("获取到的talkList为空");

                dataSource.clear();
                adapterPresenter.notifyDataSetChanged();

            } else {
                LogUtil.getUtils().e("GetSessionListSubscriber->获取到的talkList大小为" + talkListBean.size());


                for (TalkListBean bean : talkListBean) {
                    if (bean != null && bean.getTalkType() == ConstDef.CHAT_TYPE_ACTOMA) {
                        String aboutAtUrl = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl") + "team.html";
                        bean.setContent(aboutAtUrl + getResources().getString(R.string.antong_text_content));
                        //bean.setContent("http://pages.test.safecenter.com/team.html$安通+是什么?");
                    }
                    //add by zya@xdja.com bug NACTOMA-338
                    //LogUtil.getUtils().i("GetSessionListSubscriber->bean:" + bean.toString());

                }

                dataSource.clear();
                dataSource.addAll(talkListBean);
                adapterPresenter.notifyDataSetChanged();
            }
            getSessionConfigs.get().execute(new GetSessionConfigsSubscriber());
        }
    }

    /**
     * 删除会话监听
     */
    class DeleteSessionSubscriber extends IntegerResultSubScriber {

        private final TalkListBean bean;

        public DeleteSessionSubscriber(TalkListBean bean) {
            super(getResources().getString(R.string.delete_chat_list));
            this.bean = bean;
        }

        @Override
        public void onNext(Integer integer) {
            super.onNext(integer);

            doRemoveFromDataSource(bean);
            getAllMissedCount();

            handler.sendEmptyMessageDelayed(CLEARFALG_SUCCESS, 0);
        }
    }

    /**
     * 获取单个会话的配置监听
     */
    class CreateNewTalkSubscriber extends OkSubscriber<SessionConfig> {

        final TalkListBean talkListBean;

        public CreateNewTalkSubscriber(TalkListBean talkListBean) {
            super(okHandler);
            this.talkListBean = talkListBean;
        }

        @Override
        public void onNext(SessionConfig sessionConfig) {
            super.onNext(sessionConfig);
            if (sessionConfig != null) {
                //会话相关设置信息
                this.talkListBean.setNewMessageIsNotify(
                        !sessionConfig.isNoDisturb()
                );
                if (!TextUtils.isEmpty(sessionConfig.getDraft())) {
                    this.talkListBean.setDraft(sessionConfig.getDraft());
                    this.talkListBean.setHasDraft(true);
                    this.talkListBean.setDraftTime(sessionConfig.getDraftTime());
                } else
                    this.talkListBean.setHasDraft(false);
                //fix bug 1852 by licong,review by zya,2016/8/5
                if (sessionConfig.isTop()) {
                    this.talkListBean.setShowOnTop(sessionConfig.isTop());
                    this.talkListBean.setShowOnTopTime(System.currentTimeMillis());
                }//end
            }
            //fix bug 1627 by zya@xdja.com
            int index = dataSource.indexOf(talkListBean);
            if(index < 0){
                 dataSource.add(this.talkListBean);
            } else {
                dataSource.set(index,talkListBean);
            }//end
            adapterPresenter.notifyDataSetChanged();
            getAllMissedCount();
        }

        @Override
        public void onError(Throwable e) {
            //super.onError(e);
            LogUtil.getUtils().e("CreateNewTalkSubscriber" + e.getMessage());
        }
    }

    class CreateNewTalkRunnable implements Runnable {

        private final IMProxyEvent.CreateNewTalkEvent event;

        public CreateNewTalkRunnable(IMProxyEvent.CreateNewTalkEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event == null) return;

            final TalkListBean talkListBean = event.getTalkListBean();
            if (talkListBean == null) return;

            if(dataSource.contains(talkListBean)) return;

            String talkFlag = talkListBean.getTalkFlag();
            if (!TextUtils.isEmpty(talkFlag)) {
                getSingleSessionConfig
                        .get()
                        .get(talkFlag)
                        .execute(new CreateNewTalkSubscriber(talkListBean));
            }
        }
    }


    class RefreshSingleTalkRunnable implements Runnable {

        private final IMProxyEvent.RefreshSingleTalkEvent event;

        public RefreshSingleTalkRunnable(IMProxyEvent.RefreshSingleTalkEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event == null) return;

            TalkListBean talkListBean = event.getTalkListBean();
            if (talkListBean == null) {
                return;
            }
            //LogUtil.getUtils().d("======talkListBean= "+talkListBean.toString());

            String talkFlag = talkListBean.getTalkFlag();
            if (dataSource.isEmpty()) return;

            if(!dataSource.contains(talkListBean)){//dataSource中不包含该TalkListBean则创建新会话
                                       // TalkListBean中重写了equals方法，根据talkFlag判断是否为同一个
                if (!TextUtils.isEmpty(talkFlag)) {
                    getSingleSessionConfig
                            .get()
                            .get(talkFlag)
                            .execute(new CreateNewTalkSubscriber(talkListBean));
                }
            } else {//dataSource中包含该TalkListBean则刷新会话内容

                //根据talkFlag从dataSource中找到TalkListBean更新其属性值
                final TalkListBean listBean = findTalkById(talkFlag);

                if(listBean == null) return;

                if(talkListBean.getLastMsg() == null &&
                        TextUtils.isEmpty(talkListBean.getContent())){
                    //最后一条消息为空且内容为空的情况不更新时间

                    //修改草稿排序问题 by licong ,2016/9/5
                    listBean.setDraftTime(listBean.getDraftTime());
                }else {
                    listBean.setLastTime(talkListBean.getLastTime());
                }

                listBean.setLastTime(talkListBean.getLastTime());
                listBean.setNotReadCount(talkListBean.getNotReadCount());
                listBean.setLastMsg(talkListBean.getLastMsg());
                listBean.setContent(talkListBean.getContent());
                listBean.setLastMsgType(talkListBean.getLastMsgType());
                listBean.setLastMsgAccount(talkListBean.getLastMsgAccount());
                getSingleSessionConfig
                        .get()
                        .get(listBean.getTalkFlag())
                        .execute(new OkSubscriber<SessionConfig>(null) {
                            @Override
                            public void onNext(SessionConfig sessionConfig) {
                                super.onNext(sessionConfig);
                                String draft = null;
                                if (sessionConfig != null) {
                                    draft = sessionConfig.getDraft();
                                }
                                if (!TextUtils.isEmpty(draft)) {
                                    listBean.setDraft(draft);
                                    listBean.setHasDraft(true);
                                    listBean.setDraftTime(sessionConfig.getDraftTime());
                                } else {
                                    listBean.setDraft(draft);
                                    listBean.setHasDraft(false);
                                }

                                //fix bug 8788 and disturb relative 20170307
                                if(sessionConfig != null){
                                    listBean.setNewMessageIsNotify(!sessionConfig.isNoDisturb());

                                    if (sessionConfig.isTop()) {
                                        listBean.setShowOnTop(sessionConfig.isTop());
                                        listBean.setShowOnTopTime(System.currentTimeMillis());
                                    }
                                }
                                //end by zya
                                adapterPresenter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.getUtils().d(e.toString());
                            }
                        });
                getAllMissedCount();
            }
        }
    }

}
