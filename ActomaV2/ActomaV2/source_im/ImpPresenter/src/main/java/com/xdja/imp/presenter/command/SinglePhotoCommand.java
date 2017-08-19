package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.presenter.activity.SinglePhotoPresenter;

import java.util.Map;

/**
 * Created by guorong on 2017/2/27.
 */

public interface SinglePhotoCommand extends Command {
    int getMsgState(long msgId);

    void sendReadedState(long msgId);

    boolean isFileDownload(String path);

    Map<Long, SinglePhotoPresenter.DownloadInfo> getRawLoadMap();

    Map<Long, Boolean> getHdLoadMap();

    String getFileSize(long fileS);

    void pauseDownloadPic(ChatDetailPicInfo info, boolean isRaw);

    void resumeDownloadPic(ChatDetailPicInfo info, boolean isRaw);

    void downloadPic(ChatDetailPicInfo info, boolean isRaw);

    long getCurMsgId();

    void setCurMsgId(long msgId);

    void notifyAdapter();

    void deleteMsgs(TalkMessageBean messageBean);

}
