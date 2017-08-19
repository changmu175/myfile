package com.xdja.imp.ui.vu;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.ISingleChatCommand;

/**
 * Created by wanghao on 2015/12/3.
 */
public interface ISingleChatSettingVu extends ActivityVu<ISingleChatCommand> {
    /**
     * 弹出进度提示框
     */
    void showProgressDialog(String msgStr);

    /**
     * 取消弹框
     */
    void dismissDialog();

    /**
     * 设置置顶聊天选项状态
     * @return
     */
    void setTopChatCheckBoxState(boolean isTop);

    /**
     * 设置免打扰选项状态
     * @return
     */
    void setNoDisturbCheckBoxState(boolean isNoDisturb);

    void setNickName(String name);

    void isShowAddFriendBtn(boolean isShow);

    void setPartnerImage(String url);
}
