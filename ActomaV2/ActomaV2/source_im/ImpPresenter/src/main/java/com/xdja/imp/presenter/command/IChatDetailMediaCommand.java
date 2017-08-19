package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;

/**
 * Created by guorong on 2017/3/1.
 */

public interface IChatDetailMediaCommand extends Command {

    void longClick(TalkMessageBean messageBean);

    void popdismiss();

    int getMsgState(long msgid);

    void sendReadedState(long msgid);

    void hideOriginBtn();

    int getFirstItem();

    void setFirstItem(int pos);

    void downLoadVideo(VideoFileInfo videoFileInfo);

    void isForceRefresh(boolean isForce);
}
