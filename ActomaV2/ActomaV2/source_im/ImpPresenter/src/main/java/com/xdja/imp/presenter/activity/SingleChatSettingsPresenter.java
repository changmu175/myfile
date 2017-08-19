package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.interactor.def.AddNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.ClearAllMsgInTalk;
import com.xdja.imp.domain.interactor.def.DeleteNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.DeleteTopSetting;
import com.xdja.imp.domain.interactor.def.GetSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.SaveTopSetting;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.event.SessionChangedEvent;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.messageNotification.ChatNotifiSetedCache;
import com.xdja.imp.presenter.command.ISingleChatCommand;
import com.xdja.imp.ui.SingleChatSettingVu;
import com.xdja.imp.ui.vu.ISingleChatSettingVu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by wanghao on 2015/12/3.
 */
public class SingleChatSettingsPresenter
        extends IMActivityPresenter<ISingleChatCommand, ISingleChatSettingVu>
        implements ISingleChatCommand {

    @Inject
    GetSingleSessionConfig getSingleSessionConfig;

    @Inject
    Lazy<SaveTopSetting> saveTopSetting;

    @Inject
    Lazy<DeleteTopSetting> deleteTopSetting;

    @Inject
    Lazy<AddNoDisturbSetting> addNoDisturbSetting;

    @Inject
    Lazy<DeleteNoDisturbSetting> deleteNoDisturbSetting;

    @Inject
    Lazy<ClearAllMsgInTalk> clearAllMsgInTalk;

    @Inject
    BusProvider busProvider;

    private String flag;

    private String accountName;
    /**
     * 聊天对象ID
     */
    private String talkerId;

    private static final int CLEARFALG_SUCCESS = 1;  //清空聊天记录成功
    private static final int CLEARFALG_FAIL = 2;    //清空聊天记录失败

    private MyHandler handler = new MyHandler(new WeakReference<>(this));

    @Inject
    Lazy<ContactService> contactService;

    @NonNull
    @Override
    protected Class<? extends ISingleChatSettingVu> getVuClass() {
        return SingleChatSettingVu.class;
    }

    @NonNull
    @Override
    protected ISingleChatCommand getCommand() {
        return this;
    }


    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        useCaseComponent.inject(this);
        //初始化事件总线
        busProvider.register(this);

        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra(ConstDef.TAG_TALKFLAG);
            talkerId = intent.getStringExtra(ConstDef.TAG_TALKERID);
            if (TextUtils.isEmpty(stringExtra)) {
                this.finish();
                return;
            } else {
                flag = stringExtra;
                accountName = flag.substring(0, flag.indexOf('_'));
            }
//            LogUtil.getUtils().d("会话信息相关对象ID为：" + flag);
        }

        getSingleSessionConfig
                .get(flag)
                .execute(
                        new OkSubscriber<SessionConfig>(this.okHandler) {
                            @Override
                            public void onNext(SessionConfig sessionConfig) {
                                super.onNext(sessionConfig);
                                if (sessionConfig != null) {
                                    getVu().setNoDisturbCheckBoxState(sessionConfig.isNoDisturb());
                                    getVu().setTopChatCheckBoxState(sessionConfig.isTop());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.getUtils().d("获取到的会话配置异常");
                            }
                        }
                );

        boolean isShow = contactService.get().isFriendRelated(accountName) || contactService.get().isExistDepartment(accountName);
        getVu().isShowAddFriendBtn(isShow);
        if (null != talkerId) {
            ContactInfo info = getCommand().getContactInfo(talkerId);
            getVu().setPartnerImage(info.getThumbnailUrl());
            getVu().setNickName(info.getName());
        }
        //fix bug 6418 by zya,20161128
        setTitle(getResources().getString(R.string.setting));
        //end by zya
    }

    @Override
    public void openPersonListActivity() {
        ArrayList<String> temArrayList = new ArrayList();
        temArrayList.add(accountName);
        ContactModuleProxy.startChooseActivity(this, null, temArrayList);
    }

    @Override
    public void updateTopChatCheckBoxState(final boolean  isTopChat) {
        if (isTopChat) {
            saveTopSetting
                    .get()
                    .save(flag, true)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
//                        LogUtil.getUtils().d("设置置顶" + (aBoolean ? "成功" : "失败"));
                        }
                    });
        } else {
            deleteTopSetting.get()
                    .delete(flag, false)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
//                        LogUtil.getUtils().d("设置置顶" + (aBoolean ? "成功" : "失败"));
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.getUtils().d("设置置顶失败");
                        }
                    });
        }
        //fix bug 3166 by licong, reView by zya, 2016/8/23
        //发送更新事件
        SessionChangedEvent.TopStateChangedEvent event
                = new SessionChangedEvent.TopStateChangedEvent();
        event.setTop(isTopChat);
        event.setFlag(flag);

        busProvider.post(event);
    }

    @Override
    public void updateChatNoDisturbCheckBoxState(boolean isNoDisturb) {
        if (isNoDisturb) {
            addNoDisturbSetting
                    .get()
                    .add(flag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            LogUtil.getUtils().d("增加勿扰模式" + (aBoolean ? "成功" : "失败"));
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.getUtils().d("增加勿扰模式失败");
                        }
                    });
        } else {
            deleteNoDisturbSetting
                    .get()
                    .delete(flag, ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE)
                    .execute(new OkSubscriber<Boolean>(this.okHandler) {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
//                            LogUtil.getUtils().d("删除勿扰模式" + (aBoolean ? "成功" : "失败"));
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.getUtils().d("删除勿扰模式失败");
                        }
                    });
        }
        ChatNotifiSetedCache.setSettingValue(accountName, !isNoDisturb);

        //发送更新事件
        SessionChangedEvent.NodisturbStateChangedEvent event
                = new SessionChangedEvent.NodisturbStateChangedEvent();
        event.setNoDisturb(isNoDisturb);
        event.setFlag(flag);

        busProvider.post(event);
    }

    //清空所有数据
    @Override
    public void cleanAllSingleChatMessages() {
        getVu().showProgressDialog(getResources().getString(R.string.common_clean_all_chat_messages_progress_text));
        clearAllMsgInTalk
                .get()
                .clear(flag)
                .execute(new OkSubscriber<Integer>(this.okHandler) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
//                        LogUtil.getUtils().d("清空会话数据" + (integer == 0 ? "成功" : "失败"));

                        //发送更新事件
                        SessionChangedEvent.MessageCleardEvent event
                                = new SessionChangedEvent.MessageCleardEvent();
                        event.setFlag(flag);
                        busProvider.post(event);

                        //add by zya@xdja.com,20161109
                        userCache.clearCacheText();
                        //end by zya

                        handler.sendEmptyMessageDelayed(CLEARFALG_SUCCESS, 0);
                    }

                    @Override
                    public void onError(Throwable e) {//删除失败弹出提醒
                        super.onError(e);
                        /*Toast.makeText(SingleChatSettingsPresenter.this,
                                getString(R.string.failed_clean_all_chat_messages),
                                Toast.LENGTH_SHORT).show();*/
                        handler.sendEmptyMessageDelayed(CLEARFALG_FAIL, 0);
                    }
                });
    }

    @Override
    public void openChatDetailInterface() {
        ContactModuleProxy.startContactDetailActivity(getApplicationContext(), accountName);
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }
	
	//add by zya
    @Override
    public void openHistoryFileListActivity() {
        Intent intent = new Intent(this,HistoryFileListActivity.class);
        intent.putExtra(ConstDef.TAG_TALKFLAG,flag);
        intent.putExtra(ConstDef.TAG_TALKERID,talkerId);
        startActivity(intent);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SingleChatSettingsPresenter> mActivity;
        public  MyHandler(WeakReference<SingleChatSettingsPresenter> mActivity) {
            this.mActivity = mActivity;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SingleChatSettingsPresenter activity = mActivity.get();
            if (activity != null) {
                int code = msg.what;
                if (code == CLEARFALG_FAIL) {
                    Toast.makeText(activity.getBaseContext(), R.string.clearFail, Toast.LENGTH_SHORT).show();
                }

                activity.getVu().dismissDialog();
            }
        }
    }


    /*-------------------------- 一下代码接收事件总线EventBus事件后的相应   -------------------------*/
    @Subscribe
    public void onReceiveClearSingleTalkMessage(ContactProxyEvent.DeletFriendClearTalkEvent
                                                        delFriendEvent) {
        //TODO: 退出界面
        finish();
    }
    //备注更新回调事件分发
    @Subscribe
    public void ReceiveRemarkUpdateEvent(ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent) {
        String accountNO = remarkUpdateEvent.getAccount();
        String showName = remarkUpdateEvent.getShowName();
        if (TextUtils.isEmpty(accountNO) || TextUtils.isEmpty(accountName)) {
            return;
        }
        //刷新对应的账户昵称
        if (accountName.equals(accountNO)) {
            getVu().setNickName(showName);
        }
    }

    @Subscribe
    public void ReceiveNickNameUpdateEvent(ContactProxyEvent.NickNameUpdateEvent nickNameUpdateEvent) {
        ArrayList<String> accountList = nickNameUpdateEvent.getAccounts();
        if (accountList.size() == 0) {
            return;
        }
        //刷新对应的账户昵称
        if (accountList.contains(accountName)) {
            ContactInfo contactInfo = contactService.get().getContactInfo(accountName);
            String showName = ((TextUtils.isEmpty(contactInfo.getName())) ? accountName : contactInfo.getName());
            getVu().setNickName(showName);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销事件总线回调
        if (null != busProvider) {
            busProvider.unregister(this);
        }
    }
}
