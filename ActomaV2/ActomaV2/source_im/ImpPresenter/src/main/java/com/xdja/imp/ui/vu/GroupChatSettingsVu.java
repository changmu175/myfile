package com.xdja.imp.ui.vu;

import android.content.Context;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.GroupChatSettingsCommand;


/**
 * Created by cxp on 2015/7/27.
 */
public interface GroupChatSettingsVu extends ActivityVu<GroupChatSettingsCommand> {


    /**
     * 弹出进度提示框
     */
    void showProgressDialog(String msgStr);

    /**
     * 取消弹框
     */
    void dismissDialog();

    /**
     * 设置界面聊天置顶状态
     * @param isTopChat
     */
    void setTopChatCheckBoxState(boolean isTopChat);

    /**
     * 设置界面免打扰状态
     * @param isNoDisturb
     */
    void setNoDisturbCheckBoxState(boolean isNoDisturb);



    /**
     * 设置退出按钮标题
     * @param isGroupOwner
     */
    void setExitButtonTitle(boolean isGroupOwner);

    /**
     * 初始化群组聊天顶部布局
     * @param context
     * @param groupId
     * @param account
     */
    void initGroupChatTopLayout(Context context, String groupId, String account);

}
