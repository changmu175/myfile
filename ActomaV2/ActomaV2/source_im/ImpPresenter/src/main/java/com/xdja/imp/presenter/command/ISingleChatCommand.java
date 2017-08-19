package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.contactopproxy.Bean.ContactInfo;

/**
 * Created by wanghao on 2015/12/3.
 */
public interface ISingleChatCommand extends Command {

    /**
     * 打开选择联系人列表界面
     */
    void openPersonListActivity();


    /**
     * 更新是否置顶聊天状态值
     *
     * @param isTopChat
     */
    void updateTopChatCheckBoxState(boolean isTopChat);

    /**
     * 更新设置聊天免打扰状态值
     *
     * @param isNoDisturb
     */
    void updateChatNoDisturbCheckBoxState(boolean isNoDisturb);


    /**
     * 清除聊天记录
     */
    void cleanAllSingleChatMessages();

    /**
     * 打开联系人详情界面
     */
    void openChatDetailInterface();

    ContactInfo getContactInfo(String account);
	//add by zya
    void openHistoryFileListActivity();
}
