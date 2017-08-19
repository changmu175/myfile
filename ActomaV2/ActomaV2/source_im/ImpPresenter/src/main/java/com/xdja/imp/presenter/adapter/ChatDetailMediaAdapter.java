package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.DownloadFile;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.frame.mvp.presenter.BaseViewPagerAdapter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.activity.SinglePhotoPresenter;
import com.xdja.imp.presenter.command.IChatDetailMediaCommand;
import com.xdja.imp.ui.ImageViewPagerVu;
import com.xdja.imp.ui.ViewVideoPreview;
import com.xdja.imp.ui.vu.FilePreviewView;
import com.xdja.imp.ui.vu.ISinglePhotoVu;
import com.xdja.imp.util.DataFileUtils;
import com.xdja.imp.util.SimcUiConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by guorong on 2017/3/1.
 */

public class ChatDetailMediaAdapter extends
        BaseViewPagerAdapter<IChatDetailMediaCommand, TalkMessageBean> implements IChatDetailMediaCommand {

    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<DownloadFile> downloadFile;

    @Inject
    Lazy<UserCache> useCache;

    private Activity activity;
    private ISinglePhotoVu photoVuCommand;

    public ChatDetailMediaAdapter(Activity context, List<TalkMessageBean> datasource, ISinglePhotoVu command,
                                  BusProvider busProvider) {
        super(context, datasource);
        this.activity = context;
        this.busProvider = busProvider;

        this.busProvider.register(this);
        photoVuCommand = command;
    }

    @Override
    public Class<? extends FilePreviewView> getViewFromType(@ConstDef.MediaType int type) {
        Class<? extends FilePreviewView> vuClass = null;
        switch (type) {
            case ConstDef.IMAGE_ITEM:
                vuClass = ImageViewPagerVu.class;
                break;
            case ConstDef.TINY_VIDEO_ITEM:
                vuClass = ViewVideoPreview.class;
                break;
        }
        return vuClass;
    }

    @Override
    public int getType(TalkMessageBean talkMessageBean) {
        int type = ConstDef.NORMAL_ITEM;
        if (talkMessageBean != null && talkMessageBean.getFileInfo() != null) {
            switch (talkMessageBean.getFileInfo().getFileType()) {
                case ConstDef.TYPE_PHOTO:
                    type = ConstDef.IMAGE_ITEM;
                    break;
                case ConstDef.TYPE_VIDEO:
                    type = ConstDef.TINY_VIDEO_ITEM;
                    break;
            }
        }

        return type;
    }

    @Override
    public IChatDetailMediaCommand getCommand() {
        return this;
    }

    @Override
    public void onPause(int position) {
        if (adapterVus != null && adapterVus.containsKey(position)) {
            adapterVus.get(position).onPause();
        }
    }

    @Override
    public void onResume(int position) {
    }

    @Override
    public void onDestroy() {
        if (null != busProvider) {
            busProvider.unregister(this);
        }
    }

    @Override
    public void onPageSelected(int lastPos, int curPos) {
        if (adapterVus != null && adapterVus.containsKey(lastPos)) {
            adapterVus.get(lastPos).onPageSelected(lastPos, curPos);
        }
    }

    @Override
    public void isNeedForceRefresh(boolean isForce) {
        this.isForce = isForce;
    }

    @Override
    public void longClick(final TalkMessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        final FileInfo fileInfo = messageBean.getFileInfo();
        if (fileInfo == null) {
            return;
        }

        View layout = activity.getLayoutInflater().inflate(R.layout.chatdetail_pic_preview_dialog, null);
        final Dialog dialog = new Dialog(activity, R.style.AppDialogTheme);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        TextView saveBtn = (TextView) layout.findViewById(R.id.save_pic);
        TextView delBtn = (TextView) layout.findViewById(R.id.del_pic);

        if (fileInfo.getFileType() == ConstDef.TYPE_PHOTO) {

            final ChatDetailPicInfo info = (ChatDetailPicInfo) fileInfo;
            if (SinglePhotoPresenter.isHdPicLoading.containsKey(info.getMsgId())
                    || SinglePhotoPresenter.isRawPicLoading.containsKey(info.getMsgId())) {
                return;
            }

        } else if (fileInfo.getFileType() == ConstDef.TYPE_VIDEO) {

            saveBtn.setText(activity.getResources().getString(R.string.save_video));
            delBtn.setText(activity.getResources().getString(R.string.del_video));
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                saveFile(messageBean);
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                removeFile(messageBean);
                deleteMsg(messageBean);

            }
        });
        dialog.show();
    }

    @Override
    public void popdismiss() {
        photoVuCommand.dismissPopupwindow();
    }

    @Override
    public int getMsgState(long msgId) {
        return photoVuCommand.getMsgState(msgId);
    }

    @Override
    public void sendReadedState(long msgId) {
        photoVuCommand.sendReadedState(msgId);
    }

    @Override
    public void hideOriginBtn() {
        photoVuCommand.hideOriginBtn();
    }

    @Override
    public int getFirstItem() {
        return super.getFirstPos();
    }

    @Override
    public void setFirstItem(int pos) {
        super.setFirstPos(pos);
    }

    @Override
    public void downLoadVideo(VideoFileInfo videoFileInfo) {
        videoFileInfo.setType(ConstDef.FILE_IS_RAW);
        List<FileInfo> videoInfos = new ArrayList<>();
        videoInfos.add(videoFileInfo);
        downloadFile.get().downLoad(videoInfos).execute(new OkSubscriber<Integer>(null));
        photoVuCommand.showLoading(true);
    }

    @Override
    public void isForceRefresh(boolean isForce) {
        isNeedForceRefresh(isForce);
    }

    private void saveFile(TalkMessageBean talkMessageBean) {
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if (fileInfo instanceof ChatDetailPicInfo) {
            savePic((ChatDetailPicInfo) fileInfo);
        } else if (fileInfo instanceof VideoFileInfo) {
			//jyg add 2017/3/14 start 当短视频未被下载时，无法进行保存本地
			//先进行下载
            VideoFileInfo videoFileInfo = (VideoFileInfo) fileInfo;
            FileExtraInfo extraInfo = videoFileInfo.getExtraInfo();

            if (extraInfo == null) {
                return;
            }

            String rawPath = extraInfo.getRawFileUrl();
            File file = new File(rawPath);
			
            //当短视频是自己发出，或者已经下载完成，可以进行保存
            if (talkMessageBean.isMine() ||
                    file.exists() && file.length() == extraInfo.getRawFileSize()) {
                saveVideo((VideoFileInfo) fileInfo);
            } else {
                //视频未下载进行短视频的下载
                downLoadVideo(videoFileInfo);
            }
			//jyg add 2017/3/14 end
        }
    }

    /**
     * 保存短视频到本地
     * @param videoFileInfo
     */
    private void saveVideo (VideoFileInfo videoFileInfo){
        String newPath = DataFileUtils.getVideoSaveToPhonePath() + //路径
                            SimcUiConfig.LOCAL_VIDEO_PREFIX +     //前缀
                            System.currentTimeMillis() +        //名称（时间）
                            "." + videoFileInfo.getSuffix();

        String oldPath = videoFileInfo.getExtraInfo().getRawFileUrl();
        DataFileUtils.saveFileToSDCard(ConstDef.TYPE_VIDEO, oldPath, newPath, activity);
    }

    private void savePic(final ChatDetailPicInfo picInfo) {
        String newPath = DataFileUtils.getImageSavePath() +
                SimcUiConfig.LOCAL_PIC_PREFIX +
                System.currentTimeMillis() +
                "." + picInfo.getSuffix();
        String oldPath;
        boolean isShan = false;
        for (TalkMessageBean bean : datasource) {
            if (bean.get_id() == picInfo.getMsgId()) {
                if (bean.isBomb()) {
                    isShan = true;
                    break;
                }
            }
        }
        if (isShan) {
            oldPath = "";
        } else if(isFileDownload(picInfo.getRawPath())){
            oldPath = picInfo.getRawPath();
        }else if(isFileDownload(picInfo.getHdThumPath())){
            oldPath = picInfo.getHdThumPath();
        }else{
            oldPath = picInfo.getThumPath();
        }
        DataFileUtils.saveFileToSDCard(ConstDef.TYPE_PHOTO, oldPath, newPath, activity);
    }

    private void removeFile(TalkMessageBean talkMessageBean) {
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        if (fileInfo instanceof ChatDetailPicInfo) {
            removePic((ChatDetailPicInfo) fileInfo);
        } else if (fileInfo instanceof VideoFileInfo) {

        }
        photoVuCommand.removeMsg(talkMessageBean);
    }



    private void removePic(ChatDetailPicInfo info) {
        File thumPic = null;
        File hdThumPic = null;
        File rawPic = null;
        if (info.getThumPath() != null) {
            thumPic = new File(info.getThumPath());
        }
        if (info.getHdThumPath() != null) {
            hdThumPic = new File(info.getHdThumPath());
        }
        if (info.getRawPath() != null) {
            rawPic = new File(info.getRawPath());
        }
        if (thumPic != null && thumPic.exists()) {
            thumPic.delete();
        }
        if (hdThumPic != null && hdThumPic.exists()) {
            hdThumPic.delete();
        }
        if (rawPic != null && rawPic.exists() && !info.isMine()) {
            rawPic.delete();
        }

    }

    public void deleteMsg(final TalkMessageBean talkMessageBean) {
        photoVuCommand.deleteMsg(talkMessageBean);
    }

    private boolean isFileDownload(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outWidth > 0 && options.outHeight > 0) {
            return true;
        }
        return false;
    }

    @Subscribe
    public void onReceiveFileComplete(IMProxyEvent.ReceiveFileFinishedEvent event) {
        long eventId = event.getAttachedMsgId();
        for (TalkMessageBean bean : datasource) {
            if (eventId == bean.get_id() && bean.getFileInfo() != null) {

                int fileType = bean.getFileInfo().getFileType();

                switch (fileType) {
                    case ConstDef.TYPE_VIDEO: //视频
                        photoVuCommand.showLoading(false);
                        return;
                }
            }
        }
    }

}