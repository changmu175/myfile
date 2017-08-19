package com.xdja.contactopproxy;

import android.support.v4.app.Fragment;

import com.xdja.contactopproxy.Bean.ContactInfo;

import java.util.ArrayList;

/**
 * Created by liyingqing on 16-3-22.
 */
public interface ContactService {

    /**
     *判断是否存在好友关系
     * @param account 用户账号
     * @return boolean true : 存在好友关系;false : 不存在好友关系
     */
    public boolean isFriendRelated(String account);


    /**
     * 判断是否存在当前集团
     * @param account 用户账号
     * @return boolean  true : 存在于集团当中 ; false : 不存在于集团当中
     */
    public boolean isExistDepartment(String account);

    /**
     * 根据联系人账号查询对应的账号显示信息
     * @param account 账号
     * @return ContactInfo
     */
    public ContactInfo getContactInfo(String account);

    /**
     * 根据群组查询对应的群组信息
     * @param groupId 群ID
     *
     * 通过GetGroupInfoEvent方式回调给调用方
     */
    public void getGroupInfoFromServer(String groupId);

    /**
     * 判断当前账号是否存在于指定的群组中
     *
     * @param account 校验的账号
     *
     * @param groupId 指定的群id
     *
     * @return boolean  true : 存在于群组当中; false : 不存在于群组中
     */
    public boolean isAccountInGroup(String account,String groupId);

    /**
     * 启动选择联系人界面
     * @param groupId 群组ID（创建群组时传入 null,二人聊天时也是传入null）
     *
     * @param accounts 目前群中的账号集合（创建群组时传入 null,二人聊天时传入聊天对方的账号）
     */
    public void startChooseActivity(String groupId,ArrayList<String> accounts);

    /**
     * 获取群组信息管理界面
     * @param groupId 群
     * @return Fragment 群组信息详情界面
     */
    public Fragment getGroupInfoDetailManager(String groupId);

    /**
     * 打开联系人详情界面
     * @param account 要查看的联系人账号
     */
    public void startContactDetailActivity(String account);

    /**
     * 退出或者解散群
     * @param groupId 群ID
     * @param account 账号
     *
     * 通过QuitAndDismissEvent方式回调给调用方
     */
    public void quitOrDismissGroup(String groupId,String account);

    /**
     * 判断用户是否是群主
     * @param groupId 群组ID
     * @return boolean  true : 当前用户是群主 ; false : 当前用户不是群主
     */
    public boolean isGroupOwner(String groupId);

    /**
     * 根据群ID和成员账号查询对应的群成员信息
     * @param groupId 群ID
     * @param account 成员账号
     * @return ContactInfo
     */
    public ContactInfo GetGroupMemberInfo(String groupId,String account);

    /**
     * 根据群id查询对应的群信息
     * @param groupId 群ID
     * @return 根据群id查询对应的群信息
     */
    public ContactInfo getGroupInfo(String groupId);

}
