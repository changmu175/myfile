package com.xdja.imp.presenter.command;

import com.xdja.imp.domain.model.ChatDetailPicInfo;

/**
 * Created by guorong on 2016/7/7.
 */
public interface ChatDetailPicPreviewCommand {

    void longClickPic(ChatDetailPicInfo picInfo , int index);

    void showLoading(boolean flag);

    void downloadPic(ChatDetailPicInfo info , boolean isRaw);

    void pauseDownloadPic(ChatDetailPicInfo info , boolean isRaw);

    void resumeDownloadPic(ChatDetailPicInfo info , boolean isRaw);

    void popdismiss();

    void hideOriginBtn();
    int getMsgState(long id);

    void sendReadedState(long id);
}
