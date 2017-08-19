package com.xdja.imp.presenter.command;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by cxp on 2015/7/27.
 */
public interface GroupChatSettingsCommand extends Command {

    /**
     * 更新置顶聊天状态值
     *
     * @param isTopChat
     */
    void updateTopChatState(boolean isTopChat);

    /**
     * 更新设置聊天免打扰状态值
     *
     * @param isNoDisturb
     */
    void updateChatNoDisturbState(boolean isNoDisturb);



    /**
     * 清除聊天记录
     */
    void cleanAllGroupChatMessages();


    /**
     * 退出并解散群聊
     */
    void exitGroupChatAndDissolveGroup();


    /**
     * 判断是否是群主
     * @return
     */
    boolean judgeIsGroupOwner();

    /**
     * 设置置顶聊天选项状态
     * @return
     */
    void setTopChatCheckBoxState(boolean isTopShow);

    /**
     * 设置免打扰选项状态
     * @return
     */
    void setNoDisturbCheckBoxState(boolean isNoDisturb);


    /**
     * 初始化群组聊天顶部布局
     * @param context
     * @param groupId
     * @param account
     */
    void initGroupChatTopLayout(Context context, String groupId, String account);


    /**
     * 获取是否置顶显示
     * @return
     */
    boolean getIsShowOnTop();

    /**
     * 获取是否免打扰
     * @return
     */
    boolean getNoDisturb();



    /**
     * 获取联系人接口代理
     * @return
     */
    //ContactService getContactService();

    /**
     * 获取群组信息Fragment
     * @param groupId
     * @return
     */
    Fragment getGroupInfoDetailManager(String groupId);
}
