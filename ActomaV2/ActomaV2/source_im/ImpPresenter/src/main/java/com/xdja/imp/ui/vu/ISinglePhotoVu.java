package com.xdja.imp.ui.vu;

import android.graphics.drawable.Drawable;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.presenter.adapter.ChatDetailMediaAdapter;
import com.xdja.imp.presenter.command.SinglePhotoCommand;

import java.util.List;

/**
 * <p>Summary: 会话详情图片小视频查看界面</p>
 * <p>Description:</p>
 * <p>Author:guorong</p>
 * <p>Date:2017/3/9</p>
 * <p>Time:15:58</p>
 */
public interface ISinglePhotoVu extends ActivityVu<SinglePhotoCommand> {
    void setAdapter(ChatDetailMediaAdapter adapter);

    void onActivityFinish();

    void hideOriginBtn();

    int getMsgState(long msgId);

    void sendReadedState(long msgId);

    void showOriginPicBtn(String sizeStr);

    void hideOriginPicBtn();
    void updateOriginBtnPercent(int percent);

    void updateOriginBtnPause(Drawable icon);

    void showLoading(boolean flag);

    void showNopic();

    void selectPage(int index);

    boolean isViewPagerNull();

    void setCurPage(int index);

    void setDatasource(List<TalkMessageBean> datasource);

    List<ChatDetailPicInfo> getImageInfos();

    void deleteMsg(TalkMessageBean talkMessageBean);

    void dismissPopupwindow();

    void removeMsg(TalkMessageBean talkMessageBean);
}
