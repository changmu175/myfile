package com.xdja.imp.presenter.command;


import com.xdja.frame.presenter.mvp.Command;
import com.xdja.contactopproxy.Bean.ContactInfo;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.refctor.cmd</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:15:19</p>
 */
public interface ChatListAdapterCommand extends Command {

    /**
     * 获取账号
     * @param account
     * @return
     */
    ContactInfo getContactInfo(String account);

    /**
     * 根据群id获取群信息
     * @param groupId
     * @return
     */
    ContactInfo getGroupInfo(String groupId);


    /**
     * 获取群成员信息
     *@param groupId
     * @param account
     * @return
     */
    ContactInfo getGroupMemberInfo(String groupId, String account);
}
