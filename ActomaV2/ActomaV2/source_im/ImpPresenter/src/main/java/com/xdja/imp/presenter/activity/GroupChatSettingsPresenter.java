package com.xdja.imp.presenter.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.interactor.def.AddNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.ClearAllMsgInTalk;
import com.xdja.imp.domain.interactor.def.DeleteNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.DeleteSession;
import com.xdja.imp.domain.interactor.def.DeleteTopSetting;
import com.xdja.imp.domain.interactor.def.GetSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.SaveTopSetting;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.event.SessionChangedEvent;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.messageNotification.ChatNotifiSetedCache;
import com.xdja.imp.presenter.command.GroupChatSettingsCommand;
import com.xdja.imp.ui.ViewGroupChatSettings;
import com.xdja.imp.ui.vu.GroupChatSettingsVu;
import com.xdja.xutils.util.LogUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.Lazy;


/**
 * Created by cxp on 2015/7/27.
 */
public class GroupChatSettingsPresenter extends IMActivityPresenter<GroupChatSettingsCommand, GroupChatSettingsVu> implements GroupChatSettingsCommand {
    private String groupSessionFlag; //群会话标识， 群组账号_2
    private String groupId;  //群组账号
    private String accountName;
    private int chatType = ConstDef.CHAT_TYPE_P2G; //会话类型
    private static final int CLEARFALG_SUCCESS = 1;
    private static final int CLEARFALG_FAIL = 2;
    private static final int EXIT_GROUP_SUCCESS = 3;
    private static final int EXIT_GROUP_FAIL = 4;

    private boolean isShowOnTop = false;//是否置顶显示
    private boolean isNoDisturb = false;//是否消息免打扰

    //[S] fix bug 4235 by licong, reView by zya,2016/11/8
    private boolean isTop = false;
    //[E] fix bug 4235 by licong, reView by zya,2016/11/8


    @Inject
    ContactService contactService;

    @Inject
    Lazy<SaveTopSetting> saveTopSetting;

    @Inject
    Lazy<DeleteTopSetting> deleteTopSetting;

    @Inject
    Lazy<AddNoDisturbSetting> addNoDisturbSetting;

    @Inject
    Lazy<DeleteNoDisturbSetting> deleteNoDisturbSetting;
    //删除会话
    @Inject
    Lazy<DeleteSession> deleteSession;

    @Inject
    Lazy<ClearAllMsgInTalk> clearAllMsgInTalk;

    @Inject
    GetSingleSessionConfig getSingleSessionConfig;

    @Inject
    BusProvider busProvider;

    private MyHandler handler = new MyHandler(new WeakReference<>(this));

    @NonNull
    @Override
    protected Class<? extends GroupChatSettingsVu> getVuClass() {
        return ViewGroupChatSettings.class;
    }

    @Override
    protected GroupChatSettingsCommand getCommand() {
        return this;
    }


    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        //界面初始化之前的操作
        groupSessionFlag = getIntent().getStringExtra(ConstDef.TAG_TALKFLAG);
        if (null != groupSessionFlag) {
            groupId = groupSessionFlag.substring(0, groupSessionFlag.indexOf("_"));
        }
        accountName = ConstDef.PRONAME;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        useCaseComponent.inject(this);

        busProvider.register(this);


        getVu().setExitButtonTitle(judgeIsGroupOwner());
        getSingleSessionConfig
                .get(groupSessionFlag)
                .execute(
                        new OkSubscriber<SessionConfig>(this.okHandler) {
                            @Override
                            public void onNext(SessionConfig sessionConfig) {
                                super.onNext(sessionConfig);
                                if (sessionConfig != null) {
                                    setNoDisturbCheckBoxState(sessionConfig.isNoDisturb());
                                    setTopChatCheckBoxState(sessionConfig.isTop());
                                }
                            }
                        }
                );
        initGroupChatTopLayout(this, groupId, accountName);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != busProvider){
            busProvider.unregister(this);
        }
    }

    /**
     * 更新置顶聊天状态值
     *
     * @param isTopChat
     */
    @Override
    public void updateTopChatState(final boolean isTopChat) {
        //[S] fix bug 4235 by licong, reView by zya,2016/11/8
        isTop = isTopChat;
        //[E] fix bug 4235 by licong, reView by zya,2016/11/8
        //TODO:置顶更新
        if (isTopChat) {
            saveTopSetting
                    .get()
                    .save(groupSessionFlag, isTopChat)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
//                        LogUtil.getUtils().d("设置置顶" + (aBoolean ? "成功" : "失败"));
                        }
                    });
        } else {
            //fix bug 3166 by licong, reView by zya, 2016/8/23
            //如果没有置顶，则从云端以及本地删除此条置顶会话
            deleteTopSetting.get()
                    .delete(groupSessionFlag, isTopChat)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
//                        LogUtil.getUtils().d("设置置顶" + (aBoolean ? "成功" : "失败"));
                        }
                    });
        }

        //发送更新事件
        SessionChangedEvent.TopStateChangedEvent event
                = new SessionChangedEvent.TopStateChangedEvent();
        event.setTop(isTopChat);
        event.setFlag(groupSessionFlag);
        isShowOnTop = isTopChat;
        busProvider.post(event);
    }

    /**
     * 更新设置聊天免打扰状态值
     *
     * @param isNoDisturb
     */
    @Override
    public void updateChatNoDisturbState(boolean isNoDisturb) {
        //TODO：免打扰设置
        if (isNoDisturb) {
            addNoDisturbSetting
                    .get()
                    .add(groupSessionFlag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            LogUtil.getUtils().d("增加勿扰模式" + (aBoolean ? "成功" : "失败"));
                        }

                    });
        } else {
            deleteNoDisturbSetting
                    .get()
                    .delete(groupSessionFlag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            LogUtil.getUtils().d("删除勿扰模式" + (aBoolean ? "成功" : "失败"));
                        }
                    });
        }
        ChatNotifiSetedCache.setSettingValue(groupId, !isNoDisturb);

        //发送更新事件
        SessionChangedEvent.NodisturbStateChangedEvent event
                = new SessionChangedEvent.NodisturbStateChangedEvent();
        event.setNoDisturb(isNoDisturb);
        event.setFlag(groupSessionFlag);
        this.isNoDisturb = isNoDisturb;
        busProvider.post(event);
    }


    /**
     * 获取是否置顶显示
     *
     * @return
     */
    @Override
    public boolean getIsShowOnTop() {
        return this.isShowOnTop;
    }

    /**
     * 获取是否免打扰
     *
     * @return
     */
    @Override
    public boolean getNoDisturb() {
        return this.isNoDisturb;
    }

    /**
     * 获取群组信息Fragment
     *
     * @param groupId
     * @return
     */
    @Override
    public Fragment getGroupInfoDetailManager(String groupId) {

        return contactService.getGroupInfoDetailManager(groupId);
    }

    /**
     * 清除聊天记录
     */
    @Override
    public void cleanAllGroupChatMessages() {

        getVu().showProgressDialog(getResources().getString(R.string.common_clean_all_chat_messages_progress_text));
        clearAllMsgInTalk
                .get()
                .clear(groupSessionFlag)
                .execute(new OkSubscriber<Integer>(this.okHandler) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                        LogUtil.getUtils().d("清空会话数据" + (integer == 0 ? "成功" : "失败"));

                        //发送更新事件
                        SessionChangedEvent.MessageCleardEvent event
                                = new SessionChangedEvent.MessageCleardEvent();
                        event.setFlag(groupSessionFlag);

                        busProvider.post(event);
                        //add by zya@xdja.com,20161109
                        userCache.clearCacheText();
                        //end by zya

                        handler.sendEmptyMessageDelayed(CLEARFALG_SUCCESS, 0);
                    }

                    @Override
                    public void onError(Throwable e) {//删除失败弹出提醒
                        super.onError(e);
                        /*Toast.makeText(GroupChatSettingsPresenter.this,
                                getString(R.string.failed_clean_all_chat_messages),
                                Toast.LENGTH_SHORT).show();*/
                        handler.sendEmptyMessageDelayed(CLEARFALG_FAIL, 0);
                    }
                });
    }


    private static class MyHandler extends Handler {
        private final WeakReference<GroupChatSettingsPresenter> mActivity;
        public  MyHandler(WeakReference<GroupChatSettingsPresenter> mActivity) {
            this.mActivity = mActivity;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GroupChatSettingsPresenter activity = mActivity.get();
            if (activity != null) {
                int code = msg.what;
                LogUtil.getUtils().e("contact code:" + msg.what);
                if (code == CLEARFALG_SUCCESS) {
                    LogUtil.getUtils().d("code : CLEARFALG_SUCCESS");
                } else if (code == CLEARFALG_FAIL) {
                    Toast.makeText(activity.getBaseContext(), R.string.clearFail, Toast.LENGTH_SHORT).show();
                } else if (code == EXIT_GROUP_SUCCESS) {
                    if(activity.judgeIsGroupOwner()){
                        Toast.makeText(activity.getBaseContext(), R.string.exitGroupSuccess2, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(activity.getBaseContext(), R.string.exitGroupSuccess, Toast.LENGTH_SHORT).show();
                    }
                    LogUtil.getUtils().e("contact:解散或者退出群组成功！");
                    activity.finish();
                } else if (code == EXIT_GROUP_FAIL) {
                    LogUtil.getUtils().e("contact:解散或者退出群组失败！");
                    //[s]modify by xienana for bug 5305 @20161025 [review by tangsha]
                    if(isAnyNetworkConnected(activity.getBaseContext())){
//                    Toast.makeText(getBaseContext(), R.string.network_unable, Toast.LENGTH_SHORT).show();
//                }else{
                        //[e]modify by xienana for bug 5305 @20161025 [review by tangsha]
                        if(activity.judgeIsGroupOwner()){
                            Toast.makeText(activity.getBaseContext(), R.string.exitGroupFail2, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity.getBaseContext(), R.string.exitGroupFail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                activity.getVu().dismissDialog();
            }
        }
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return true 有网络可用，false 无可用网络
     * @since 2014-3-8 weizg
     */
    private static boolean isAnyNetworkConnected(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void exitGroupChatAndDissolveGroup() {

        if(judgeIsGroupOwner()){
            getVu().showProgressDialog(getResources().getString(R.string.exiting_group));
        }else{
            getVu().showProgressDialog(getResources().getString(R.string.quit_group));
        }

        //退出群组并解散群组
        //TODO：由联系人适配组件提供，退群/解散
        contactService.quitOrDismissGroup(groupId, accountName);
    }

    @Subscribe
    public void onGroupDissmissed(ContactProxyEvent.QuitAndDismissEvent event) {
        if (event.isResult()) {
            //退出成功
            //[S] fix bug 4235 by licong, reView by zya,2016/11/8
            deleteTopSetting.get()
                    .delete(groupSessionFlag, isTop)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            deleteNoDisturbSetting
                                    .get()
                                    .delete(groupSessionFlag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                                    .execute(new OkSubscriber<Boolean>(null) {
                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            super.onNext(aBoolean);
                                            //退出成功
                                            handler.sendEmptyMessageDelayed(EXIT_GROUP_SUCCESS, 0);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            LogUtil.getUtils().e(this.getClass().getSimpleName() +
                                                    "onGroupDissmissed delete nodisturb error" +
                                                    Arrays.toString(e.getStackTrace()));
                                            //fix bug 7838 add by zya 20170104
                                            handler.sendEmptyMessageDelayed(EXIT_GROUP_SUCCESS, 0);
                                            //end by zya
                                        }
                                    });

                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            //fix bug 7838 add by zya 20170104
                            handler.sendEmptyMessageDelayed(EXIT_GROUP_SUCCESS, 0);
                            //end by zya
                        }
                    });

            //[E] fix bug 4235 by licong, reView by zya,2016/11/8
        } else {
            //退出失败
            handler.sendEmptyMessageDelayed(EXIT_GROUP_FAIL, 0);
        }
    }


    /**
     * 判断是否是群主
     *
     * @return
     */
    @Override
    public boolean judgeIsGroupOwner() {
        //获取当前群组群主账号
        //TODO：由联系人适配组件提供，获取群主信息
        return contactService.isGroupOwner(groupId);
    }

    /**
     * 设置置顶聊天选项状态
     *
     * @return
     */
    @Override
    public void setTopChatCheckBoxState(boolean isTopShow) {
        this.isShowOnTop = isTopShow;
        getVu().setTopChatCheckBoxState(isTopShow);
    }

    /**
     * 设置免打扰选项状态
     *
     * @return
     */
    @Override
    public void setNoDisturbCheckBoxState(boolean isNoDisturb) {
        //TODO：由框架适配组件提供
        this.isNoDisturb = isNoDisturb;
        getVu().setNoDisturbCheckBoxState(isNoDisturb);
    }

    /**
     * 初始化群组聊天顶部布局
     *
     * @param context
     * @param groupId
     * @param account
     */
    @Override
    public void initGroupChatTopLayout(Context context, String groupId, String account) {

        getVu().initGroupChatTopLayout(context, groupId, account);

    }

}
