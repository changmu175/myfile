package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.SetNicknameCommand;

/**
 * Created by ldy on 16/5/3.
 */
public interface VuSetNickname extends ActivityVu<SetNicknameCommand> {
    /**
     * 填充原来的昵称
     * @param nickName
     */
    void  setNickName(String nickName);
}
