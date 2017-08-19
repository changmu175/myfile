package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.ChangeMsgState;
import com.xdja.imp.domain.interactor.def.DeleteMsg;
import com.xdja.imp.domain.interactor.def.GetSessionImageList;
import com.xdja.imp.domain.interactor.def.PauseReceiveFile;
import com.xdja.imp.domain.interactor.def.ResumeReceiveFile;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.ChatDetailMediaAdapter;
import com.xdja.imp.presenter.command.SinglePhotoCommand;
import com.xdja.imp.ui.SinglePhotoVu;
import com.xdja.imp.ui.vu.ISinglePhotoVu;
import com.xdja.imsdk.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;

import static com.xdja.imp.domain.model.ConstDef.TAG_TALKFLAG;
/**
 * <p>Summary: 会话详情图片小视频查看界面</p>
 * <p>Description:</p>
 * <p>Author:guorong</p>
 * <p>Date:2017/3/9</p>
 * <p>Time:15:58</p>
 */
public class SinglePhotoPresenter extends
        IMActivityPresenter<SinglePhotoCommand, ISinglePhotoVu> implements SinglePhotoCommand {
    public static final String INTENT_TAG_LAYOUT_ID = "intent-layout-id";

    private static final String LOG_TAG = "SinglePhotoPresenter";

    private List<TalkMessageBean> dataSource = new ArrayList<>();

    private ChatDetailMediaAdapter chatDetailMediaAdapter;

    @Inject
    Lazy<ChangeMsgState> changeMsgState;

    @Inject
    Lazy<GetSessionImageList> sessionImageList;

    @Inject
    Lazy<PauseReceiveFile> pauseReceiveFile;

    @Inject
    Lazy<ResumeReceiveFile> resumeReceiveFile;

    @Inject
    Lazy<DeleteMsg> deleteMsg;

    @Inject
    BusProvider busProvider;

    private String talkFlag;

    private long msgId;

    private final Handler mHandler = new Handler();

    //下载中的原图信息
    public static final Map<Long, DownloadInfo> isRawPicLoading = new HashMap<>();
    //下载中的高清缩略图
    public static final Map<Long, Boolean> isHdPicLoading = new HashMap<>();
    //会话中所有的图片信息
    private final List<ChatDetailPicInfo> imageInfos = new ArrayList<>();


    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            talkFlag = intent.getStringExtra(TAG_TALKFLAG);
            msgId = intent.getLongExtra(ConstDef.MSG_ID , -1);
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        useCaseComponent.inject(this);
        this.busProvider.register(this);
        //获取会话中的TalkMessageBean
        sessionImageList
                .get()
                .get(talkFlag)
                .execute(new Subscriber<List<TalkMessageBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TalkMessageBean> talkMessageBean) {
                        if(talkMessageBean != null && talkMessageBean.size() != 0){
                            dataSource = convertTalkMessageBean(talkMessageBean);
                            if(imageInfos.size() == 0){
                                for(TalkMessageBean messageBean : dataSource){
                                    if(messageBean.getFileInfo() != null && messageBean.getFileInfo() instanceof  ChatDetailPicInfo){
                                        imageInfos.add((ChatDetailPicInfo) messageBean.getFileInfo());
                                    }
                                }
                            }
                            chatDetailMediaAdapter =
                                    new ChatDetailMediaAdapter(SinglePhotoPresenter.this, dataSource, getVu(),
                                            busProvider);
                            useCaseComponent.inject(chatDetailMediaAdapter);
                            getVu().setAdapter(chatDetailMediaAdapter);
                            getVu().setDatasource(dataSource);
                            int curIndex = getCurindex();
                            if (curIndex == -1) {
                                return;
                            }
                            getVu().setCurPage(curIndex);
                        }
                    }
                });
    }

    private List<TalkMessageBean> convertTalkMessageBean(List<TalkMessageBean> talkMessageBean) {
        ChatDetailPicInfo chatDetailPic;
        ImageFileInfo tempFileInfo;
        FileExtraInfo extraInfo;
        for(TalkMessageBean msgBean : talkMessageBean){
            FileInfo fileInfo = msgBean.getFileInfo();
            if(fileInfo != null && fileInfo instanceof ImageFileInfo){
                tempFileInfo = (ImageFileInfo) fileInfo;
                chatDetailPic = new ChatDetailPicInfo();
                chatDetailPic.setMine(msgBean.isMine());
                chatDetailPic.setBoom(msgBean.getMessageState() == ConstDef.STATE_DESTROY);
                chatDetailPic.setTalkMessageId(msgBean.get_id());

                chatDetailPic.setFileSize(tempFileInfo.getFileSize());
                chatDetailPic.setFileName(tempFileInfo.getFileName());
                chatDetailPic.setFilePath(tempFileInfo.getFilePath());
                chatDetailPic.setFileState(tempFileInfo.getFileState());
                chatDetailPic.setFileType(tempFileInfo.getFileType());

                extraInfo = tempFileInfo.getExtraInfo();

                chatDetailPic.setThumName(tempFileInfo.getFileName());
                chatDetailPic.setThumPath(tempFileInfo.getFilePath());
                chatDetailPic.setThumSize(tempFileInfo.getFileSize());
                chatDetailPic.setThumTranslateSize(tempFileInfo.getTranslateSize());

                chatDetailPic.setHdThumPath(extraInfo.getThumbFileUrl());
                chatDetailPic.setHdThumName(extraInfo.getThumbFileName());
                chatDetailPic.setHdThumSize(extraInfo.getThumbFileSize());
                chatDetailPic.setHdThumTranslateSize(extraInfo.getThumbFileTranslateSize());

                chatDetailPic.setRawPath(extraInfo.getRawFileUrl());
                chatDetailPic.setRawName(extraInfo.getRawFileName());
                chatDetailPic.setRawSize(extraInfo.getRawFileSize());
                chatDetailPic.setRawTranslateSize(extraInfo.getRawFileTranslateSize());

                chatDetailPic.setMsgId(msgBean.get_id());
                chatDetailPic.setSuffix(tempFileInfo.getSuffix());
                msgBean.setFileInfo(chatDetailPic);
            }
        }
        return talkMessageBean;
    }

    private TalkMessageBean getTempMsgBean(TalkMessageBean talkMessageBean){
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        TalkMessageBean tempMsgBean = new TalkMessageBean();
        if(fileInfo instanceof ChatDetailPicInfo){
            ImageFileInfo imageFileInfo = mapChatpicToImageFileInfo((ChatDetailPicInfo) fileInfo ,false);
            tempMsgBean.setFileInfo(imageFileInfo);
            tempMsgBean.set_id(talkMessageBean.get_id());
            tempMsgBean.setMine(talkMessageBean.isMine());
            tempMsgBean.setCategoryId(talkMessageBean.getCategoryId());
            tempMsgBean.setCheck(talkMessageBean.isCheck());
            tempMsgBean.setContent(talkMessageBean.getContent());
            tempMsgBean.setDownloadState(talkMessageBean.getDownloadState());
            tempMsgBean.setFailCode(talkMessageBean.getFailCode());
            tempMsgBean.setFrom(talkMessageBean.getFrom());
            tempMsgBean.setGroupMsg(talkMessageBean.isGroupMsg());
            tempMsgBean.setLimitTime(talkMessageBean.getLimitTime());
            tempMsgBean.setMessageState(talkMessageBean.getMessageState());
            tempMsgBean.setProgress(talkMessageBean.getProgress());
            tempMsgBean.setMessageType(talkMessageBean.getMessageType());
            tempMsgBean.setSelect(talkMessageBean.isSelect());
            tempMsgBean.setSenderCardId(talkMessageBean.getSenderCardId());
            tempMsgBean.setShowTime(talkMessageBean.getShowTime());
            tempMsgBean.setShowTimeLine(talkMessageBean.isShowTimeLine());
            tempMsgBean.setSortTime(talkMessageBean.getSortTime());
            tempMsgBean.setTo(talkMessageBean.getTo());
            tempMsgBean.setIsBomb(talkMessageBean.isBomb());
            return tempMsgBean;
        }
        return talkMessageBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
        getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public void close() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.unregister(this);
		//jyg add 2017/3/13 start Glide回收
        Glide.get(this).clearMemory();
		//jyg add 2017/3/13 end
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @NonNull
    @Override
    protected Class getVuClass() {
        return SinglePhotoVu.class;
    }

    @NonNull
    @Override
    protected SinglePhotoCommand getCommand() {
        return this;
    }


    @Override
    public int getMsgState(long msgId) {
        int state = -1;
        for (TalkMessageBean talkMessageBean : dataSource) {
            if (talkMessageBean.get_id() == msgId) {
                state = talkMessageBean.getMessageState();
            }
        }
        return state;
    }

    @Override
    public void sendReadedState(long msgId) {
        for (final TalkMessageBean talkMessageBean : dataSource) {
            if (talkMessageBean.get_id() == msgId) {
                changeMsgState
                        .get()
                        .change(getTempMsgBean(talkMessageBean), ConstDef.STATE_READED)
                        .execute(new OkSubscriber<Integer>(null) {
                            @Override
                            public void onNext(Integer integer) {
                                super.onNext(integer);
                                if (integer == 0) {
                                    for (int i = 0; i < dataSource.size(); i++) {
                                        if (dataSource.get(i).get_id() == talkMessageBean.get_id()) {
                                            talkMessageBean.setMessageState(ConstDef.STATE_READED);
                                            dataSource.set(i, talkMessageBean);
                                            chatDetailMediaAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        });
                break;
            }
        }
    }

    private int onDeleteMessage(TalkMessageBean talkMessageBean) {
        try {
            IMProxyEvent.DeleteMessageEvent messageEvent = new IMProxyEvent.DeleteMessageEvent();
            messageEvent.setMsgAccount(talkMessageBean.getFrom());
            messageEvent.setTalkMessageBean(talkMessageBean);

            this.busProvider.post(messageEvent);
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public boolean isFileDownload(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outWidth > 0 && options.outHeight > 0;
    }

    @Override
    public Map<Long, DownloadInfo> getRawLoadMap() {
        return isRawPicLoading;
    }

    @Override
    public Map<Long, Boolean> getHdLoadMap() {
        return isHdPicLoading;
    }


    public String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormat df1 = new DecimalFormat("#");
        DecimalFormat df2 = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df1.format((int) fileS) + "B";
            if (fileSizeString.contains(".")) {
                if (fileSizeString.contains(".")) {
                    String[] s = fileSizeString.split("\\.");
                    fileSizeString = s[0] + "B";
                }
            }
        } else if (fileS < 1048576) {
            if (fileS > 1024 * 1000) {
                fileSizeString = "1MB";
            } else {
                if ((int) fileS / 1024 < 100) {
                    fileSizeString = df.format((int) fileS / 1024) + "KB";
                } else {
                    fileSizeString = df1.format((int) fileS / 1024) + "KB";
                }
            }
        } else if (fileS < 1073741824) {
            if ((double) fileS / 1048576 >= 10) {
                fileSizeString = df.format((double) fileS / 1048576) + "MB";
            } else {
                fileSizeString = df2.format((double) fileS / 1048576) + "MB";
            }
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public void downloadPic(ChatDetailPicInfo info, boolean isRaw) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("downloadPic msgId:" + info.getMsgId());
        resumeDownloadPic(info, isRaw);
    }

    @Override
    public long getCurMsgId() {
        return msgId;
    }

    @Override
    public void setCurMsgId(long msgId) {
        this.msgId = msgId;
    }

    @Override
    public void notifyAdapter() {
        if (chatDetailMediaAdapter != null) {
            chatDetailMediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteMsgs(final TalkMessageBean talkMessageBean) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("deleteMsgs msgId:" + talkMessageBean.get_id());
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(talkMessageBean.get_id());
        deleteMsg.get().delete(ids).execute(new OkSubscriber<Integer>(null) {
            @Override
            public void onNext(Integer integer) {
                if (integer == 0) {
                    onDeleteMessage(talkMessageBean);
                }
            }
        });
    }


    @Override
    public void pauseDownloadPic(ChatDetailPicInfo info, boolean isRaw) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("pauseDownloadPic msgId:" + info.getMsgId());
        FileInfo fileInfo = mapChatpicToImageFileInfo(info, isRaw);
        pauseReceiveFile.get().pause(fileInfo).execute(new OkSubscriber<Integer>(null));
    }

    @Override
    public void resumeDownloadPic(ChatDetailPicInfo info, boolean isRaw) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("resumeDownloadPic msgId:" + info.getMsgId());
        FileInfo fileInfo = mapChatpicToImageFileInfo(info, isRaw);
        resumeReceiveFile.get().resume(fileInfo).execute(new OkSubscriber<Integer>(null));
    }

    private ImageFileInfo mapChatpicToImageFileInfo(ChatDetailPicInfo info, boolean isRaw) {
        ImageFileInfo imageFileInfo = new ImageFileInfo();
        if (isRaw) {
            imageFileInfo.setFileName(info.getRawName());
            imageFileInfo.setTranslateSize(info.getRawTranslateSize());
            imageFileInfo.setFilePath(info.getRawPath());
            imageFileInfo.setFileSize(info.getRawSize());
            imageFileInfo.setType(ConstDef.FILE_IS_RAW);
        } else {
            imageFileInfo.setFileName(info.getHdThumName());
            imageFileInfo.setTranslateSize(info.getHdThumTranslateSize());
            imageFileInfo.setFilePath(info.getHdThumPath());
            imageFileInfo.setFileSize(info.getHdThumSize());
            imageFileInfo.setType(ConstDef.FILE_IS_THUMB_HD);
        }
        imageFileInfo.setTalkMessageId(info.getMsgId());
        imageFileInfo.setTalkListTag(info.getTalkId());
        imageFileInfo.setOriginal(isRaw);
        imageFileInfo.setFileType(ConstDef.MSG_TYPE_PHOTO);
        imageFileInfo.setSuffix(info.getSuffix());
        return imageFileInfo;
    }

    /**
     * 根据msgId判断当前应该显示那个page
     * @return 当前选择的界面索引
     * */
    private int getCurindex(){
        int curIndex = 0;
        for (int i = 0; i < dataSource.size(); i++) {
            FileInfo fileInfo = dataSource.get(i).getFileInfo();
            if (fileInfo != null && fileInfo.getTalkMessageId() == getCurMsgId()) {
                curIndex = i;
                break;
            }
        }
        return curIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstDef.REQUEST_CODE_SELECT://全部图片选择页面
                if(data != null){
                    setCurMsgId(data.getLongExtra(ConstDef.MSG_ID , 0));
                    getVu().setCurPage(getCurindex());
                }
                break;
        }

    }

    @Subscribe
    public void RecieveFileFinished(IMProxyEvent.ReceiveFileFinishedEvent event) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("ReceiveFileFinishedEvent msgId:" + event.getAttachedMsgId());
        long currPicMsgId = SinglePhotoVu.getCurmsgid();
        synchronized ((Long) currPicMsgId) {
            final FileInfo fileInfo = event.getFileInfo();
            if (fileInfo == null) {
                return;
            }
            ChatDetailPicInfo chatDetailPicInfo = null;
            for (ChatDetailPicInfo info : imageInfos) {
                //找到完成下载的图片，并去更新该图片已经下载的大小
                if (info.getMsgId() == fileInfo.getTalkMessageId()) {
                    if (isHdPicLoading.containsKey(fileInfo.getTalkMessageId())) {
                        info.setHdThumTranslateSize(info.getHdThumSize());
                    } else {
                        info.setRawTranslateSize(info.getRawSize());
                    }
                }
                //如果下载完成的是当前的图片
                if (info.getMsgId() == currPicMsgId) {
                    chatDetailPicInfo = info;
                }
            }

            if (currPicMsgId == fileInfo.getTalkMessageId() && chatDetailPicInfo != null) {
                if (isHdPicLoading.containsKey(currPicMsgId) && isHdPicLoading.get(currPicMsgId)) {
                    //高清图下载完成，如果能够查看原图，显示查看原图按钮
                    if (chatDetailPicInfo.getRawSize() > 0 &&
                            !isFileDownload(chatDetailPicInfo.getRawPath())
                            && !chatDetailPicInfo.isMine()) {
                        long size = chatDetailPicInfo.getRawSize();
                        final String sizeStr;
                        sizeStr = getResources().getString(R.string.view_original_image)+"(" + getFileSize(size) + ")";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新界面
                                getVu().showOriginPicBtn(sizeStr);
                            }
                        });
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //刷新界面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatDetailMediaAdapter.isNeedForceRefresh(true);
                                    chatDetailMediaAdapter.notifyDataSetChanged();
                                    getVu().showLoading(false);
                                }
                            });

                        }
                    } , 500);
                } else {

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //如果当前界面的图片是在下载原图

                            //隐藏查看原图按钮
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatDetailMediaAdapter.isNeedForceRefresh(true);
                                    chatDetailMediaAdapter.notifyDataSetChanged();
                                    getVu().hideOriginBtn();
                                }
                            });
                        }
                    } , 500);
                }

            }
            if (isHdPicLoading.containsKey(fileInfo.getTalkMessageId())) {
                isHdPicLoading.remove(fileInfo.getTalkMessageId());
            }
            if (isRawPicLoading.containsKey(fileInfo.getTalkMessageId())) {
                isRawPicLoading.remove(fileInfo.getTalkMessageId());
            }
        }
    }

    @Subscribe
    public void RecieveFileUpdated(final IMProxyEvent.ReceiveFileProgressUpdateEvent event) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("ReceiveFileProgressUpdateEvent msgId:" + event.getAttachedMsgId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FileInfo fileInfo = event.getFileInfo();
                if (fileInfo == null) {
                    return;
                }
                if (isHdPicLoading.containsKey(fileInfo.getTalkMessageId()) &&
                        isHdPicLoading.get(fileInfo.getTalkMessageId())) {
                    return;
                }
                ChatDetailPicInfo info;
                FileInfo tempFileinfo;
                for (TalkMessageBean tempPicInfo : dataSource) {
                    tempFileinfo = tempPicInfo.getFileInfo();
                    if(tempFileinfo instanceof ChatDetailPicInfo){
                        info = (ChatDetailPicInfo) tempFileinfo;
                        if (info.get_id() == event.getFileInfo().getTalkMessageId()) {
                            info.setRawTranslateSize(fileInfo.getTranslateSize());
                        }
                    }
                }
                DownloadInfo downloadInfo = isRawPicLoading.get(SinglePhotoVu.getCurmsgid());
                if (SinglePhotoVu.getCurmsgid() == fileInfo.getTalkMessageId()) {
                    if (downloadInfo != null) {
                        if (downloadInfo.isLoading) {
                            getVu().updateOriginBtnPercent(event.getPercent());

                        }
                    }
                    if (downloadInfo != null) {
                        DownloadInfo downloadInfo1 = isRawPicLoading.get(SinglePhotoVu.getCurmsgid());
                        downloadInfo.isLoading = true;
                        downloadInfo.percent = event.getPercent();
                        isRawPicLoading.put(SinglePhotoVu.getCurmsgid(), downloadInfo1);
                    }
                }
            }
        });
    }

    @Subscribe
    public void RecieveFilePaused(final IMProxyEvent.ReceiveFilePaused event) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("ReceiveFilePaused msgId:" + event.getAttachedMsgId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FileInfo fileInfo = event.getFileInfo();
                if (fileInfo != null) {
                    ChatDetailPicInfo info;
                    FileInfo tempFileinfo;
                    for (TalkMessageBean msgBean : dataSource) {
                        tempFileinfo = msgBean.getFileInfo();
                        if(tempFileinfo instanceof ChatDetailPicInfo){
                            info = (ChatDetailPicInfo) tempFileinfo;
                            if (info.getMsgId() == fileInfo.getTalkMessageId()) {
                                if (isHdPicLoading.containsKey(fileInfo.getTalkMessageId())
                                        && isHdPicLoading.get(fileInfo.getTalkMessageId())) {
                                    info.setHdThumTranslateSize(fileInfo.getTranslateSize());
                                } else {
                                    Drawable pauseIcon = getResources().getDrawable(R.drawable.origin_pic_pause_selector);
                                    //todo guorong
                                    getVu().updateOriginBtnPause(pauseIcon);
                                    info.setRawTranslateSize(fileInfo.getTranslateSize());
                                    DownloadInfo downloadInfo = isRawPicLoading.get(SinglePhotoVu.getCurmsgid());
                                    if (downloadInfo != null) {
                                        downloadInfo.isLoading = false;
                                        isRawPicLoading.put(SinglePhotoVu.getCurmsgid(), downloadInfo);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    @Subscribe
    public void RecieveFileFailed(IMProxyEvent.ReceiveFileFailedEvent event) {
        Logger.getLogger(INTENT_TAG_LAYOUT_ID).d("ReceiveFileFailedEvent msgId:" + event.getAttachedMsgId());
        if (event.getFileInfo() != null) {
            long msgId = event.getFileInfo().get_id();
            if (msgId == SinglePhotoVu.getCurmsgid()) {
                getVu().hideOriginBtn();
                getVu().showLoading(false);
            }
        }
    }

    @Subscribe
    public void FileDestory(final IMProxyEvent.RefreshSingleMessageEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean talkMessageBean = event.getTalkMessageBean();
                if(talkMessageBean != null && talkMessageBean.isBomb() &&
                        talkMessageBean.getMessageState() == ConstDef.STATE_DESTROYING){
                    Logger.getLogger(LOG_TAG).d("DestroyedEvent msgId:" + event.getTalkMessageBean().get_id());
                    ChatDetailPicInfo info;
                    FileInfo fileInfo;
                    for (int i = 0; i < dataSource.size(); i++) {
                        if (dataSource.get(i).get_id() == event.getTalkMessageBean().get_id()) {
                            fileInfo = dataSource.get(i).getFileInfo();
                            if(fileInfo != null && fileInfo instanceof ChatDetailPicInfo){
                                info = (ChatDetailPicInfo) fileInfo;
                                if (isHdPicLoading.containsKey(info.getMsgId())) {
                                    pauseDownloadPic(info, false);
                                }
                                if (isRawPicLoading.containsKey(info.getMsgId())) {
                                    pauseDownloadPic(info, true);
                                }
                                info.setBoom(true);
                                chatDetailMediaAdapter.isNeedForceRefresh(true);
                                chatDetailMediaAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    public static class DownloadInfo {
        public boolean isLoading;
        public int percent;

        public DownloadInfo(boolean isLoading, int percent) {
            this.isLoading = isLoading;
            this.percent = percent;
        }
    }
}
