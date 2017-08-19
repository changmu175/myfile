package com.xdja.imp.presenter.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;

import com.securevoipcommon.VoipFunction;
import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.event.ChangeTabIndexEvent;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.PermissionUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.comm.uitl.TelphoneState;
import com.xdja.comm.uitl.handler.SafeLockUtil;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.entity.mapper.ValueConverter;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.interactor.def.ClearUnReadMsg;
import com.xdja.imp.domain.interactor.def.GetConfig;
import com.xdja.imp.domain.interactor.def.GetMsgList;
import com.xdja.imp.domain.interactor.def.SendCustomTextMsg;
import com.xdja.imp.domain.interactor.def.SendFileMsg;
import com.xdja.imp.domain.interactor.def.SendFileMsgList;
import com.xdja.imp.domain.interactor.def.SendTextMsg;
import com.xdja.imp.domain.interactor.mx.GetSingleSessionConfigUseCase;
import com.xdja.imp.domain.interactor.mx.SaveDraftUseCase;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.domain.model.VoiceFileInfo;
import com.xdja.imp.event.ForwardCompletedEvent;
import com.xdja.imp.event.SessionChangedEvent;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.ChatDetailAdapterPresenter;
import com.xdja.imp.presenter.command.IChatDetailCommand;
import com.xdja.imp.ui.ViewChatDetail;
import com.xdja.imp.ui.vu.IChatDetailVu;
import com.xdja.imp.util.DataFileUtils;
import com.xdja.imp.util.FileSizeUtils;
import com.xdja.imp.util.IMMediaPlayer;
import com.xdja.imp.util.ImageCache;
import com.xdja.imp.util.SimcUiConfig;
import com.xdja.imp.util.TranslateInfoUtil;
import com.xdja.imp.util.VoicePlayState;
import com.xdja.imp.util.XToast;
import com.xdja.simcui.recordingControl.manager.MediaManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;

/**
 * Created by wanghao on 2015/11/23.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ChatDetailActivity extends IMActivityPresenter<IChatDetailCommand, IChatDetailVu>
        implements IChatDetailCommand ,SensorEventListener {

    @Inject
    Lazy<SendTextMsg> sendTextMsg;
    
    @Inject
    Lazy<SendCustomTextMsg> sendCustomTextMsg;

    @Inject
    Lazy<SendFileMsg> sendFileMsg;
    
    @Inject
    Lazy<SendFileMsgList> sendFileMsgList;

    @Inject
    Lazy<GetMsgList> getMsgList;

    @Inject
    Lazy<ClearUnReadMsg> clearUnReadMsg;

    @Inject
    Lazy<SaveDraftUseCase> saveDraft;

    @Inject
    Lazy<GetSingleSessionConfigUseCase> getSessionConfig;

    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<GetConfig> getConfigLazy;

    @Inject
    Lazy<ContactService> contactService;

    /**
     * 每页消息展示数量
     */
    private static final int PAGE_SIZE = 15;

    /**
     * 相机权限请求码
     */
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;// TODO: 2016/12/27 guorong

    /**
     * 短视频录制权限请求码
     */
    private static final int VIDEO_PERMISSION_REQUEST_CODE = 3;
    // TODO: 2016/12/27 licong
    private static final String RECENT_APPS = "recentapps";

    private static final String REASON = "reason";
    /**
     * 消息集合
     */
    private final List<TalkMessageBean> messageList = new ArrayList<>();

    /**
     * 会话flag
     */
    private String talkFlag;
    /**
     * 聊天对象账号id
     */
    private String talkAccount;
    /**
     * 会话类型
     */
    private int talkType;

    /**
     * 当前会话配置信息（免打扰、草稿、草稿时间）
     */
    private SessionConfig sessionConfig;

    /**
     * 是否选中闪信
     */
    private boolean isShan = false;

    /**
     * 拍照图片缓存路径
     */
    private String photoFileCachePath;

    /**
     * 适配器
     */
    private ChatDetailAdapterPresenter adapterPresenter;

    //[S]modify by lll@xdja.com for add the receiver mode 2016/10/18
    /**
     * 传感器管理类
     */
    private SensorManager mSensorManager;
    private Sensor mSensor;
    /**
     * 是否拨打voip电话
     */
    private static boolean isVoipCall = false;
    //[E]modify by lll@xdja.com for add the receiver mode 2016/10/18

    /**
     * 成员数量
     */
    private int mMemberCount;

    @NonNull
    @Override
    protected Class<? extends IChatDetailVu> getVuClass() {
        return ViewChatDetail.class;
    }

    @NonNull
    @Override
    protected IChatDetailCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            talkAccount = intent.getStringExtra(ConstDef.TAG_TALKERID);
            talkFlag = intent.getStringExtra(ConstDef.TAG_TALKFLAG);
            talkType = intent.getIntExtra(ConstDef.TAG_TALKTYPE, ConstDef.CHAT_TYPE_P2P);
        }

        talkFlag = ToolUtil.getSessionTag(talkAccount, talkType);

        //无账号，直接返回
        if (TextUtils.isEmpty(talkAccount)){
            finish();
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == useCaseComponent) {
            LogUtil.getUtils().e("useCaseComponent is null");
            return;
        }
        //初始化注入
        useCaseComponent.inject(this);

        //初始化事件总线
        busProvider.register(this);

        adapterPresenter = new ChatDetailAdapterPresenter(messageList, busProvider, talkType);

        useCaseComponent.inject(adapterPresenter);

        adapterPresenter.setActivity(this);

        //fix bug 2705 by licong, reView zya, 2016/08/17
        adapterPresenter.setIsActivityIsDestroy(false);

        adapterPresenter.setListView(getVu().getDisplayList());

        adapterPresenter.refreshTalkInfo(talkFlag, talkAccount, talkType);

        getVu().initListView(adapterPresenter);

        //清除未读消息数量
        clearUnReadCount();

        //设置Title
        reSetTitle();

        //获取消息列表
        getMsgList
                .get()
                .get(talkFlag, 0, PAGE_SIZE)
                .execute(new OkSubscriber<List<TalkMessageBean>>(this.okHandler) {
                            @Override
                            public void onNext(List<TalkMessageBean> talkMessageBean) {
                                super.onNext(talkMessageBean);
                                LogUtil.getUtils().e("onBindView onNext!");
                                if (talkMessageBean != null) {
                                    //modify by zya@xdja.com ,fix bug NACTOMA-240
                                    addAllNotExistsInMsgList(-1, talkMessageBean);
                                    //messageList.addAll(talkMessageBean);
                                    //end zya@xdja.com

                                    //modify by guorong@xdja.com
                                    for(TalkMessageBean bean : talkMessageBean){
                                        if( (bean.getMessageType() == ConstDef.MSG_TYPE_FILE ||
                                                bean.getMessageType() == ConstDef.MSG_TYPE_VIDEO) && bean.getFileInfo() != null){
                                            bean.getFileInfo().setPercent(TranslateInfoUtil.getPercent(bean.get_id()));
                                            TranslateInfoUtil.remove(bean.get_id());
                                        }
                                    }
                                    //end guorong@xdja.com
                                }
                                adapterPresenter.notifyDataSetChanged();
                                getVu().setListSelection(messageList.size());
                            }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                                 LogUtil.getUtils().e("onBindView onError!");
                             }
                         }
                );

        // fix bug 2368 by licong ,review by zya@xdja.com 2016/08/03
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(broadcastReceiver, filter);
        //end

        //听筒模式初始化
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        //注册线控耳机插拔广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //保证此Activity每次被调到前台时onPrepareOptionsMenu(Menu menu)方法都会执行
        invalidateOptionsMenu();

        if (intent != null) {

            talkType = intent.getIntExtra(ConstDef.TAG_TALKTYPE, ConstDef.CHAT_TYPE_P2P);
            String account = intent.getStringExtra(ConstDef.TAG_TALKERID);
            if (TextUtils.isEmpty(account)) {
                LogUtil.getUtils().e("user account is null, so return.");
                return;
            }

            //modify by zya@xdja.com,fix bug 1711
            if (!account.equals(talkAccount)) {

                reSetTitle();

                talkAccount = intent.getStringExtra(ConstDef.TAG_TALKERID);
                talkFlag = ToolUtil.getSessionTag(talkAccount, talkType);
                //相关信息刷新
                adapterPresenter.refreshTalkInfo(talkFlag, talkAccount, talkType);
                getVu().restoreInputAction();

                //添加群聊界显示群聊人数 fix by licong, 2016/11/28
                if(contactService != null) {
                    contactService.get().getGroupInfoFromServer(talkAccount);
                }

                sessionConfig = null;//清除之前会话的配置信息
                //获取消息列表
                //end by zya
                getMsgList
                        .get()
                        .get(talkFlag, 0, PAGE_SIZE)
                        .execute( new OkSubscriber<List<TalkMessageBean>>(this.okHandler) {
                                      @Override
                                      public void onNext(List<TalkMessageBean> talkMessageBeen) {
                                          super.onNext(talkMessageBeen);
                                          if (talkMessageBeen != null) {
                                              messageList.clear();
                                              messageList.addAll(talkMessageBeen);
                                          }
                                          adapterPresenter.notifyDataSetChanged();
                                          getVu().setListSelection(messageList.size());
                                      }
                                  }
                        );
            } else if(messageList.size() > 0){
                getVu().setListSelection(messageList.size());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //添加群聊界显示群聊人数 fix by licong, 2016/11/28
        if(contactService != null) {
            contactService.get().getGroupInfoFromServer(talkAccount);
        }
        reSetTitle();
        //end fix by licong, 2016/11/28

        if (null != adapterPresenter) {
            adapterPresenter.setIsActivityShowing(true);
            adapterPresenter.notifyDataSetInvalidated();
        }
        if (null != userCache) {
            userCache.setIMPartener(this.talkAccount);
        }
        if (null != getSessionConfig) {
            getSessionConfig
                    .get()
                    .get(talkFlag)
                    .execute(new OkSubscriber<SessionConfig>(this.okHandler) {
                        @Override
                        public void onNext(SessionConfig config) {
                            super.onNext(config);
                            if (config != null) {
                                sessionConfig = config;
                                getVu().setMessageText(sessionConfig.getDraft());
                            } else {
                                //juyingang fix bug 3715 20160906 begin
                                getVu().setMessageText("");
                                //juyingang fix bug 3715 20160906 end
                            }
                        }
                    });
        }
        isVoipCall = false;
        //注册sensor监听器
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        adapterPresenter.setIsActivityShowing(false);

        //[S]modify by lll@xdja.com ,语音播放和voip交互过程中逻辑处理 2016/12/12
        //如果当前屏幕熄灭，如果当前正在播放语音熄灭，则不停止播放
        if (!MediaManager.getInstance().isWakeAcquire()) {
            //停止播放
            IMMediaPlayer.stopPlay();

            //必须使用文件路径和消息messageId同时进行判断，防止在文件发送重复的情况下，判断错误
            adapterPresenter.sendDestroyedReceiptByFilePath(IMMediaPlayer.getPlayingFile(),
                    IMMediaPlayer.getPlayingMessageId(),
                    VoicePlayState.COMPLETION.getKey());
        } else {
            //reason：华为手机熄屏时，会调用onPause方法；在语音联播时，会用到该变量，为true，才会发送状态消息
            //该条件为：手机熄屏时，当语音播放中时才会走到
            adapterPresenter.setIsActivityShowing(true);
        }
        //防止和VOIP之间切换冲突,启动VOIP时，提前恢复状态，在此就不再恢复
        if (!isVoipCall && !MediaManager.getInstance().isWakeAcquire()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaManager.getInstance().restoreAudioMode();
                }
            }, 500);
        }
        //如果此时来Voip电话，则需要释放锁，不然会导致呼叫界面手势熄屏
        if (!TelphoneState.getPhotoStateIsIdle(this) ||
                VoipFunction.getInstance().hasActiveCall()) {
            IMMediaPlayer.stopPlay();
            MediaManager.getInstance().releaseWakeLock();
        }
        //[E]modify by lll@xdja.com ,语音播放和voip交互过程中逻辑处理 2016/12/12

        clearUnReadCount();
        changeMainFrameCurrentTab(TabTipsEvent.INDEX_CHAT);

        final String draft = getVu().getInputString();//新草稿
        //保存草稿的4种情况：1，没有会话配置信息，有新草稿；2，有会话配置信息,没有旧草稿，有新草稿；
        //3，有旧草稿，没有新草稿;4,新旧草稿不相同
        if (sessionConfig != null) {//有会话配置信息
            if (!TextUtils.isEmpty(sessionConfig.getDraft())) {//有旧草稿
                if (TextUtils.isEmpty(draft) ||
                        !TextUtils.equals(draft, sessionConfig.getDraft())) {//没有新草稿或新旧草稿不相同
                    saveDraft(draft);
                } else {
                    //修改空会话问题 fix by licong,2016/9/5
                    refreshChatListLastMsg(false);
                }
            } else if (!TextUtils.isEmpty(draft)) {//没有旧草稿，有新草稿
                saveDraft(draft);
            } else {
                //修改空会话问题 fix by licong,2016/9/5
                refreshChatListLastMsg(false);
            }
        } else if (!TextUtils.isEmpty(draft)) {//没有会话配置信息有新草稿
            saveDraft(draft);
        } else {
            //修改空会话问题 fix by licong,2016/9/5
            refreshChatListLastMsg(false);
        }

        //sensor监听器反注册
        mSensorManager.unregisterListener(this);
        for(TalkMessageBean bean : messageList){
            if( (bean.getMessageType() == ConstDef.MSG_TYPE_FILE ||
                    bean.getMessageType() == ConstDef.MSG_TYPE_VIDEO) &&
                    bean.getFileInfo() != null && bean.getFileInfo().getPercent() != 0
                    && bean.getFileInfo().getPercent() != 100){

                TranslateInfoUtil.putInfo(bean.get_id() , bean.getFileInfo().getPercent());
            }
        }
    }

    /**
     * 保存草稿
     * @param draft
     */
    private void saveDraft(final String draft) {
        getConfigLazy
                .get()
                .get(ConstDef.KEY_TIME_DIFFERENT)
                .execute(new OkSubscriber<String>(this.okHandler){
                    @Override
                    public void onNext(String s) {
                        long draftTime;
                        if (!TextUtils.isEmpty(s)) {
                            draftTime = SystemClock.elapsedRealtime() +
                                    Long.parseLong(s.substring(1, s.length() - 1));
                        } else {
                            draftTime = System.currentTimeMillis();
                        }
                        saveDraft
                                .get()
                                .save(talkFlag, draft, draftTime)
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
                                        //修改空会话问题 fix by licong,2016/9/5
                                        refreshChatListLastMsg(true);
                                    }
                                });
                    }
                });
    }


    private void refreshChatListLastMsg(boolean isChange){
        //构建事件对象
        IMProxyEvent.RefreshSingleTalkEvent talkEvent
                = new IMProxyEvent.RefreshSingleTalkEvent();
        TalkListBean talkListBean = new TalkListBean();
        talkListBean.setTalkFlag(talkFlag);
        talkListBean.setTalkerAccount(talkAccount);
        talkListBean.setTalkType(talkType);
        talkListBean.setNotReadCount(0);

        if (messageList.size() > 0) {//聊天记录没有被清空,取最后一条消息
            TalkMessageBean messageBean = messageList.get(messageList.size()-1);
            talkListBean.setLastMsg(messageBean);
            talkListBean.setContent(messageBean.getContent());
            talkListBean.setLastTime(messageBean.getShowTime());
            talkListBean.setLastMsgType(ValueConverter.talkMsgTypeConvert(messageBean));
            talkEvent.setTalkListBean(talkListBean);
            //修改空会话问题 fix by licong,2016/9/5
            //发送事件
            busProvider.post(talkEvent);
        } else {//聊天记录被清空
            //修改空会话问题 fix by licong,2016/9/5
            if (isChange) {
                talkListBean.setHasDraft(true);
                talkListBean.setDraft(getVu().getInputString());
                talkEvent.setTalkListBean(talkListBean);

                //发送事件
                busProvider.post(talkEvent);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //时机问题，当点击播放之后，开始播放之前，立即退出，就会导致界面已经退出，但是还是继续播放
        IMMediaPlayer.stopPlay();
        MediaManager.getInstance().releaseWakeLock();

        // fix bug 2368 by licong ,review by zya@xdja.com 2016/08/03
        try {
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }//end

            if (headsetPlugReceiver != null) {
                unregisterReceiver(headsetPlugReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != busProvider) {
            //注销事件总线回调
            busProvider.unregister(this);
        }
        if (null != adapterPresenter) {
            adapterPresenter.onDestroy();
            //fix bug 2705 by licong, reView zya, 2016/08/17
            adapterPresenter.setIsActivityIsDestroy(true);
        }

        //when exit the activity, clear all image cache.
        ImageCache.getInstance().clearAllCache();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 清空未读消息数
     */
    private void clearUnReadCount() {
        clearUnReadMsg
                .get()
                .clear(talkFlag)
                .execute(new OkSubscriber<Integer>(this.okHandler) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                    }
                });
    }


    @Override
    public List<TalkMessageBean> getMessageList() {
        return messageList;
    }

    @Override
    public void setLimitFlagIsCheck(boolean isChecked) {
        this.isShan = isChecked;
    }

    /**
     * 打开相册获取图片
     */
    @Override
    public void startToAlbum(){

        //关闭操作面板
        getVu().restoreActionState();

        Intent intent = new Intent(this, PictureSelectActivity.class);
        startActivityForResult(intent, ConstDef.REQUEST_CODE_ALBUM);
    }

    /**
     * 启动相机拍照
     */
    @Override
    public void startToPhoto(){

        //关闭操作面板
        getVu().restoreActionState();

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            //文件保存路径
            String imageRootPath = DataFileUtils.getImageSavePath();
            File imgRootPath = new File(imageRootPath);
            if (!imgRootPath.exists()){
                imgRootPath.mkdirs();
            }
            //文件名称
            photoFileCachePath = imageRootPath +        //父路径
                    SimcUiConfig.LOCAL_PIC_PREFIX +     //前缀
                    System.currentTimeMillis() +        //名称（时间）
                    ".jpeg";                            //后缀
            //启动相机拍照
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoFileCachePath)));
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            startActivityForResult(intent, ConstDef.REQUEST_CODE_PHOTO);

            //[S] fix bug 7706 by licong for safeLock
            SafeLockUtil.setUseCameraOrFile(true);
            //[E] fix bug 7706 by licong for safeLock
        }
    }

    /**
     * 启动文件浏览界面
     */
    @Override
    public void startToFileExplorer() {
        Intent intent = new Intent(this, FileExplorerPresenter.class);
        startActivityForResult(intent, ConstDef.REQUEST_CODE_FILE);
    }

    /**
     * 启动小视频浏览界面
     */
    @Override
    public void startToVideo() {
        if (VoipFunction.getInstance().hasActiveCall()
                || VoipFunction.getInstance().isMediaPlaying()) {
            XToast toast = new XToast(this);
            toast.display(R.string.Phone_is_inCall);
            return;
        }
        //关闭操作面板
        getVu().restoreActionState();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Intent intent = new Intent(this, VideoRecorderPresenter.class);
            startActivityForResult(intent, ConstDef.REQUEST_CODE_VIDEO);
            this.overridePendingTransition(R.anim.video_activity_open,0);
        }
    }
    /**
     * 修改人 guorong
     * 时间 2016-8-2 15:38:02
     * 增加处理照相相关的权限问题的方法
     * mate8权限问题 2237
     * */
    @Override
    public void handleTakePhotoPermission(int code){
        //start fix bug 5146 by licong,reView by zya,2016/10/25
        int i = -1;
        if (Build.VERSION.SDK_INT < 23) {
            i = PermissionUtil.ALL_PERMISSION_OBTAINED;
            if (code == 1) {

                if (PermissionUtil.checkPermission( Manifest.permission.CAMERA) ==
                        PermissionUtil.REQUEST_FAILED ) {
                    showNeedPermissionDialog(Manifest.permission.CAMERA);//无照相机权限
                    i = PermissionUtil.REQUEST_FAILED;
                }
                if (PermissionUtil.checkPermission( Manifest.permission.RECORD_AUDIO) ==
                        PermissionUtil.REQUEST_FAILED ) {
                    showNeedPermissionDialog(Manifest.permission.RECORD_AUDIO);//无录音权限
                    i = PermissionUtil.REQUEST_FAILED;
                }

            } else {
                if (PermissionUtil.checkPermission(Manifest.permission.CAMERA) ==
                        PermissionUtil.REQUEST_FAILED) {
                    getVu().showPermissionDialog();
                    i = PermissionUtil.REQUEST_FAILED;
                }
            }
        } else {
            if (code == 0) {

                i = PermissionUtil.requestPermissions(this,
                        CAMERA_PERMISSION_REQUEST_CODE , Manifest.permission.CAMERA);
            } else if (code == 1) {
                String [] permissions = new String[] {Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO};
                i = PermissionUtil.requestPermissions(this,
                        VIDEO_PERMISSION_REQUEST_CODE , permissions);
            }
        }

        //end fix bug 5146 by licong,reView by zya,2016/10/25
        switch (i){
            case PermissionUtil.ALL_PERMISSION_OBTAINED:
                if (code == 0) {
                    startToPhoto();
                } else if (code == 1) {
                    startToVideo();
                }
                break;
            case PermissionUtil.PERMISSION_HAS_REFUSED:
                break;
            case PermissionUtil.REQUEST_FAILED:
                break;
            case PermissionUtil.REQUEST_SUCCESSED:
                break;

        }

    }

    @Override
    public String getTitlebarText() {
        return "";
    }


    /**
     * 进入图片预览界面
     * @param pictureInfo
     */
    private void startToPreview(LocalPictureInfo pictureInfo){

        Intent intent = new Intent(this, PicturePreviewActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<LocalPictureInfo> bundleList = new ArrayList();
        bundleList.add(pictureInfo);
        bundle.putParcelableArrayList(ConstDef.TAG_SELECTPIC, bundleList);
        bundle.putBoolean(ConstDef.FROM_TAKE_PHOTO , true);
        bundle.putInt(ConstDef.TAG_SELECTPIC_INDEX, 0);
        intent.putExtras(bundle);
        startActivityForResult(intent, ConstDef.REQUEST_CODE_PREVIEW);
    }

    /**
     * 获取文件信息
     * @param filePath
     * @return
     */
    private LocalPictureInfo generateLocalPictureInfo(String filePath){
        if (TextUtils.isEmpty(filePath)){
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()){
            return null;
        }
        String fileName = ToolUtil.getLastString(filePath, "/");
        return new LocalPictureInfo(fileName, filePath, file.length());
    }

    @Override
    public boolean sendTextMessage(final String message) {
        sendTextMsg
                .get()
                .send(this, talkAccount, message, isShan, talkType == ConstDef.CHAT_TYPE_P2G)
                .execute(new OkSubscriber<TalkMessageBean>(this.okHandler) {
                            @Override
                            public void onNext(TalkMessageBean talkMessageBean) {
                                super.onNext(talkMessageBean);
                                if (talkMessageBean != null) {
                                    messageList.add(talkMessageBean);
                                    adapterPresenter.notifyDataSetChanged();
                                    //fix bug 6680 by zya,20161206
                                    getVu().setListSelection(messageList.size());
                                    //end by zya
                                }
                            }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                             }

                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
                             }
                         }
                );
        return true;
    }

    @Override
    public boolean sendCustomTextMessage(String message, final String to) { // modified by ycm 20161229
        sendCustomTextMsg
                .get()
                .send(this, to, message, talkType == ConstDef.CHAT_TYPE_P2G)
                .execute(new OkSubscriber<TalkMessageBean>(this.okHandler) {
                             @Override
                             public void onNext(TalkMessageBean talkMessageBean) {
                                 super.onNext(talkMessageBean);
                                 //只有发送自定消息的目标账号和当前会话的账号相等时才刷新消息
                                 if (TextUtils.equals(talkAccount, to) && talkMessageBean != null) {// modified by ycm 20161229
                                     messageList.add(talkMessageBean);
                                     adapterPresenter.notifyDataSetChanged();
                                     getVu().setListSelection(messageList.size());
                                 }
                             }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                             }

                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                            }
                        }
                );
        return true;
    }


    @Override
    public void sendVoiceMessage(String path, int seconds) {

        VoiceFileInfo voiceFileInfo = new VoiceFileInfo();
        voiceFileInfo.setAmountOfTime(seconds);
        voiceFileInfo.setFilePath(path);
        voiceFileInfo.setFileName(path.substring(path.lastIndexOf("/") + 1 , path.length()));
        voiceFileInfo.setFileSize(FileSizeUtils.getFileSize(path));
        voiceFileInfo.setSuffix("amr");
        voiceFileInfo.setFileType(ConstDef.TYPE_VOICE);
        //发送文件
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileInfoList.add(voiceFileInfo);

        sendFileMsgList.get().send(this, talkAccount, isShan, talkType == ConstDef.CHAT_TYPE_P2G, fileInfoList)
                .execute(new OkSubscriber<TalkMessageBean>(this.okHandler) {
                            @Override
                            public void onNext(TalkMessageBean talkMessageBean) {
                                super.onNext(talkMessageBean);
                                if (talkMessageBean != null) {
                                    //fix bug 7818 by zya 20170104
                                    List<TalkMessageBean> beans = new ArrayList<>();
                                    beans.add(talkMessageBean);
                                    addAllNotExistsInMsgList(-1,beans);
                                    //messageList.add(talkMessageBean);
                                    //end by zya
                                    adapterPresenter.notifyDataSetChanged();
                                    //add by zya 20170329,fix bug 10643
                                    getVu().setListSelection2Last();
                                    //end by zya
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                            }
                        }
                );
    }

    @Override
    public void sendVideoMessage(VideoFileInfo videoFileInfo) {

        //发送文件
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileInfoList.add(videoFileInfo);

        //展示等待框
        if(!isFinishing()){
            getVu().showCommonProgressDialog(getString(R.string.im_file_encrpto));
        }

        sendFileMsgList.get().send(this, talkAccount, isShan, talkType == ConstDef.CHAT_TYPE_P2G, fileInfoList)
                .execute(new OkSubscriber<TalkMessageBean>(this.okHandler) {
                             @Override
                             public void onNext(TalkMessageBean talkMessageBean) {
                                 super.onNext(talkMessageBean);
                                 if (talkMessageBean != null) {
                                     messageList.add(talkMessageBean);
                                     adapterPresenter.notifyDataSetChanged();
                                     getVu().setListSelection(messageList.size());
                                 }
                                 if( !isFinishing()){
                                     getVu().dismissCommonProgressDialog();
                                 }
                             }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                                 if( !isFinishing()){
                                     getVu().dismissCommonProgressDialog();
                                 }
                             }

                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
                                 if( !isFinishing()){
                                     getVu().dismissCommonProgressDialog();
                                 }
                             }
                         }
                );

    }

    @Override
    public void sendImageMessage(List<FileInfo> fileInfoList) {
        sendFileMsgList.get().send(this, talkAccount, isShan, talkType == ConstDef.CHAT_TYPE_P2G, fileInfoList)
               .execute(new OkSubscriber<TalkMessageBean>(this.okHandler){
                   @Override
                   public void onNext(TalkMessageBean talkMessageBean) {
                       super.onNext(talkMessageBean);
                       LogUtil.getUtils().d("------------图片消息发送成功---------------------");
                       if (talkMessageBean != null){
                           LogUtil.getUtils().d("talkMessageBean:" + talkMessageBean.toString());
                           //fix bug 7818 by zya 20170104
                           List<TalkMessageBean> beans = new ArrayList<>();
                           beans.add(talkMessageBean);
                           addAllNotExistsInMsgList(-1,beans);
                           //messageList.add(talkMessageBean);
                           //end by zya
                           adapterPresenter.notifyDataSetChanged();
                       }
                   }

                   @Override
                   public void onCompleted() {
                       super.onCompleted();
                   }

                   @Override
                   public void onError(Throwable e) {
                       super.onError(e);
                   }
               });
    }

    @Override
    public void sendFileMessage(List<FileInfo> fileInfos) {
        sendFileMsgList.get().send(this , talkAccount , isShan , talkType == ConstDef.CHAT_TYPE_P2G , fileInfos)
                .execute(new OkSubscriber<TalkMessageBean>(this.okHandler){
                    @Override
                    public void onNext(TalkMessageBean talkMessageBean) {
                        super.onNext(talkMessageBean);
                        LogUtil.getUtils().d("------------文件消息发送成功---------------------");
                        if (talkMessageBean != null){
                            LogUtil.getUtils().d("talkMessageBean:" + talkMessageBean.toString());
                            //fix bug 7818 by zya 20170104
                            List<TalkMessageBean> beans = new ArrayList<>();
                            beans.add(talkMessageBean);
                            addAllNotExistsInMsgList(-1,beans);
                            //messageList.add(talkMessageBean);
                            //end by zya
                            adapterPresenter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @Override
    public void downRefreshList() {

        long msgId = 0;

        if (messageList.size() > 0) {
            TalkMessageBean messageBean = messageList.get(0);
            if (messageBean != null && messageBean.get_id() >= 0) {
                msgId = messageBean.get_id();
            }
        }

        //获取消息列表
        getMsgList
                .get()
                .get(talkFlag, msgId, PAGE_SIZE)
                .execute(new OkSubscriber<List<TalkMessageBean>>(this.okHandler) {
                            @Override
                            public void onNext(List<TalkMessageBean> talkMessageBeen) {
                                super.onNext(talkMessageBeen);
                                if (talkMessageBeen != null && !talkMessageBeen.isEmpty()) {

                                    LogUtil.getUtils().e("总消息数:" + messageList.size() + "分页查询到" + talkMessageBeen.size() +
                                            "条消息");

                                    //modify by zya,20160919
//                                    messageList.addAll(0, talkMessageBeen);
                                    addAllNotExistsInMsgList(0,talkMessageBeen);

                                    adapterPresenter.notifyDataSetChanged();
                                    //[S]modify by lixiaolong on 20160902. fix bug 3158. review by gbc.
                                    getVu().setDownRefreshSelection(talkMessageBeen.size());
                                    //[E]modify by lixiaolong on 20160902. fix bug 3158. review by gbc.
                                }
                            }

                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
                                 //add by zya@xdja.com,20160928,fix bug 4105
                                 LogUtil.getUtils().e("downRefreshList : onCompleted");
                                 getVu().stopRefresh();
                             }

                             //fix bug 4105 by licong, reView by zya, 2016/9/17
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                LogUtil.getUtils().e("downRefreshList : onError");
                                getVu().stopRefresh();
                            }//end
                        }
                );
    }


    @Override
    public int getSessionType() {
        return talkType;
    }

    /**
     * 当前用户是否在群组中
     *
     * @return
     */
    //TODO：gbc
    @Override
    public boolean getIsInGroup() {
        //TODO：来自接口判断
        //fix bug 7675 by zya 20170103
        if(contactService == null){
            return false;
        }//end by zya

        if (talkType == ConstDef.CHAT_TYPE_P2G) {
            return contactService.get().isAccountInGroup(ConstDef.PRONAME, talkAccount);
        }
        return false;
    }
    /**
     * 当前聊天对象是否为好友
     *
     * @return
     */
    @Override
    public boolean getIsFriend() {
        //fix bug 7675 by zya 20170103
        if(contactService == null){
            return false;
        }//end by zya

        if(talkType == ConstDef.CHAT_TYPE_P2P){
            return contactService.get().isFriendRelated(talkAccount) ||
                    contactService.get().isExistDepartment(talkAccount);
        }
        return false;
    }

    /**
     * 当前聊天单聊
     * @return
     */
    @Override
    public boolean getIsSingleChat() {
        return talkType == ConstDef.CHAT_TYPE_P2P;
    }

    @Override
    public void refreshMsgList() {
        //获取消息列表
        getMsgList
                .get()
                .get(talkFlag, 0, PAGE_SIZE)
                .execute(new OkSubscriber<List<TalkMessageBean>>(this.okHandler) {
                            @Override
                            public void onNext(List<TalkMessageBean> talkMessageBean) {
                                super.onNext(talkMessageBean);
                                if (talkMessageBean != null) {
                                    //modify by zya@xdja.com ,fix bug NACTOMA-240
                                    addAllNotExistsInMsgList(-1,talkMessageBean);
                                    //messageList.addAll(talkMessageBean);
                                    //end zya@xdja.com
                                }
                                adapterPresenter.notifyDataSetChanged();
                                getVu().setListSelection(messageList.size());
                            }
                        }
                );
    }


    @Override
    public String getSenderShowName(String contactId) {
        return null;
    }


    @Override
    public Bitmap getPhotoMiniMap(TalkMessageBean messageBean) {
        return null;
    }


    @Override
    public CharSequence getVoiceLength(TalkMessageBean talkMessageBean) {
        return null;
    }

    @Override
    public boolean getVoiceMessageIsPlaying(String messageId) {
        return false;
    }

    /**
     * 拨打电话
     */
    @Override
    public void call() {
        //[S]modify by lll@xdja.com for add the receiver mode 2016/12/01
        //reason:直接播放Voip电话时，要提前恢复音频状态。不然会因为生命周期问题，导致voip播放外音
        isVoipCall = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IMMediaPlayer.isPlaying()) {
                    IMMediaPlayer.stopPlay();
                    MediaManager.getInstance().restoreAudioMode();
                }
                VoipFunction.getInstance().makeCall(getApplicationContext(), talkAccount);// account是被叫安通帐号
            }
        }, 500);
        //[E]modify by lll@xdja.com for add the receiver mode 2016/12/01
    }

    @Override
    public void startSettingPage() {
        Intent intent = new Intent(this, SingleChatSettingsPresenter.class);
        intent.putExtra(ConstDef.TAG_TALKFLAG, talkFlag);
        intent.putExtra(ConstDef.TAG_TALKERID, talkAccount);
        intent.putExtra(ConstDef.TAG_TALKTYPE,talkType);
        startActivity(intent);
    }

    //TODO:gbc
    @Override
    public void startGroupSettingPage() {
        Intent intent = new Intent();
        //TODO: GroupOwnerId, CurrentAccount
        intent.putExtra(ConstDef.TAG_TALKFLAG, talkFlag);
        //intent.putExtra(ConstDef.TAG_TALKTYPE, ConstDef.CHAT_TYPE_P2G);
        intent.setClass(this, GroupChatSettingsPresenter.class);
        startActivity(intent);
    }

    /**
     * add by zya@xdja.com ,fix bug NACTOMA-240
     * 添加messageList中不存在的TalkMessageBean
     * @param location 添加位置
     * @param talkMessageBeans 需要添加的集合
     */
    private void addAllNotExistsInMsgList(int location,List<TalkMessageBean> talkMessageBeans){
        //判空和长度为0,情况处理
        if(talkMessageBeans == null || talkMessageBeans.size() == 0){
            return ;
        }

        LogUtil.getUtils().e("addAllNotExistsInMsgList start dataSource.size:" + messageList.size());
        List<TalkMessageBean> beans = new ArrayList<>(talkMessageBeans);

        //剔除在messageList中已经存在的对象
        beans.retainAll(messageList);
        if(beans.size() != 0) {
            talkMessageBeans.removeAll(beans);
        }

        //最后剩余的集合长度大于0，在去给messageList添加，否则不添加
        if(talkMessageBeans.size() != 0){
            if(location > -1){
                messageList.addAll(location,talkMessageBeans);
            } else {
                messageList.addAll(talkMessageBeans);
            }
        }

        LogUtil.getUtils().e("addAllNotExistsInMsgList end dataSource.size:" + messageList.size());
    }

    /*--------------Activity返回结果处理----------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.getUtils().d("onActivityResult requestCode:" + requestCode);
        switch (requestCode){
            case ConstDef.REQUEST_CODE_ALBUM: //相册
                //跳转至最后一行
                getVu().setListSelection2Last();
                //获取待发送图片列表，并发送消息
                if (data != null){
                    List<FileInfo> imageFileInfoList = data.getParcelableArrayListExtra(
                            ConstDef.TAG_SELECTPIC);
                    if (imageFileInfoList != null && imageFileInfoList.size() > 0){
                        sendImageMessage(imageFileInfoList);
                    }
                }
                break;

            case ConstDef.REQUEST_CODE_PHOTO: //拍照
                LocalPictureInfo pictureInfo = generateLocalPictureInfo(photoFileCachePath);
                if (pictureInfo != null){
                    startToPreview(pictureInfo);

                    //发送广播，使得图库可见
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(pictureInfo.getLocalPath()));
                    intent.setData(uri);
                    sendBroadcast(intent);
                }
                break;

            case ConstDef.REQUEST_CODE_PREVIEW://预览
                if (data != null){

                    List<FileInfo> imageFileInfoList = data.getParcelableArrayListExtra(
                            ConstDef.TAG_SELECTPIC);
                    if (imageFileInfoList != null && imageFileInfoList.size() > 0){
                        sendImageMessage(imageFileInfoList);
                    }
                }
                break;

            case ConstDef.REQUEST_CODE_SELECT://全部图片选择页面
                if(data != null){
                    Intent intent = new Intent(this , SinglePhotoPresenter.class);
                    intent.putExtra(SinglePhotoPresenter.INTENT_TAG_LAYOUT_ID , R.layout.imageshower);
                    startActivity(intent);
                }
                break;

            case ConstDef.REQUEST_CODE_FILE: //文件选择界面返回
                if (data != null) {
                    List<LocalFileInfo> fileInfoList = data.getParcelableArrayListExtra(
                            ConstDef.TAG_SELECTFILE);
                    //文件发送，按照列表进行发送
                    List<FileInfo> infos = new ArrayList<>();
                    FileInfo fileInfo;
                    for(LocalFileInfo info : fileInfoList){
                        fileInfo = new FileInfo();
                        fileInfo.setFilePath(info.getFilePath());
                        fileInfo.setFileName(info.getFileName());
                        fileInfo.setFileSize(info.getFileSize());
                        String name = info.getFileName();
                        if(null != name && !"".equals(name)){
                            String suffixs[] = name.split("\\.");
                            if(suffixs.length > 1){
                                fileInfo.setSuffix(suffixs[suffixs.length - 1]);
                            }
                        }
						infos.add(fileInfo);
                    }
                    sendFileMessage(infos);
                }
                break;
            //从文件选择界面返回
            case ConstDef.REQUEST_CODE_FILE_CHECK:
                if(data != null){
                    long msg_id = data.getLongExtra("msg_id" , -1);
                    int percent = data.getIntExtra("percent" , 0);
                    int state = data.getIntExtra("state" , ConstDef.INACTIVE);
                    for(int i=0;i<messageList.size();i++){
                        if(messageList.get(i).get_id() == msg_id){
                            FileInfo fileInfo = messageList.get(i).getFileInfo();
                            if(fileInfo != null){
                                fileInfo.setFileState(state);
                                fileInfo.setPercent(percent);
                                adapterPresenter.updateItem(i);
                            }
                        }
                    }
                }
                break;
            // Task 2632 [End]
			case ConstDef.REQUEST_CODE_VIDEO ://录制小视频
                //跳转至最后一行
                getVu().setListSelection2Last();
                if (data != null){
                    VideoFileInfo videoFileInfo = data.getParcelableExtra(
                            ConstDef.TAG_SELECTVIDEO);
                    sendVideoMessage(videoFileInfo);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length <= 0){
            return;
        }
        if(requestCode == 2){
            if(grantResults.length == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                showNeedPermissionDialog(Manifest.permission.RECORD_AUDIO);
            }
        }else if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startToPhoto();
            }else{
                showNeedPermissionDialog(Manifest.permission.CAMERA);
            }
        } else if (requestCode == VIDEO_PERMISSION_REQUEST_CODE) {
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                startToVideo();
            }else{

                for (int i=0; i<permissions.length; i++) {

                    if (Manifest.permission.CAMERA.equalsIgnoreCase(permissions[i])
                            && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        showNeedPermissionDialog(Manifest.permission.CAMERA);//无打开照相机权限
                    } else if (Manifest.permission.RECORD_AUDIO.equalsIgnoreCase(permissions[i])
                            && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        showNeedPermissionDialog(Manifest.permission.RECORD_AUDIO);//无录音权限
                    }
                }
            }
        }
    }

    /**
     * 展示需要权限Dialog,照相机、录音权限
     */
    private void showNeedPermissionDialog (String permission) {

        final CustomDialog customDialog = new CustomDialog(this);

        if (Manifest.permission.CAMERA.equalsIgnoreCase(permission)) {
            customDialog.setTitle(getString(R.string.none_camera_permission)).setMessage(
                    getString(R.string.none_camera_permission_hint));
        } else if (Manifest.permission.RECORD_AUDIO.equalsIgnoreCase(permission)) {
            customDialog.setTitle(getString(R.string.none_audio_permission)).setMessage(
                    getString(R.string .none_audio_permission_hint));
        }

        customDialog.setNegativeButton(getString(com.xdja.imp.R.string.confirm)
                , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                }).show();
    }


    /*---------------以下处理来自消息总线-----------------*/
    @Subscribe
    public void onReceiveNewMessage(IMProxyEvent.ReceiveNewMessageEvent event) {
        if (event == null) {
            return;
        }
        invalidateOptionsMenu();
        final List<TalkMessageBean> talkMessageBean = event.getTalkMessageBeansList();
        String from = event.getMsgAccount();
        if (talkMessageBean == null || !from.equals(talkAccount)) {
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int location = -1;
                int size = messageList.size();
                for (int i = 0 ; i < size; i++) {
                    TalkMessageBean bean = messageList.get(i);
                    if (talkMessageBean.get(0).getSortTime() < bean.getSortTime()) {
                        location = i;
                        break;
                    }
                }

                //modify by zya@xdja.com ,fix bug NACTOMA-240.
                addAllNotExistsInMsgList(location,talkMessageBean);
                //end zya@xdja.com

                adapterPresenter.notifyDataSetChanged();

                //fix bug NACTOMA-388 by zya@xdja.com
                int lastVisiblePosition = getVu().getDisplayList().getLastVisiblePosition();
                if(messageList.size() > PAGE_SIZE){
                    if(lastVisiblePosition <= messageList.size() - 1 && lastVisiblePosition > messageList.size() - 1 - PAGE_SIZE){
                        getVu().setListSelection(messageList.size());
                    }
                } else {
                    getVu().setListSelection(messageList.size());
                }
                //end zya@xdja.com
            }
        });
    }


    @Subscribe
    public void onDeleteMessage(IMProxyEvent.DeleteMessageEvent event) {
        //modify by zya 20161230 fix bug 7690
        if (event == null || TextUtils.isEmpty(event.getMsgAccount())) {
            LogUtil.getUtils("TAG").e("event.getMsgAccount() is null.");
            return;
        }//end by zya

        //add by zya
        List<TalkMessageBean> beans = event.getTalkMessageBeansList();
        if(beans != null){
            messageList.removeAll(beans);
            adapterPresenter.notifyDataSetChanged();
            return ;
        }//end by zya

        TalkMessageBean talkMessageBean = event.getTalkMessageBean();
        if (talkMessageBean != null) {
            final long id = talkMessageBean.get_id();
            if (messageList != null) {
                //查找匹配待删除消息
                for (int i = messageList.size() - 1; i >= 0; i--) {
                    TalkMessageBean messageBean = messageList.get(i);
                    if (messageBean != null) {
                        if (messageBean.get_id() == id) {
                            //删除列表中的数据
                            messageList.remove(i);
                            adapterPresenter.notifyDataSetChanged();

                            //add by zya@xdja.com.20160928,修复删除后调到最新位置的bug
                            int firstVisibleItem = getVu().getDisplayList().getFirstVisiblePosition();
                            getVu().setListSelection(firstVisibleItem);
                            //end
                            break;
                        }
                    }
                }
            }
        }

        if(messageList.size() == 0) {
            //add by zya
            IMProxyEvent.RefreshSingleTalkEvent talkEvent
                    = new IMProxyEvent.RefreshSingleTalkEvent();
            TalkListBean talkListBean = new TalkListBean();
            talkListBean.setTalkerAccount(talkAccount);
            talkListBean.setTalkFlag(talkFlag);
            talkListBean.setNotReadCount(0);
            talkListBean.setTalkType(talkType);
            talkEvent.setTalkListBean(talkListBean);

            //发送事件
            busProvider.post(talkEvent);
            //end
        }
    }

    @Subscribe
    public void clearMessage(SessionChangedEvent.MessageCleardEvent event) {
        //modified by ycm
        //start fix bug 5087 by licong, reView by zya, 2016/10/19
        if (messageList.size() != 0) {
            messageList.clear();
            adapterPresenter.notifyDataSetChanged();
        }
            getVu().setMessageText("");
            saveDraft("");
        //end fix bug 5087 by licong, reView by zya, 2016/10/19
    }

    @Subscribe
    public void onRefreshSingleMessage(final IMProxyEvent.RefreshSingleMessageEvent event) {
        if (event == null || event.getTalkMessageBean() == null
                || TextUtils.isEmpty(event.getMsgAccount())) {
            return;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean bean = event.getTalkMessageBean();
                changeMsgState(bean);

                if (bean.getFailCode() == ConstDef.FAIL_CHECK) {
                    //对方版本过低，发送自定义消息
                    ContactInfo info = adapterPresenter.getContactInfo(bean.getTo());
                    sendCustomTextMessage(String.format(getResources().getString(R.string.file_not_support),
                            (info == null ? "" : info.getName())), bean.getTo());// modified by ycm 20161229
                }
            }
        });
    }

    /**
     * 查找更改目标消息状态
     */
    private void changeMsgState(TalkMessageBean bean) {
        long msgId = bean.get_id();
        //start: add by ycm for bug 2984 2016/9/2 review by liming
        int state = bean.getMessageState();
        int type = bean.getMessageType();
        String content = bean.getContent();
        if (type == ConstDef.MSG_TYPE_FILE
                && (ConstDef.FILE_NAME_IMAGE).equals(content) // modified by ycm 20161201
                && state == ConstDef.STATE_DESTROYING) {
            adapterPresenter.postMsgDestory(bean);
        }
        //end: add by ycm for bug 2984 2016/9/2 review by liming

        if (messageList != null) {
            //轮询查找，修改消息状态
            for (int i = messageList.size() - 1; i >= 0; i--) {
                TalkMessageBean messageBean = messageList.get(i);
                if (messageBean != null) {
                    if (messageBean.get_id() == msgId) {
                        messageBean.setMessageState(state);
                        messageBean.setFailCode(bean.getFailCode());
                        messageBean.setContent(bean.getContent());
                        adapterPresenter.updateItem(i);
                        //fix bug 6692 by zya 20161207
                        if (i == messageList.size() - 1) {//修改最后一条消息状态之后ListView滑动到底部防止显示不完整
                            getVu().setListSelection(messageList.size());
                        }//end by zya
                        break;
                    }
                }
            }
        }
    }


    //退出并解散群时需要退出消息详情界面
    @Subscribe
    public void onReceiveClearTalkMessage(ContactProxyEvent.QuitGroupNeedClearMessageEvent clearMessageEvent) {
        //TODO: 退出消息消息详情界面
        ChangeTabIndexEvent event = new ChangeTabIndexEvent();
        event.setIndex(TabTipsEvent.INDEX_CHAT);
        this.busProvider.post(event);

        finish();
    }

    @Subscribe
    public void onReceiveClearSingleTalkMessage(ContactProxyEvent.DeletFriendClearTalkEvent
                                                        clearMessageEvent) {
        //TODO: 退出消息消息详情界面
        ChangeTabIndexEvent event = new ChangeTabIndexEvent();
        event.setIndex(TabTipsEvent.INDEX_CHAT);
        this.busProvider.post(event);

        finish();
    }

    @Subscribe
    public void onRefreshMessageList(IMProxyEvent.RefreshMessageListEvent event) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterPresenter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onSendFileFailed(IMProxyEvent.SendFileFailedEvent event) {
        //do nothing fixed by lll
        /*long eventId = event.getAttachedMsgId();
        FileInfo eventFileInfo = event.getFileInfo();
        for (int i = messageList.size() - 1; i >= 0; i--) {

            TalkMessageBean messageBean = messageList.get(i);

            if (messageBean != null) {
                if (messageBean.get_id() == eventId) {
                    if(messageBean.getFileInfo().getFileType() == ConstDef.TYPE_VOICE){
                        LogUtil.getUtils().d("message:fileType");
                    } else if(messageBean.getFileInfo().getFileType() == ConstDef.TYPE_NORMAL ||
                                messageBean.getFileInfo().getFileType() == ConstDef.TYPE_VIDEO){

                        messageBean.getFileInfo().setFileState(ConstDef.FAIL);
                        TranslateInfoUtil.remove(messageBean.get_id());
                    }

                    if (adapterPresenter.getActivityIsShowing()) {
                        adapterPresenter.updateItem(i);
                    }
                    break;
                }
            }
        }*/
    }

    @Subscribe
    public void onReceiveFileFailed(IMProxyEvent.ReceiveFileFailedEvent event) {

        LogUtil.getUtils().e("receive file failed message.");

        //更新文件状状态
        long eventId = event.getAttachedMsgId();

        FileInfo fileInfo = event.getFileInfo();

        if (fileInfo == null) {
            LogUtil.getUtils().e("Receive file failed message, but post fileInfo is null.");
            return;
        }
        //[S]modify by lll@xdja.com for update failed status 2017/3/23
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (eventId == messageList.get(i).get_id()) {

                //内存中保存的文件信息
                FileInfo fileInfoInfoBean = messageList.get(i).getFileInfo();
                if (fileInfoInfoBean == null) {
                    break;
                }

                if (fileInfo.getFileType() == ConstDef.TYPE_PHOTO) {
                    //图片下载，只有是缩略图是，才更新界面（排除高清缩略图事件）
                    ImageFileInfo imageFileInfo = (ImageFileInfo) fileInfo;
                    if (imageFileInfo.getType() != ConstDef.FILE_IS_THUMB) {
                        break;
                    }
                } else if (fileInfo.getFileType() == ConstDef.TYPE_VIDEO) {
                    //视频逻辑，控件自身处理，不用过滤
                    /*VideoFileInfo videoFileInfo = (VideoFileInfo) fileInfo;
                    if (videoFileInfo.getType() != ConstDef.FILE_IS_RAW) {
                        break;
                    }*/

                    //因为短视频下载失败，所以将短视频正在下载的集合中删除这一条记录，防止下次点击下载无效, add by jyg
                    TranslateInfoUtil.remove(messageList.get(i).get_id());
                }

                //更新显示状态为失败
                fileInfo.setFileState(ConstDef.FAIL);
                messageList.get(i).setFileInfo(fileInfo);
                //通知界面更新
                adapterPresenter.updateItem(i);
                break;
            }
        }
        //[E]modify by lll@xdja.com for update failed status 2017/3/23
    }

    @Subscribe
    public void onReceiveFileComplete(IMProxyEvent.ReceiveFileFinishedEvent event) {
        long eventId = event.getAttachedMsgId();
        FileInfo eventFileInfo = event.getFileInfo();
        for (TalkMessageBean bean : messageList) {
            if (eventId == bean.get_id() && bean.getFileInfo() != null) {

                int fileType = bean.getFileInfo().getFileType();

                switch (fileType) {
                    case ConstDef.TYPE_VOICE: //音频
                        FileInfo fileInfo = bean.getFileInfo();
                        if (fileInfo != null && !TextUtils.isEmpty(fileInfo.getFilePath())){
                            IMMediaPlayer.startPlay(fileInfo.getFilePath(),
                                                    fileInfo.getTalkMessageId(),
                                                    bean.isMine());
                        }
                        break;

                    case ConstDef.TYPE_VIDEO: //视频
						//jyg add 2017/3/14 satrt 解决第一帧下载完成，无法显示全
                        long lastMsgId = messageList.get(messageList.size() - 1).get_id();
                        if (lastMsgId == eventId) {
                            getVu().setListSelection(messageList.size() - 1);
                        }
						//jyg add 2017/3/14 end
                        return;

                    case ConstDef.TYPE_PHOTO: //图片
                        break;

                    case ConstDef.TYPE_NORMAL://普通文件
                        bean.getFileInfo().setFileState(ConstDef.DONE);
                        return;
                }

                //add by zya.20160927
                bean.setFailCode(ConstDef.DOWNLOAD_DECRYPT_SUCC);
                //状态处理
                bean.getFileInfo().setTranslateSize(eventFileInfo.getTranslateSize());
                adapterPresenter.sendReadReceipt(bean);
                break;
            }
        }
    }

    @Subscribe
    public void RecieveFilePause(final  IMProxyEvent.ReceiveFilePaused event){
        long eventId = event.getAttachedMsgId();
        int size = messageList.size();
        for(int i = 0 ; i < size; i++){
            TalkMessageBean talkMessageBean = messageList.get(i);
            if(talkMessageBean.get_id() == eventId){
                FileInfo fileInfo = talkMessageBean.getFileInfo();
                if(fileInfo != null){
                    fileInfo.setFileState(ConstDef.PAUSE);
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapterPresenter.getActivityIsShowing()) {
                                adapterPresenter.updateItem(finalI);
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    @Subscribe
    public void SendFileProgressUpdate(final IMProxyEvent.SendFileProgressUpdateEvent event){
        if(event.getFileInfo() != null && ( event.getFileInfo().getFileType() == ConstDef.TYPE_NORMAL ||
                event.getFileInfo().getFileType() == ConstDef.TYPE_VIDEO )){
            long eventId = event.getAttachedMsgId();
            int size = messageList.size();
            for(int i = 0 ; i < size; i++){
                TalkMessageBean talkMessageBean = messageList.get(i);
                if(talkMessageBean.get_id() == eventId){
                    FileInfo fileInfo = talkMessageBean.getFileInfo();
                    if(fileInfo != null){
                        fileInfo.setPercent(event.getPercent());
                        fileInfo.setFileState(ConstDef.LOADING);

                        //jyg add 2017/3/16 start 解决短视频下载进度显示
                        if (fileInfo instanceof VideoFileInfo) {
                            VideoFileInfo videoFileInfo = (VideoFileInfo) fileInfo;
                            videoFileInfo.setType(((VideoFileInfo)event.getFileInfo()).getType());
                            talkMessageBean.setFileInfo(videoFileInfo);
                        }
                        //jyg add 2017/3/16 end

                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapterPresenter.getActivityIsShowing()) {
                                    adapterPresenter.updateItem(finalI);
                                }
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    @Subscribe
    public void SendFileFinish(final IMProxyEvent.SendFileFinishedEvent event){
        long eventId = event.getAttachedMsgId();
        for(int i=0 ; i<messageList.size();i++){
            TalkMessageBean talkMessageBean = messageList.get(i);
            if(talkMessageBean.get_id() == eventId){
                FileInfo fileInfo = talkMessageBean.getFileInfo();
                if(fileInfo != null){
                    fileInfo.setFileState(ConstDef.DONE);
                    TranslateInfoUtil.remove(talkMessageBean.get_id());

                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapterPresenter.getActivityIsShowing()) {
                                adapterPresenter.updateItem(finalI);
                            }
                        }
                    });
                }
            }
        }
    }

    @Subscribe
    public void RecFileProgressUpdate(final IMProxyEvent.ReceiveFileProgressUpdateEvent event) {
        if (event.getFileInfo() != null && (event.getFileInfo().getFileType() == ConstDef.TYPE_NORMAL ||
                event.getFileInfo().getFileType() == ConstDef.TYPE_VIDEO)) {

                long eventId = event.getAttachedMsgId();
                for (int i = 0; i < messageList.size(); i++) {
                    TalkMessageBean talkMessageBean = messageList.get(i);
                    if (talkMessageBean.get_id() == eventId) {
                        FileInfo fileInfo = talkMessageBean.getFileInfo();
                        if(fileInfo != null) {
                            fileInfo.setPercent(event.getPercent());
                            fileInfo.setFileState(ConstDef.LOADING);
                            //jyg add 2017/3/15 start 解决短视频下载进度显示
                            if (fileInfo instanceof VideoFileInfo) {
                                VideoFileInfo videoFileInfo = (VideoFileInfo) fileInfo;
                                videoFileInfo.setType(((VideoFileInfo)event.getFileInfo()).getType());
                                talkMessageBean.setFileInfo(videoFileInfo);
                            }
                            //jyg add 2017/3/15 end
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                if (adapterPresenter.getActivityIsShowing()) {
                                    adapterPresenter.updateItem(finalI);
                                }
                            }
                            });
                        }
                       break;
                    }
                }
        }
    }
    @Subscribe
    public void RecFileFinished(final IMProxyEvent.ReceiveFileFinishedEvent event){
        long eventId = event.getAttachedMsgId();
        for(int i=0 ; i<messageList.size();i++){
            TalkMessageBean talkMessageBean = messageList.get(i);
            if(talkMessageBean.get_id() == eventId){
                FileInfo fileInfo = talkMessageBean.getFileInfo();
                if(fileInfo != null){
                    fileInfo.setPercent(100);
                    fileInfo.setFileState(ConstDef.DONE);
                    TranslateInfoUtil.remove(talkMessageBean.get_id());
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapterPresenter.getActivityIsShowing()) {
                                adapterPresenter.updateItem(finalI);
                            }
                        }
                    });
                }
            }
        }
    }
    //备注更新回调事件分发
    @Subscribe
    public void ReceiveRemarkUpdateEvent(ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent) {
        String account = remarkUpdateEvent.getAccount();
        String showName = remarkUpdateEvent.getShowName();
        if (TextUtils.isEmpty(account)) {
            return;
        }
        //刷新对应导航栏标题
        if (talkAccount.equals(account)) {
            reSetTitle();
        }
    }


    //群组更新
    @Subscribe
    public void GroupMemberUpdate(ContactProxyEvent.GroupUpdateEvent groupUpdateEvent) {
        String groupId = groupUpdateEvent.getoupId();
        //刷新对应的会话列表
        //刷新对应的聊天对象，刷新对应导航栏标题
        adapterPresenter.notifyDataSetChanged();
        if (talkAccount.equals(groupId)) {
            reSetTitle();
        }
    }

    @Subscribe
    public void GroupInfoUpdate(ContactProxyEvent.GetGroupInfoEvent getGroupInfoEvent) {
        String groupId = getGroupInfoEvent.getoupId();
        //添加群聊界显示群聊人数 fix by licong, 2016/11/28
        mMemberCount = getGroupInfoEvent.getMembersCount();
        //end fix by licong, 2016/11/28

        //刷新对应的聊天对象，刷新对应导航栏标题
        adapterPresenter.notifyDataSetChanged();
        if (talkAccount.equals(groupId)) {
            reSetTitle();
        }
    }

    @Subscribe
    public void ReceiveNickNameUpdateEvent(ContactProxyEvent.NickNameUpdateEvent nickNameUpdateEvent) {
        ArrayList<String> accountList = nickNameUpdateEvent.getAccounts();
        if (accountList.size() == 0) {
            return;
        }
        //刷新对应的聊天对象，刷新对应导航栏标题
        adapterPresenter.notifyDataSetChanged();
        if (accountList.contains(talkAccount)) {
            reSetTitle();
        }
    }

    @Subscribe
    public void ReceiveForwardCompletedEvent(ForwardCompletedEvent forwardCompletedEvent) {
        getMsgList
                .get()
                .get(talkFlag, 0, PAGE_SIZE)
                .execute(new OkSubscriber<List<TalkMessageBean>>(this.okHandler) {
                             @Override
                             public void onNext(List<TalkMessageBean> talkMessageBean) {
                                 super.onNext(talkMessageBean);
                                 if (talkMessageBean != null) {
                                     addAllNotExistsInMsgList(-1,talkMessageBean);
                                 }
                                 adapterPresenter.notifyDataSetChanged();
                                 getVu().setListSelection(messageList.size());
                             }
                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
                             }
                         }
                );
    }

    private void reSetTitle() {
        ContactInfo contactInfo = null;
        switch (talkType) {
            case ConstDef.CHAT_TYPE_P2P:
                contactInfo = contactService != null ? contactService.get().getContactInfo(talkAccount)
                    : null;
                break;

            case ConstDef.CHAT_TYPE_P2G:
                //去联系人模块获取群组显示名称
                contactInfo = contactService != null ? contactService.get().getGroupInfo(talkAccount)
                    : null;
                break;
        }

        String defaultName = "";
        switch (talkType) {
            case ConstDef.CHAT_TYPE_P2G:
                defaultName = getResources().getString(R.string.group_name_default);
                break;
            case ConstDef.CHAT_TYPE_P2P:
                defaultName = talkAccount;
                break;
        }
        //添加群聊界显示群聊人数 fix by licong, 2016/11/28
        //add by zya 20161130
        String groupMemberCount = mMemberCount == 0 ? "" : "（" + mMemberCount + "）";
        //end
        if (contactInfo != null) {
            LogUtil.getUtils().i("**群名称修改**:" + contactInfo.getName());
            if (talkType == ConstDef.CHAT_TYPE_P2G) {
                //modify by zya fix bug 7477 ,20161230
                if(!TextUtils.isEmpty(contactInfo.getName())){
                    defaultName = contactInfo.getName();
                }
                setTitle(defaultName + (getIsInGroup() ? groupMemberCount : ""));
            } else {
                setTitle(((TextUtils.isEmpty(contactInfo.getName())) ? defaultName: contactInfo.getName()));
            }
            //end fix by licong, 2016/11/28

        } else {
            setTitle(defaultName);
        }

        //如果听筒模式打开，则TitleBar进行提示
        if (SettingServer.isReceiverModeOn()) {
            setReceiverLogoVisible();
        }
    }

    private void changeMainFrameCurrentTab(@TabTipsEvent.POINT_DEF int index) {
        ChangeTabIndexEvent event = new ChangeTabIndexEvent();
        event.setIndex(index);
        busProvider.post(event);
    }

    // fix bug 2368 by licong ,review by zya@xdja.com 2016/08/03
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if(RegisterActionUtil.ACTION_REFRESH_LIST.equals(intent.getAction())){
                invalidateOptionsMenu();
            }
        }
    };//end


    //[S]modify by lll@xdja.com for add the receiver mode 2016/10/18
    /**
     * 线控耳机插拔广播
     */
    private final BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.hasExtra("state")) {

                    if (intent.getIntExtra("state", 0) == 1) {
                        LogUtil.getUtils().d("耳机检测：插入");
                        MediaManager.getInstance().setHeadsetOn(true);
                        MediaManager.getInstance().setReceiverMode(true);
                    }
                }
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                LogUtil.getUtils().d("耳机检测：拔出");
                MediaManager.getInstance().setHeadsetOn(false);
                if (SettingServer.isReceiverModeOn()) {
                    MediaManager.getInstance().setReceiverMode(true);
                } else {
                    MediaManager.getInstance().setReceiverMode(false);
                }
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        //情景一：VOIP通话中，则直接返回
        if (!TelphoneState.getPhotoStateIsIdle(this) ||
                VoipFunction.getInstance().hasActiveCall() ||
                VoipFunction.getInstance().isMediaPlaying()) {
            return;
        }

        //情景二：如果在听筒模式时，插上耳机，然后放开听筒，应该切换正常模式，耳机起作用
        if (MediaManager.getInstance().isHeadsetOn()) {

            if (event.values[0] == mSensor.getMaximumRange()) {//正常模式
                MediaManager.getInstance().setReceiverMode(false);
                IMMediaPlayer.startToRePlay();
                MediaManager.getInstance().onSensorChanged(false);
            }
            MediaManager.getInstance().wakeLockBrightRelease();
            return;
        }

        //情景：正在播放过程中
        if (IMMediaPlayer.isPlaying()) {
            if (event.values[0] == mSensor.getMaximumRange()) {//正常模式
                MediaManager.getInstance().wakeLockBrightRelease();
                //提示信息
                if (SettingServer.isReceiverModeOn()) {
                    getVu().onReceiverModeChanged(true);
                } else {
                    getVu().onReceiverModeChanged(false);
                }
            } else {                                           //听筒模式
                MediaManager.getInstance().wakeLockBrightAcquire();
            }
        } else {
            MediaManager.getInstance().wakeLockBrightRelease();
        }

        //情景三：听筒模式开启 或者播放已经停止，如果当前为正常模式，则需要进行相关提示
        if (SettingServer.isReceiverModeOn() || !IMMediaPlayer.isPlaying()) {

            if (event.values[0] == mSensor.getMaximumRange()) {//正常模式
                MediaManager.getInstance().setReceiverMode(false);
                //提示信息
                if (SettingServer.isReceiverModeOn()) {
                    getVu().onReceiverModeChanged(true);
                } else {
                    getVu().onReceiverModeChanged(false);
                }
            } else {
                MediaManager.getInstance().setReceiverMode(true);
            }
            return;
        }

        //情景四：听筒和正常模式之间切换
        if (event.values[0] == mSensor.getMaximumRange()) { //正常模式
            MediaManager.getInstance().setReceiverMode(false);
            IMMediaPlayer.startToRePlay();
            MediaManager.getInstance().onSensorChanged(false);
        } else {                                            //听筒模式
            MediaManager.getInstance().setReceiverMode(true);
            IMMediaPlayer.startToRePlay();
            MediaManager.getInstance().onSensorChanged(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }


    /**
     * 开始播放语音，如果在听筒模式下，则进行提示
     *
     * @param event
     */
    @Subscribe
    public void onStartPlayVoice(IMProxyEvent.PlayVoiceEvent event) {
        if (SettingServer.isReceiverModeOn()) {
            getVu().onReceiverModeChanged(true);
        }
    }

    /**
     * 设置Title听筒图标可见
     */
    private void setReceiverLogoVisible(){

        //加载之前先取消
        setReceiverLogGone();

        String title = getTitle().toString().trim();
        //设置显示Title听筒图标
        Drawable drawable = getResources().getDrawable(R.drawable.ic_tips_receiver);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        SpannableString spannableString = new SpannableString(title + "  ");//2个空格占位符，不可修改删除
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, title.length(),
                title.length() + 2,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        setTitle(spannableString);
    }

    /**
     * 设置Title听筒图标不可见
     */
    private void setReceiverLogGone(){
        //取消Title听筒图标，还原默认图标
        String title = getTitle().toString().trim();
        setTitle(title);
    }
    //[E]modify by lll@xdja.com for add the receiver mode 2016/10/18

}
