package com.xdja.imp.presenter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.securevoipcommon.VoipFunction;
import com.squareup.otto.Subscribe;
import com.xdja.comm.uitl.TelphoneState;
import com.xdja.comm.uitl.handler.SafeLockUtil;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.ChangeMsgState;
import com.xdja.imp.domain.interactor.def.DeleteMsg;
import com.xdja.imp.domain.interactor.def.DownloadFile;
import com.xdja.imp.domain.interactor.def.GetSessionImageList;
import com.xdja.imp.domain.interactor.def.GetTalkMessageBean;
import com.xdja.imp.domain.interactor.def.PauseReceiveFile;
import com.xdja.imp.domain.interactor.def.ResendMsg;
import com.xdja.imp.domain.interactor.def.ResumeReceiveFile;
import com.xdja.imp.domain.interactor.def.ResumeSendFile;
import com.xdja.imp.domain.model.*;
import com.xdja.imp.frame.mvp.presenter.BasePresenterItemAdapter;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.activity.ChooseIMSessionActivity;
import com.xdja.imp.presenter.activity.SinglePhotoPresenter;
import com.xdja.imp.presenter.command.ChatDetailAdapterCommand;
import com.xdja.imp.presenter.command.ChatDetailPopWindowCommand;
import com.xdja.imp.ui.*;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.imp.util.Functions;
import com.xdja.imp.util.HistoryFileUtils;
import com.xdja.imp.util.IMAction;
import com.xdja.imp.util.IMExtraName;
import com.xdja.imp.util.IMMediaPlayer;
import com.xdja.imp.util.VoicePlayState;
import com.xdja.imp.util.XToast;
import com.xdja.imp.widget.ChatDetailLongClickPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 聊天界面消息适配器
 * Created by jing on 2015/12/25.
 * 功能描述
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for hyperlink click by ycm at 20161104.
 */
public class ChatDetailAdapterPresenter
        extends BasePresenterItemAdapter<ChatDetailAdapterCommand, TalkMessageBean>
        implements ChatDetailAdapterCommand, ChatDetailPopWindowCommand {

    @Inject
    Lazy<ChangeMsgState> changeMsgState;

    @Inject
    Lazy<ResendMsg> resendMsg;

    @Inject
    Lazy<DeleteMsg> deleteMsg;

    @Inject
    Lazy<DownloadFile> downloadFile;

    @Inject
    Lazy<PauseReceiveFile> pauseReceiveFile;

    @Inject
    Lazy<ResumeReceiveFile> resumeReceiveFile;

    @Inject
    Lazy<GetSessionImageList> sessionImageList;

    @Inject
    Lazy<UserCache> useCache;

    @Inject
    Lazy<ResumeSendFile> resumeSendFile;

    @Inject
    Lazy<ContactService> contactService;

    @Inject
    BusProvider busProvider;
	
	@Inject
    Lazy<GetTalkMessageBean> getTalkMessageBean;

    /**
     * 消息列表
     */
    private final List<TalkMessageBean> dataSource;

    /**
     * 聊天对象账号
     */
    private String talkAccount;

    /**
     * 所属会话标识
     */
    private String talkFlag;

    /**
     * 是否快速点击  add by ycm 2016/09/11
     */
    private boolean fastClick = false;

    /**
     * 当前页面是否处于可视状态
     */
    private boolean isActivityShowing = false;

    /**
     * 判断当前Activity是否被销毁
     */

    private boolean isActivityDestroy = false;

    /**
     * 聊天类型（群组聊天，单人聊天）
     */
    private int mChatType;

    /**
     * 是否需要连播
     */
    private boolean isNext = false;

    /**
     * 记录短视频对应的消息id和下载状态，用于控制下载和暂停下载
     */
    private final Map<Long, Boolean> isVideoLoading = new ConcurrentHashMap<>();

    private Activity activity;
    private ListView listView;

    private ChatDetailAdapterReceiver receiver;

    private ChatDetailAdapterPresenter chatDetailAdapterPresenter;

    private List<Class<? extends AdapterVu<ChatDetailAdapterCommand, TalkMessageBean>>> vuClasses;

    public ChatDetailAdapterPresenter(List<TalkMessageBean> dataSource, BusProvider busProvider, int chatType) {
        this.dataSource = dataSource;

        //初始化事件总线
        this.busProvider = busProvider;

        this.busProvider.register(this);
        this.chatDetailAdapterPresenter = this;
        mChatType = chatType;
    }

    private class ChatDetailAdapterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            //语音消息播放状态发生改变
            if (action.equals(IMAction.VOICE_CHAT_ITEM_BROCAST_STATECHANGE)) {
                voiceMessagePlayStateChanged(intent);
            }
        }
    }

    /**
     * 重置会话信息
     * @param talkFlag 会话标识
     * @param talkAccount 聊天对象账号
     * @param talkType 聊天类型
     */
    public void refreshTalkInfo(String talkFlag, String talkAccount, int talkType) {
        this.talkFlag = talkFlag;
        this.talkAccount = talkAccount;
        this.mChatType = talkType;
    }

    /**
     * 语音消息播放状态发生改变
     *
     * @param intent intent
     */
    private void voiceMessagePlayStateChanged(Intent intent) {
        if (dataSource == null) {
            return;
        }

        String filePath = intent.getStringExtra(IMExtraName.FILENAME);

        int playState = intent.getIntExtra(IMExtraName.VOICE_PLAY_STATE, -1);

        final long msgID = intent.getLongExtra(IMExtraName.MSGID, -1);

        if (msgID == -1 || TextUtils.isEmpty(filePath)) {
            LogUtil.getUtils().e("message id is -1, or file path is null.");
            return;
        }

        int index = 0;
        boolean flag = false;
        int size = dataSource.size();
        for (; index < size; index++) {
            FileInfo fileInfo = dataSource.get(index).getFileInfo();
            if (fileInfo != null) {
                String path = fileInfo.getFilePath();
                if (!TextUtils.isEmpty(path)) {
                    if((TextUtils.equals(path, filePath) ||
                            path.contains(filePath)) &&
                            (fileInfo.getTalkMessageId() == msgID)){
                        flag = true;
                        break;
                    }
                }
            }
        }
        //modify by zya@xdja.com bug NACTOM-418,当flag为ture的时候index才有意义。
        if (flag) {
            final boolean isMine = dataSource.get(index).isMine();
            VoicePlayState voicePlayState = VoicePlayState.getMessageType(playState);
            switch (voicePlayState) {
                case COMPLETION:
                    //播放下一条
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isMine && isNext) {
                                playNextMessage(msgID);
                            }
                        }
                    }).start();
                    break;
                case ERROR:
                    break;
            }

            if ((playState == VoicePlayState.STOP.getKey() || playState == VoicePlayState.COMPLETION.getKey())
                    && !dataSource.get(index).isMine()
                    && dataSource.get(index).getLimitTime() > 0
                    && dataSource.get(index).getMessageState() < ConstDef.STATE_DESTROY) {
                //发送已销毁回执
                sendDestroyedReceipt(dataSource.get(index));
            } else {
                updateItem(index);
            }
        }
    }


    public void sendDestroyedReceiptByFilePath(String filePath, long messageId, int playState) {
        boolean flag = false;
        int index = 0;
        int size = dataSource.size();
        for (; index < size; index++) {
            FileInfo fileInfo = dataSource.get(index).getFileInfo();
            if ( fileInfo != null && !TextUtils.isEmpty(fileInfo.getFilePath()) && !TextUtils.isEmpty(filePath)) {
                if ((TextUtils.equals(fileInfo.getFilePath(), filePath) ||
                        fileInfo.getFilePath().contains(filePath)) &&
                        (messageId == fileInfo.getTalkMessageId())) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            if ((playState == VoicePlayState.STOP.getKey() || playState == VoicePlayState.COMPLETION.getKey())
                    && !dataSource.get(index).isMine()
                    && dataSource.get(index).getLimitTime() > 0
                    && dataSource.get(index).getMessageState() < ConstDef.STATE_DESTROY) {
                //发送已销毁回执
                sendDestroyedReceipt(dataSource.get(index));
            } else {
                updateItem(index);
            }
        }

    }

    /**
     * 播放下一条消息
     *
     * @param messageId
     */
    @SuppressLint("SwitchIntDef")
    private synchronized void playNextMessage(long messageId) {

        int index = 0;
        int flag = 0;//0
        int lastClickMessageType = ConstDef.MSG_TYPE_DEFAULT;
        TalkMessageBean talkMessageBean = null;
        for (; index < dataSource.size(); index++) {
            talkMessageBean = dataSource.get(index);

            if (flag == 0) {
                if (talkMessageBean.get_id() == messageId) {
                    lastClickMessageType = talkMessageBean.getMessageType();
                    flag = 1;
                }
            } else if (flag == 1) {
                if (!talkMessageBean.isMine()
                        && talkMessageBean.getMessageState() < ConstDef.STATE_READED) {

                    switch (talkMessageBean.getMessageType()) {
                        case ConstDef.MSG_TYPE_VOICE:
                            flag = 2;
                            break;
                    }

                    if (flag == 2) {
                        break;
                    }
                }
            }
        }

        if (flag == 2) {
            switch (talkMessageBean.getMessageType()) {
                case ConstDef.MSG_TYPE_VOICE:
                    clickVoiceMessage(talkMessageBean);
                    break;
            }
        } else {
            //如果不继续播放消息，并且最后一条是Tts语音播报，
            switch (lastClickMessageType) {
                case ConstDef.MSG_TYPE_TEXT:
                    break;
            }
        }
    }


    /**
     * 单独更新列表中的 一个视图/一条数据（单条刷新）
     * @param position 视图/数据 位置
     */
    @Override
    public void updateItem(int position) {
        if (getListView() != null) {
            // 当前listView显示的第一个元素的未知
            int firstVisPosition = getListView().getFirstVisiblePosition();
            // 当前listView显示的最后一个元素的位置
            int lastVisPosition = getListView().getLastVisiblePosition();
            // 如果要更新的元素不在当前屏幕显示中，阻止界面更新操作
            if (position < firstVisPosition - 1 || position > lastVisPosition) {
                return;
            }
            // listView.getChildAt()的参数为要更新的项的索引与当前屏幕内第一条可见条目的偏移量(jff + 1 是因为详情界面listview添加有headview)
            View view = getListView().getChildAt(position - firstVisPosition + 1);
            if (view != null) {
                AdapterVu<ChatDetailAdapterCommand, TalkMessageBean> tag = (AdapterVu<ChatDetailAdapterCommand,TalkMessageBean>)view.getTag();
                tag.bindDataSource(position,getDataSource(position));
            }
        }
    }


    @Override
    protected ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    /**
     * 设置相关绑定的Activity
     *
     * @param activity 目标Activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        if(receiver == null){
            receiver = new ChatDetailAdapterReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IMAction.VOICE_CHAT_ITEM_BROCAST_STATECHANGE);//语音消息播放状态发生改变
            this.activity.registerReceiver(receiver, intentFilter);
        }
    }

    /**
     * 注销广播
     */
    private void unRegisterBroadcastReceiver() {
        try {
            if (receiver != null) {
                this.activity.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        } finally {
            receiver = null;
        }
    }

    public void onDestroy() {
        unRegisterBroadcastReceiver();
        if (null != busProvider) {
            busProvider.unregister(this);
        }
    }


    @Override
    protected Activity getActivity() {
        return this.activity;
    }


    @Override
    protected ChatDetailAdapterCommand getCommand() {
        return this;
    }

    @Override
    protected TalkMessageBean getDataSource(int position) {

        return this.dataSource != null ? this.dataSource.get(position) : null;
    }


    @Override
    public void postDestroyAnimate(TalkMessageBean talkMessageBean){
        LogUtil.getUtils().d("发送已销毁状态内容 = " + talkMessageBean.getContent() + " 状态 = " + talkMessageBean.getMessageState());
        IMProxyEvent.RefreshSingleMessageEvent event = new IMProxyEvent.RefreshSingleMessageEvent();
        event.setMsgAccount(talkMessageBean.getFrom());
        talkMessageBean.setMessageState(ConstDef.STATE_DESTROY);
        event.setTalkMessageBean(talkMessageBean);
        busProvider.post(event);
    }

    @Override
    public int getCount() {
        return this.dataSource != null ? this.dataSource.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return this.dataSource != null ? this.dataSource.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return this.dataSource != null ? this.dataSource.get(position).get_id() : 0;
    }

    @Override
    public int getViewTypeCount() {
        return getVuClasses() != null ? getVuClasses().size() : 0;
    }

    @Override
    public int getNormalTextColor() {
        return Color.BLACK;
    }

    @Override
    public int getLimitTextColor() {
        return Color.LTGRAY;
    }

    @Override
    public SpannableString getShowContentFromString(TalkMessageBean talkMessageBean) {
        if (talkMessageBean == null) {
            return null;
        }
        //[S]modify by lixiaolong on 20160902. fix bug 3158. review by gbc.
        //return new SpannableString(BitmapUtils.formatSpanContent(talkMessageBean.getContent(), activity, 1.1f));
        return BitmapUtils.formatSpanContent(talkMessageBean.getContent(), activity, 1.1f);
        //[E]modify by lixiaolong on 20160902. fix bug 3158. review by gbc.
    }

    public void setIsActivityShowing(boolean isActivityShowing) {
        this.isActivityShowing = isActivityShowing;
    }

    @Override
    public boolean getActivityIsShowing() {
        return isActivityShowing;
    }

    //fix bug 2705 by licong, reView zya, 2016/08/17
    @Override
    public boolean getActivityIsDestroy() {
        return isActivityDestroy;
    }

    public void setIsActivityIsDestroy(boolean isActivityDestroy) {
        this.isActivityDestroy = isActivityDestroy;
    }//end

    @Override
    public TalkMessageBean getTalkMsgBean(int position) {
        return this.dataSource != null ? this.dataSource.get(position) : null;
    }


    @Override
    public void sendReadReceipt(final TalkMessageBean talkMessageBean) {
        //确定当前Activity可见
        if (!isActivityShowing) {
            return;
        }
        //消息需要处理状态
        changeMsgState
                .get()
                .change(talkMessageBean, ConstDef.STATE_READED)
                .execute(new OkSubscriber<Integer>(null) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                        if (integer == 0) {
                            LogUtil.getUtils().d("消息已读状态更改成功");
                            for (int i = 0; i < dataSource.size(); i++) {
                                if (dataSource.get(i).get_id() == talkMessageBean.get_id()) {
                                    talkMessageBean.setMessageState(ConstDef.STATE_READED);
                                    dataSource.set(i, talkMessageBean);

                                    notifyDataSetChanged();
                                }
                            }
                        } else {
                            LogUtil.getUtils().d("消息已读状态更改失败");
                        }
                    }
                });

    }

    @Override
    public void sendDestroyedReceipt(final TalkMessageBean talkMessageBean) {
        //消息需要处理状态
        changeMsgState
                .get()
                .change(talkMessageBean, ConstDef.STATE_DESTROY)
                .execute(new OkSubscriber<Integer>(null) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                        if (integer == 0) {
                            LogUtil.getUtils().d("消息已销毁状态更改成功");

                            //TODO jff 2016.5.16 start
                            // 暂时屏蔽让测试人员帮忙测试销毁动画没有正常播放的问题，
                            //分析原因是因为修改状态，SDK会提供回调那时会主动刷新界面，播放闪信动画
                            //在收到已销毁回执时又触发界面刷新，此时会再次触发闪信动画，两个相加可能导致动画不播放
                            //end

//                            for (int i = 0; i < dataSource.size(); i++) {
//                                if (dataSource.get(i).get_id() == talkMessageBean.get_id()) {
//                                    talkMessageBean.setMessageState(ConstDef.STATE_DESTROYING);
//                                    dataSource.set(i, talkMessageBean);
//                                    updateItem(i);
//                                }
//                            }
//                        } else {
//                            LogUtil.getUtils().d("消息已销毁状态更改失败");
                        }
                    }
                });
    }


    @Override
    protected List<Class<? extends AdapterVu<ChatDetailAdapterCommand, TalkMessageBean>>> getVuClasses() {
        if (this.vuClasses == null) {
            this.vuClasses = new ArrayList<>();
            this.vuClasses.add(ViewSendTextItem.class);
            this.vuClasses.add(ViewRecTextItem.class);
            this.vuClasses.add(ViewChatCustomItem.class);
            this.vuClasses.add(ViewSendVoiceItem.class);
            this.vuClasses.add(ViewRecVoiceItem.class);
            this.vuClasses.add(ViewSendImageItem.class);
            this.vuClasses.add(ViewRecImageItem.class);
            this.vuClasses.add(ViewSendFileItem.class);
            this.vuClasses.add(ViewRecFileItem.class);
			this.vuClasses.add(ViewSendVideoItem.class);
            this.vuClasses.add(ViewRecVideoItem.class);
            this.vuClasses.add(ViewSendWebItem.class);
            this.vuClasses.add(ViewRecWebItem.class);
        }
        return vuClasses;
    }

    @Override
    protected Class<? extends AdapterVu<ChatDetailAdapterCommand, TalkMessageBean>> getVuClassByViewType(int itemViewType) {
        Class<? extends AdapterVu<ChatDetailAdapterCommand, TalkMessageBean>> vuCls = null;
        switch (itemViewType) {
            case ConstDef.MESSAGE_TYPE_SEND_TEXT:
                vuCls = this.getVuClasses().get(0);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_TEXT:
                vuCls = this.getVuClasses().get(1);
                break;
            case ConstDef.MESSAGE_TYPE_PRESENTATION_TEXT:
                vuCls = this.getVuClasses().get(2);
                break;
            case ConstDef.MESSAGE_TYPE_SEND_VOICE:
                vuCls = this.getVuClasses().get(3);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_VOICE:
                vuCls = this.getVuClasses().get(4);
                break;
            case ConstDef.MESSAGE_TYPE_SEND_IMAGE:
                vuCls = this.getVuClasses().get(5);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_IMAGE:
                vuCls = this.getVuClasses().get(6);
                break;
            case ConstDef.MESSAGE_TYPE_SEND_FILE:
                vuCls = this.getVuClasses().get(7);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_FILE:
                vuCls = this.getVuClasses().get(8);
                break;
			case ConstDef.MESSAGE_TYPE_SEND_VIDEO:
                vuCls = this.getVuClasses().get(9);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_VIDEO:
                vuCls = this.getVuClasses().get(10);
                break;
            case ConstDef.MESSAGE_TYPE_SEND_WEB:
                vuCls = this.getVuClasses().get(11);
                break;
            case ConstDef.MESSAGE_TYPE_RECEIVE_WEB:
                vuCls = this.getVuClasses().get(12);
                break;
        }
        return vuCls;
    }

    @Override
    public int getItemViewType(int position) {

        TalkMessageBean bean = this.dataSource != null ? this.dataSource.get(position) : null;


        if (bean == null) {

            return 0;

        } else {

            return getViewType(bean);
        }
    }


    private int getViewType(TalkMessageBean talkMessageBean) {
        int chatDetailType = 0;

        switch (talkMessageBean.getMessageType()) {
            case ConstDef.MSG_TYPE_TEXT:    //文本
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_TEXT;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_TEXT;
                }
                break;

            case ConstDef.MSG_TYPE_VOICE:   //音频
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_VOICE;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_VOICE;
                }
                break;

            case ConstDef.MSG_TYPE_VIDEO:   //视频
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_VIDEO;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_VIDEO;
                }
                break;
            case ConstDef.MSG_TYPE_WEB:
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_WEB;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_WEB;
                }
                break;
            case ConstDef.MSG_TYPE_PHOTO:   //图片
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_IMAGE;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_IMAGE;
                }
                break;


            case ConstDef.MSG_TYPE_FILE:    //文件
                if (talkMessageBean.isMine()) {
                    chatDetailType = ConstDef.MESSAGE_TYPE_SEND_FILE;
                } else {
                    chatDetailType = ConstDef.MESSAGE_TYPE_RECEIVE_FILE;
                }
                break;

            case ConstDef.MSG_TYPE_PRESENTATION:
                chatDetailType = ConstDef.MESSAGE_TYPE_PRESENTATION_TEXT;
                break;
            case ConstDef.MSG_TYPE_DEFAULT:
                break;
            default:
                break;
        }
        return chatDetailType;
    }


    @Override
    public void reSendMessage(final TalkMessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        messageBean.setMessageState(ConstDef.STATE_SENDING);
        //fix bug 3394 by licong, reView by zya, 2016/8/30
        refreshItem(messageBean.get_id());
        //notifyDataSetChanged();
        resendMsg
                .get()
                .setChatMsg(messageBean)
                .execute(new OkSubscriber<Integer>(null) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                        //fix bug 3394 by licong, reView by zya, 2016/8/30
                        //notifyDataSetChanged();
                        refreshItem(messageBean.get_id());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        LogUtil.getUtils().d("重发出现异常 " + e.getMessage());
                    }
                });
    }

    @Override
    public void longClickMessage(final TalkMessageBean bean,final View view) {
        FileInfo fileInfo = bean.getFileInfo();
        // 如果是短视频消息，需要查询一次短视频相关的所有信息，判断短视频文件是否已经下载
        if (!bean.isMine() && fileInfo != null && fileInfo.getFileType() == ConstDef.TYPE_VIDEO) {
            getTalkMessageBean
                    .get().get(bean.get_id() + "")
                    .execute(new OkSubscriber<TalkMessageBean>(null) {
                        @Override
                        public void onNext(TalkMessageBean talkMessageBean) {
                            super.onNext(talkMessageBean);
                            bean.setFileInfo(talkMessageBean.getFileInfo());
                            new ChatDetailLongClickPopup(activity, bean, chatDetailAdapterPresenter).
                                    showAtLocation(view, Gravity.CENTER, 0, 0);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            LogUtil.getUtils().e("GetVideo Fail");
                        }
                    });
        } else {
            new ChatDetailLongClickPopup(activity, bean, this).
                    showAtLocation(view, Gravity.CENTER, 0, 0);
        }

        //长按语音弹出PopupWindows 停止正在播放的语音
        // fix bug NACTOMA-457 by licong 2016/6/30 , reView by zya@xdja.com
        if (fileInfo != null && (fileInfo.getFileType() == ConstDef.TYPE_VOICE)){
            IMMediaPlayer.stopPlay();
        }
    }


    @Override
    public int getFileLogoId(TalkMessageBean talkMessageBean) {
        if(talkMessageBean == null){
            return R.drawable.ic_others;
        }
        return HistoryFileUtils.getIconWithSuffix(talkMessageBean);
    }


    @Override
    public void copy(TalkMessageBean bean) {
        ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (bean != null && !TextUtils.isEmpty(bean.getContent())) {
            cmb.setText(bean.getContent());
        }
    }

    @Override
    public void deleteSingleMessage(final TalkMessageBean talkMessageBean) {
        if (talkMessageBean != null) {
            final long id = talkMessageBean.get_id();
            List<Long> ids = new ArrayList<>();
            ids.add(id);
            //删除数据库中的数据
            deleteMsg
                    .get()
                    .delete(ids)
                    .execute(new OkSubscriber<Integer>(null) {
                        @Override
                        public void onNext(Integer integer) {
                            super.onNext(integer);
                            if (integer == 0) {
                                LogUtil.getUtils().d("ID为" + id + "的消息删除成功");
                                onDeleteMessage(talkMessageBean);
                            }
                        }
                    });

        }
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }

    @Override
    public void startContactDetailActivity(String account) {
        contactService.get().startContactDetailActivity(account);
    }

    @Override
    public CharSequence getVoiceLength(TalkMessageBean talkMessageBean) {
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if(fileInfo != null){
            if(fileInfo.getFileType() == ConstDef.TYPE_VOICE){
                return ((VoiceFileInfo) fileInfo).getAmountOfTime() + " \"";
            }
        }
        return null;
    }

    @Override
    public boolean getVoiceMessageIsPlaying(String filePath, long messageId) {
        return IMMediaPlayer.getVoiceMessageIsPlaying(filePath, messageId);
    }

    /**
     * 点击语音消息
     * @param talkMessageBean
     */
    @Override
    public synchronized void clickVoiceMessage(TalkMessageBean talkMessageBean) {

        //如果正在通话，停止语音
        if (!TelphoneState.getPhotoStateIsIdle(activity.getApplicationContext()) ||
                VoipFunction.getInstance().hasActiveCall() ||
                VoipFunction.getInstance().isMediaPlaying()) {
            IMMediaPlayer.stopPlay();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new XToast(activity).display(R.string.Phone_is_inCall);
                }
            });
            LogUtil.getUtils().d(R.string.Phone_is_inCall);
            return;
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        FileInfo info = talkMessageBean.getFileInfo();
        if(info == null){
            LogUtil.getUtils().e("fileInfo is null error, so return.");
            return;
        }

        fileInfos.add(info);

        //文件已经下载完成(存在大于的情况，具体原因待查)
        if(talkMessageBean.isMine() || info.getTranslateSize() >= info.getFileSize()){
            //本地文件存在，或者本地文件缓存被清除
            if (checkFile(info.getFilePath())) {
                isNext = false;
                if (getVoiceMessageIsPlaying(info.getFilePath(), info.getTalkMessageId())) {
                    IMMediaPlayer.stopPlay();
                } else if (!(talkMessageBean.isBomb() && talkMessageBean.getMessageState() >= ConstDef.STATE_READED)
                        || (talkMessageBean.isBomb() && talkMessageBean.isMine())) {

                    if (isActivityShowing){
                        IMMediaPlayer.startPlay(info.getFilePath(),
                                talkMessageBean.get_id(),
                                talkMessageBean.isMine());
                        //[S]lll@xdja.com 2016-11-02 added. receiver mode tips. review by liming.
                        busProvider.post(new IMProxyEvent.PlayVoiceEvent());
                        //[E]lll@xdja.com 2016-11-02 added. receiver mode tips. review by liming.
                    } else {
                        IMMediaPlayer.stopPlay();
                    }

                    //fix bug NACTOMA-416 by zya@xdja.com,语音分类不明确，需要把自己发送的排除出去
                    if (!talkMessageBean.isMine()) {
                        sendReadReceipt(talkMessageBean);
                    }
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new XToast(activity).display(R.string.file_no_exists);
                    }
                });
            }
        } else {

            //网络未连接的情况下，直接提示退出
            if (!Functions.isAnyNetworkConnected(getActivity())) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new XToast(activity).display(R.string.net_no_exists);
                    }
                });
                return;
            }

            isNext = true;

            //文件未下载完成，或者存在数据库被清除的可能
            if (checkFile(info.getFilePath())){
                if (getVoiceMessageIsPlaying(info.getFilePath(), info.getTalkMessageId())) {
                    IMMediaPlayer.stopPlay();
                } else {
                    if (isActivityShowing){
                        IMMediaPlayer.startPlay(info.getFilePath(),
                                talkMessageBean.get_id(),
                                talkMessageBean.isMine());
                        //[S]lll@xdja.com 2016-11-02 added. receiver mode tips. review by liming.
                        busProvider.post(new IMProxyEvent.PlayVoiceEvent());
                        //[E]lll@xdja.com 2016-11-02 added. receiver mode tips. review by liming.
                    } else {
                        IMMediaPlayer.stopPlay();
                    }
                    if (!talkMessageBean.isMine()) {
                        sendReadReceipt(talkMessageBean);
                    }
                }
            } else {
                //如果是发送方，则继续发送；如果是接收方，则重新下载
                if (talkMessageBean.isMine()){
                    resumeSendFile.get().resume(info).execute(new OkSubscriber<Integer>(null) {
                        @Override
                        public void onNext(Integer integer) {
                            super.onNext(integer);
                            if (integer == 0) {
                                LogUtil.getUtils().d("重新发送语音文件");
                            }
                        }
                    });
                } else {
                    downloadFile.get().downLoad(fileInfos).execute(new OkSubscriber<Integer>(null) {
                        @Override
                        public void onNext(Integer integer) {
                            super.onNext(integer);
                            if (integer == 0) {
                                LogUtil.getUtils().d("开始下载语音文件");
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void clickVideoMessage(final TalkMessageBean talkMessageBean) {

        if (fastClick) {
            return;
        }

        getTalkMessageBean
                .get()
                .get(talkMessageBean.get_id()+"")
                .execute(new OkSubscriber<TalkMessageBean>(null){
            @Override
            public void onNext(TalkMessageBean bean) {
                super.onNext(bean);

                FileInfo fileInfo = bean.getFileInfo();
                if (fileInfo == null) {
                    return;
                }
                VideoFileInfo videoFileInfo = (VideoFileInfo) fileInfo;
                        if (videoFileInfo.getFileSize() != 0) {
                            FileExtraInfo extraInfo = videoFileInfo.getExtraInfo();
                            if (extraInfo == null) {
                                return;
                            }

                            String rawPath = extraInfo.getRawFileUrl();
                            File file = new File(rawPath);
                            if (talkMessageBean.isMine() ||
                                    file.exists() && file.length() == extraInfo.getRawFileSize()) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                                Intent intent = new Intent(activity , SinglePhotoPresenter.class);
                                intent.putExtra(ConstDef.TAG_TALKFLAG , talkFlag);
                                intent.putExtra(ConstDef.MSG_ID, videoFileInfo.getTalkMessageId());
                                activity.startActivity(intent);
                                if ( !talkMessageBean.isMine()) {
                                    sendReadReceipt(talkMessageBean);
                                }
                            } else{
                                if ( !isVideoLoading.containsKey(talkMessageBean.get_id())){
                                    isVideoLoading.put(talkMessageBean.get_id(), true);
                                    downVideoMessage( videoFileInfo );
									//jyg add 2017/3/15 start 解决下载短视频立即更新进度条
                                    videoFileInfo.setType(ConstDef.FILE_IS_RAW);
                                    videoFileInfo.setFileState(ConstDef.LOADING);
                                    videoFileInfo.setPercent(0);
                                    talkMessageBean.setFileInfo(videoFileInfo);
                                    notifyDataSetChanged();
									//jyg add 2017/3/15 end
                                }
                            }

                        }
                    }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.getUtils().e("GetVideo Fail");
            }

        });

    }

    @Override
    public void downVideoMessage(VideoFileInfo videoInfo) {
        videoInfo.setType(ConstDef.FILE_IS_RAW);
        List<FileInfo> videoInfos = new ArrayList<>();
        videoInfos.add(videoInfo);
        downloadFile.get().downLoad(videoInfos).execute(new OkSubscriber<Integer>(null));
    }

    /**
     * 获取最后一条显示时间轴的消息位置
     */
    @Override
    public int getLastTimeLineIsShowPosition(){
        //[S]modify by lixiaolong on 20160919. fix bug 4207.review by myself.
        //return lastTimeLineIsShowPosition;
        if (dataSource != null && dataSource.size() > 0) {
            int lastIndex = dataSource.size() - 1;
            for (int i = lastIndex; i >= 0; i--) {
                if (dataSource.get(i).isShowTimeLine()) {
                    return i;
                }
            }
        }
        return 0;
        //[E]modify by lixiaolong on 20160919. fix bug 4207.review by myself.
    }

    @Override
    public void clickImageMessage(TalkMessageBean talkMessageBean) {
        //start: add by ycm for fast click 2016/9/11
        if (fastClick) {
            return;
        }

        //modify by guorong 解决销毁闪信和下载中缩略图可以点击的问题 start
        if(!talkMessageBean.isMine() && (talkMessageBean.getMessageState() < ConstDef.STATE_READED
                || talkMessageBean.getMessageState() >= ConstDef.STATE_DESTROY)){
            return;
        }
        //modify by guorong 解决销毁闪信和下载中缩略图可以点击的问题 end
        //end: add by ycm for fast click 2016/9/11
        final FileInfo fileInfo = talkMessageBean.getFileInfo();
        if (fileInfo != null) {
            LogUtil.getUtils().d("fileInfo:" + fileInfo.toString());

            //在弹出图片预览界面时，如果键盘是弹出状态，则收起键盘
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            Intent intent = new Intent(activity , SinglePhotoPresenter.class);
            intent.putExtra(ConstDef.TAG_TALKFLAG , talkFlag);
            intent.putExtra(ConstDef.MSG_ID, fileInfo.getTalkMessageId());
            activity.startActivity(intent);
        }
    }

    @Override
    public void loadImage(TalkMessageBean talkMessageBean) {
        List<FileInfo> fileInfos = new ArrayList<>();
        FileInfo info = talkMessageBean.getFileInfo();
        if(info == null){
            return;
        }
        fileInfos.add(info);

        downloadFile.get().downLoad(fileInfos).execute(new OkSubscriber<Integer>(null){

            @Override
            public void onNext(Integer integer) {
                super.onNext(integer);

                if (integer == 0) {
                    LogUtil.getUtils().d("开始下载语音文件");
                }

                LogUtil.getUtils().d("update progress :" + integer);
            }
        });
    }

    @Override
    public void postMsgDestory(TalkMessageBean bean) {
        IMProxyEvent.DestroyedEvent event = new IMProxyEvent.DestroyedEvent();
        event.setTalkMessageBean(bean);
        busProvider.post(event);
    }
	
	// add by ycm for sharing web message [start]
    @Override
    public void clickWebMessage(TalkMessageBean talkMessageBean) {
        openWebMessage(talkMessageBean);
    }

    private void openWebMessage(TalkMessageBean talkMessageBean) {
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if (fileInfo instanceof WebPageInfo) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(((WebPageInfo) fileInfo).getWebUri());
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }
	// add by ycm for sharing web message [end]

    /**
     * 获取群组成员信息
     *
     * @param groupId 群组ID
     * @param account 成员账号
     * @return
     */
    @Override
    public ContactInfo getGroupMemberInfo(String groupId, String account) {
        return contactService.get().GetGroupMemberInfo(groupId, account);
    }

    private int onDeleteMessage(TalkMessageBean talkMessageBean) {
        try {
            //构建事件对象
            IMProxyEvent.DeleteMessageEvent messageEvent = new IMProxyEvent.DeleteMessageEvent();
            messageEvent.setMsgAccount(talkMessageBean.getFrom());
            messageEvent.setTalkMessageBean(talkMessageBean);

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }


    @Override
    public void repeat(TalkMessageBean bean) {
        reSendMessage(bean);
    }

    @Override
    public void delete(TalkMessageBean bean) {
        deleteSingleMessage(bean);

        //fix bug NACTOMA-410 zya@xdja.com 删除语音的时候，如果删除的是播放的语音则停止语音播放
        FileInfo info = bean.getFileInfo();
        if(info == null) return ;
        if(bean.getMessageType() == ConstDef.MSG_TYPE_VOICE
                && getVoiceMessageIsPlaying(info.getFilePath(), info.getTalkMessageId())){
            IMMediaPlayer.stopPlay();
        }
    }

    // Task 2632 [Begin]
    /**
     * 转发
     * @param bean
     */
    @Override
    public void forwardMessage(final TalkMessageBean bean) {
        //[S] fix bug 8719 by licong,变量用来判断当前使用过转发
        SafeLockUtil.setIsForwardMessage(true);
        //[E] fix bug 8719 by licong
        final Intent forwardIntent = new Intent();
        forwardIntent.setClass(getActivity(), ChooseIMSessionActivity.class);
        forwardIntent.putExtra(ConstDef.TAG_TALKERID, talkAccount);
        forwardIntent.putExtra(ConstDef.TAG_TALKTYPE, mChatType);//消息类型
        forwardIntent.setAction(ConstDef.FORWARD);
        switch (bean.getMessageType()) {
            case ConstDef.MSG_TYPE_TEXT:
                forwardText(forwardIntent, bean);
                break;
            case ConstDef.MSG_TYPE_PHOTO:
                forwardImage(forwardIntent, bean);
                break;
            case ConstDef.MSG_TYPE_FILE:
                forwardFile(forwardIntent, bean);
                break;
            case ConstDef.MSG_TYPE_VIDEO:
                forwardVideo(forwardIntent, bean);
                break;
            case ConstDef.MSG_TYPE_DEFAULT:
                break;
            case ConstDef.MSG_TYPE_PRESENTATION:
                break;
            case ConstDef.MSG_TYPE_VOICE:
                break;
            case ConstDef.MSG_TYPE_WEB:
                forwardWeb(forwardIntent, bean);
                break;
        }
    }
	
	//add by ycm for forward web message [start]
    private void forwardWeb(Intent forwardIntent, TalkMessageBean bean) {
        FileInfo fileInfo = bean.getFileInfo();// 文件信息
        if (fileInfo == null) {
            LogUtil.getUtils().e("ERROR: file info is null.");
            return;
        }

        if (fileInfo instanceof WebPageInfo) {
            // TODO: 2017/3/31 ycm 网页文件被删除处理方式
            WebPageInfo webPageInfo = (WebPageInfo) fileInfo;
            ArrayList<WebPageInfo> fileInfos = new ArrayList<>();
            fileInfos.add(webPageInfo);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ConstDef.TAG_SELECTWEB, fileInfos);
            forwardIntent.putExtras(bundle);
            forwardIntent.setType(ConstDef.WEB_SHARE_TYPE);
            getActivity().startActivity(forwardIntent);
        }
    }
	//add by ycm for forward web message [end]

    /**
     * 转发文本
     * @param forwardIntent 转发内容
     * @param bean 消息bean
     */
    private void forwardText(Intent forwardIntent, TalkMessageBean bean) {
        String beanContent = bean.getContent();
        if (beanContent != null && !beanContent.isEmpty()) {
            ClipData beanContentClip = ClipData.newPlainText(" ", bean.getContent());
            forwardIntent.setClipData(beanContentClip);
            forwardIntent.putExtra(ConstDef.FORWARD_CONTENT, beanContent);
        }
        forwardIntent.setType(ConstDef.TEXT_SHARE_TYPE);
        getActivity().startActivity(forwardIntent);
    }

    /**
     * 转发图片
     * @param forwardIntent 转发内容
     * @param bean 消息bean
     */
    private void forwardImage(final Intent forwardIntent, final TalkMessageBean bean) {
        getTalkMessageBean
                .get()
                .get(bean.get_id() + "")
                .execute(new OkSubscriber<TalkMessageBean>(null) {
                    @Override
                    public void onNext(TalkMessageBean bean) {
                        super.onNext(bean);
                        ArrayList<FileInfo> uriList = new ArrayList<>();
                        boolean isOriginal;
                        if (bean != null) {
                            final FileInfo fileInfo = bean.getFileInfo();
                            isOriginal = ((ImageFileInfo) fileInfo).isOriginal();
                            uriList.add(fileInfo);
                            Bundle bundle = new Bundle();
                            //发送图片数据
                            bundle.putParcelableArrayList(ConstDef.TAG_SELECTPIC, uriList);
                            //添加数据到Intent
                            forwardIntent.putExtras(bundle);
                            forwardIntent.setType(ConstDef.IMAGE_SHARE_TYPE);
                            forwardIntent.putExtra(ConstDef.IS_ORIGINAL, isOriginal);
                            getActivity().startActivity(forwardIntent);
                        } else {
                            new XToast(getActivity()).display(R.string.get_image_error);// add by ycm 20161115
                        }
                    }
                });
    }

    /**
     * 转发文件
     * @param forwardIntent 转发内容
     * @param bean 消息bean
     */
    private void forwardFile(Intent forwardIntent, TalkMessageBean bean) {
        FileInfo fileInfo = bean.getFileInfo();// 文件信息
        if (fileInfo == null) {
            LogUtil.getUtils().e("ERROR: file info is null.");
            return;
        }
        if (!checkFile(fileInfo.getFilePath())) {// 文件被删除
            new XToast(getActivity()).display(R.string.file_is_deleted);
            return;
        }
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(fileInfo);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstDef.TAG_SELECTFILE, fileInfos);
        forwardIntent.putExtras(bundle);
        forwardIntent.setType(ConstDef.FILE_SHARE_TYPE);
        getActivity().startActivity(forwardIntent);
    }

    /**
     * 转发小视频
     * @param forwardIntent 转发内容
     * @param bean 消息bean
     */
    private void forwardVideo(final Intent forwardIntent, TalkMessageBean bean) {
        final String msgId = bean.get_id() + "";
        getTalkMessageBean
                .get()
                .get(msgId)
                .execute(new OkSubscriber<TalkMessageBean>(null) {
                    @Override
                    public void onNext(TalkMessageBean bean) {
                        super.onNext(bean);
                        FileInfo fileInfo = bean.getFileInfo();
                        VideoFileInfo videoInfo = (VideoFileInfo) fileInfo;
                        if (videoInfo != null && videoInfo.getFileSize() != 0) {
                            if (!checkFile(videoInfo.getFilePath())) {// 文件被删除
                                new XToast(getActivity()).display(R.string.file_is_deleted);
                                return;
                            }
                            ArrayList<VideoFileInfo> fileInfos = new ArrayList<>();
                            videoInfo.setPercent(0);// add by ycm for bug 10107
                            videoInfo.setTranslateSize(0);
                            fileInfos.add(videoInfo);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(ConstDef.TAG_SELECTVIDEO, fileInfos);
                            forwardIntent.putExtras(bundle);
                            forwardIntent.setType(ConstDef.VIDEO_SHARE_TYPE);
                            getActivity().startActivity(forwardIntent);
                        }
                    }

                });
    }
    

    //打开文件
    @Override
    public void openFile(TalkMessageBean bean) {
        if(bean == null || bean.getFileInfo() == null){
            return;
        }
        FileInfo fileInfo = bean.getFileInfo();
        HistoryFileUtils.intentBuilder(getActivity(), fileInfo.getFilePath() , fileInfo.getSuffix());
    }

    // Task 2632 [End]


    @Override
    public void playMediaInCall(TalkMessageBean bean) {

    }

    @Override
    public void playMediaInLoudspeakers(TalkMessageBean bean) {

    }

    @Override
    public void suspend(TalkMessageBean bean) {

    }

    @Override
    public void reDown(TalkMessageBean bean) {

    }

    @Override
    public void callPhone(TalkMessageBean bean) {

    }

    //备注更新回调事件分发
    @Subscribe
    public void ReceiveRemarkUpdateEvent(ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent) {
        //String account = remarkUpdateEvent.getAccount();
        //String showName = remarkUpdateEvent.getShowName();
        //刷新对应的聊天对象
        if (mChatType == ConstDef.CHAT_TYPE_P2G) {
            //modify by zya@xdja.com.fix bug 2709,review by gr,20160812
            //refreshItem(account);
            notifyDataSetChanged();
            //end
        }
    }

    @Subscribe
    public void RecieveFileFinished(IMProxyEvent.ReceiveFileFinishedEvent event) {
            FileInfo fileInfo = event.getFileInfo();
            if (fileInfo != null && fileInfo.getFileType() == ConstDef.TYPE_VIDEO &&
                isVideoLoading.containsKey( fileInfo.getTalkMessageId() )){
            isVideoLoading.remove(fileInfo.getTalkMessageId());
            }
		}


    private void refreshItem(long talkId) {

        // 当前listView显示的第一个元素的未知
        int firstVisPosition = listView.getFirstVisiblePosition();
        // 当前listView显示的最后一个元素的位置
        int lastVisPosition = listView.getLastVisiblePosition();

        int position = getPosition(talkId, firstVisPosition, lastVisPosition);
        // 如果要更新的元素不在当前屏幕显示中，阻止界面更新操作
        if (position < 0) {
            return;
        }
        updateItem(position);

    }

    private int getPosition(long ownerId, int firstVisPosition, int lastVisPosition) {
        LogUtil.getUtils().i("zhu->firstVisPosition:" + firstVisPosition + ";lastVisPosition:" +
                lastVisPosition + ";dataSource.size:" + dataSource.size());
        int position = -1;
        if (dataSource.size() == 0) {
            return position;
        }
        int size = dataSource.size();
        //modify by zya@xdja.com,fix bug 1634.20160808
        for (int i = firstVisPosition; i < lastVisPosition; i++) {
            LogUtil.getUtils().i("zhu->i:" + i);
            //modify by zya@xdja.com,fix bug 1634
            if (size == 0 || i > size) {
                break;
            }
            //start fix bug 4937 by licong, reView by zya, 2016/10/19
            if (ownerId == dataSource.get(i).get_id()) {
                position = i;
                break;
            }
            //end fix bug 4937 by licong, reView by zya, 2016/10/19
        }
        return position;
    }

    /**
     * 检测本地文件是否存在
     *
     * @param filePath 文件绝对路径
     * @return {@code true} if this file exists, {@code false} otherwise.
     */
    private boolean checkFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
