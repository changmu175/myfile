package com.xdja.contact.service;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wanghao on 2016/2/25.
 *
 */
public class FireEventUtils {

    //群组事件监听
    public static final List<IContactEvent> CONTACT_LISTENERS = new CopyOnWriteArrayList<>();

    public static void addGroupListener(IContactEvent listener) {
        if (! CONTACT_LISTENERS.contains(listener)) {
             CONTACT_LISTENERS.add(listener);
        }
    }

    public static void removeGroupListener(IContactEvent listener) {
         CONTACT_LISTENERS.remove(listener);
    }


    /**
     * 推送业务收到群创建动作
     * @param groupMap
     */
    public static void pushFireAddGroupEvent(Map<String,Group> groupMap,List<Group> addGroups){
        if(!ObjectUtil.mapIsEmpty(groupMap)){
            for (ListIterator<Group> iterator = addGroups.listIterator(); iterator.hasNext(); ) {
                Group group = iterator.next();
                Group value = groupMap.get(group.getGroupId());
                if(!ObjectUtil.objectIsEmpty(value)) {
                    iterator.remove();
                }
            }
        }
        fireAddGroupEvent(addGroups);
    }

    /**
     * <pre>
     * 群组创建动作通知
     * @param addedGroups
     */
    public static void fireAddGroupEvent(Collection<Group> addedGroups){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            if (ObjectUtil.collectionIsEmpty(addedGroups)) {
                break;
            }
            listener.onEvent(IContactEvent.EVENT_GROUP_ADDED, addedGroups, null, null);
        }
    }

    //[S]modify by tangsha@20161228 for 7490
    public static void pushFireAddMemberEvent(Map<String, String> updateSerialMap, List<GroupMember> addMembers, Map<String, String> deleteGroupMember) {
        List<Group> addGroups = new ArrayList<>();//需要给哪些群发事件。
        for (ListIterator<GroupMember> iterator = addMembers.listIterator(); iterator.hasNext();) {
            GroupMember groupMember = iterator.next();
            if(ObjectUtil.stringIsEmpty(groupMember.getAccount()) || ObjectUtil.stringIsEmpty(groupMember.getGroupId()))
                continue;
            if(judgeGroupMemberAccountSerialIsZero(groupMember, updateSerialMap) || judgeGroupMemberIsDel(groupMember, deleteGroupMember)) {
                LogUtil.getUtils().d("20161228 FireEventUtils pushFireAddMemberEvent " + groupMember.getGroupId()+"#"+groupMember.getAccount()+"#"+groupMember.getInviteAccount());
                iterator.remove();
            } else {
                //[S] add by ysp, review tangsha
                Group tmpGroup = new Group();
                tmpGroup.setGroupId(groupMember.getGroupId());
                if (!addGroups.contains(tmpGroup)) {
                    addGroups.add(tmpGroup);
                }
                //[E] add by ysp, review tangsha
            }
        }
        if (!ObjectUtil.collectionIsEmpty(addGroups)) {
            pushFireUpdateGroupList(addGroups);  //add by ysp.
        }
        fireAddMemberEvent(addMembers);
    }
	//[E]modify by tangsha@20161228 for 7490

    /**
     * 判断群成员是否已经被移除群组
     * @param groupMember        群成员对象
     * @param deleteGroupMember  已经删除群成员Map集合，key:groupId#account  value:groupId#account
     * @return
     */
    public static boolean judgeGroupMemberIsDel(GroupMember groupMember, Map<String, String> deleteGroupMember) {
        if(ObjectUtil.mapIsEmpty(deleteGroupMember)) return false;
        return !ObjectUtil.stringIsEmpty(deleteGroupMember.get(groupMember.getGroupId()+"#"+ContactUtils.getCurrentAccount()));
    }

    /**
     * 判断群成员在群成员表里的序列号是否为空
     * @param groupMember      群成员对象
     * @param updateSerialMap  群成员表中序列号为0的Map集合。 key : groupId#account#inviteAccount, value : groupId#account#inviteAccount;
     * @return
     */
    public static boolean judgeGroupMemberAccountSerialIsZero(GroupMember groupMember, Map<String, String> updateSerialMap) {
        if(ObjectUtil.mapIsEmpty(updateSerialMap)) return false;
        return !ObjectUtil.stringIsEmpty(updateSerialMap.get(groupMember.getGroupId()+"#"+groupMember.getAccount()+"#"+groupMember.getInviteAccount()));
    }


    /***
     * 发出通知群成员添加
     * @param addedMembers
     */
    public static void fireAddMemberEvent(Collection<GroupMember> addedMembers){
        String currentAccount = GroupUtils.getCurrentAccount(ActomaController.getApp());
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_MEMBER_ADDED, addedMembers, currentAccount, null);
        }
    }



    /**
     * 推送发出通知群解散
     * 在当前群成员内查询 如果当前用户已经在群组内被移除,则在次增量到群解散动作时不做提醒
     * @param deletedGroups
     */
    public static void pushFireDismissGroupEvent(Map<String,String> groupMemberMap,Collection<Group> deletedGroups){
        Map<String,String> groupIdMap = new HashMap<>();  //groupId:groupId
        String currentAccount = ContactUtils.getCurrentAccount();
        for(String key : groupMemberMap.keySet()){
            String[] keyArray = key.split("#");
            if(keyArray[1].equals(currentAccount)) { //当前用户已经在keyArray[1],表示用户已经被移除群组、退出群组
                groupIdMap.put(keyArray[0], keyArray[0]);
            }
        }
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            for(Group group : deletedGroups){
                String groupId = group.getGroupId();
                String value = groupIdMap.get(groupId);
                if(ObjectUtil.stringIsEmpty(value)){
                    listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, group.getGroupId(), group.getGroupOwner(), IContactEvent.DISMISS);
                }
            }
        }
    }

    /**
     * <pre>
     * 这里推送有人员退出群提示包括两端
     * 1. 当群主收到这个数据时-->提示xxx退出了群组
     * 2. 当群内成员收到这个数据时应该提示两种:
     *    <li>群主把xxx移除了群组<li/>
     *    <li>xxx退出了群组<li/>
     * 但是当前数据或者说结构没办法做到精确提醒
     * @param deleteSerialMap
     * </pre>
     */
    public static void pushFireDeleteMemberEvent(Map<String,String> deleteSerialMap,List<GroupMember> deleteMembers){
        if (!ObjectUtil.mapIsEmpty(deleteSerialMap)) {
            List<Group> deleteGroups = new ArrayList<>();
            for (ListIterator<GroupMember> iterator = deleteMembers.listIterator(); iterator.hasNext(); ) {
                GroupMember groupMember = iterator.next();
                if (ObjectUtil.stringIsEmpty(groupMember.getAccount()) || ObjectUtil.stringIsEmpty(groupMember.getGroupId())) continue;
                if(!ObjectUtil.stringIsEmpty(deleteSerialMap.get(groupMember.getGroupId()+"#"+groupMember.getAccount()))) {
                    iterator.remove();
                } else {
                    //[S] add by ysp, review tangsha
                    Group tmpGroup = new Group();
                    tmpGroup.setGroupId(groupMember.getGroupId());
                    if(!deleteGroups.contains(tmpGroup)) {
                        deleteGroups.add(tmpGroup);
                    }
                    //[E] add by ysp, review tangsha
                }
                if(!ObjectUtil.collectionIsEmpty(deleteGroups)) {
                    FireEventUtils.pushFireUpdateGroupList(deleteGroups);  //add by ysp
                }
            }
        }
        /****************************************************************************************
         * 以上if 判断过滤群主收到被自己移除的群成员(群主移除的群成员提示不在这里进行提醒，而在执行移除的时候提醒)  *
         *****************************************************************************************/


        //过滤已经解散的群组 如果群主已经解散了
        Map<String,Group> deleteGroupMap = new GroupExternalService(ActomaController.getApp()).queryDeletedGroupMap();
        for (ListIterator<GroupMember> iterator = deleteMembers.listIterator(); iterator.hasNext(); ) {
            GroupMember groupMember = iterator.next();
            Group group = deleteGroupMap.get(groupMember.getGroupId());
            if (!ObjectUtil.objectIsEmpty(group)) {
                iterator.remove();
            }
        }

        Set<String> groupIds = new HashSet<>();
        for(GroupMember groupMember : deleteMembers){
            groupIds.add(groupMember.getGroupId());
        }

        String currentAccount = ContactUtils.getCurrentAccount();
        Map<String,Group> groupMap = new GroupExternalService(ActomaController.getApp()).queryGroupsByGroupIds(new ArrayList<String>(groupIds));
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            for(GroupMember groupMember : deleteMembers){
                String groupId = groupMember.getGroupId();
                Group group = groupMap.get(groupId);
                if(group.getGroupOwner().equals(currentAccount)){ //区分群主还是群成员
                    listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, groupMember.getGroupId(), groupMember.getAccount(), IContactEvent.QUIT);
                }else{
                    //Note: 群成员这里 包含两种提示 1:xxx 主动退群  2:群主已将xxx移除群组
                    //listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, groupMember.getGroupId(), groupMember.getAccount(), IContactEvent.REMOVED);
                    if(groupMember.getAccount().equals(currentAccount)){
                        listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, groupMember.getGroupId(), groupMember.getAccount(), IContactEvent.REMOVED);
                    }
                }
            }
        }
    }

    /**
     * 群主主动移除群成员时发出提示告诉密信
     * 发出通知群成员被移除
     * @param deletedMembers
     */
    public static void fireDeleteMemberEvent(Collection<GroupMember> deletedMembers){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            for(GroupMember groupMember : deletedMembers){
                listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, groupMember.getGroupId(), groupMember.getAccount(), IContactEvent.REMOVED);
            }
        }
    }








    /**
     * 发出动作群名称或者头像有变更
     * @param updatedGroups
     */
    public static void fireUpdateGroupEvent(Collection<Group> updatedGroups,String serviceType){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            for(Group group : updatedGroups){
                listener.onEvent(IContactEvent.EVENT_GROUP_CHANGED, group.getGroupId(), group.getGroupName(), group.getThumbnail());
            }
        }
    }



    public static void fireGroupAvatarUpdateEvent(String groupId, String avatar) {
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_GROUP_AVATAR_CHANGED, groupId, avatar, null);
        }
    }

    public static void fireGroupNameUpdateEvent(String groupId, String groupName) {
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_GROUP_NAME_CHANGED, groupId, groupName, null);
        }
    }


    /**
     * 发出通知群成员更新 (头像和昵称)
     * @param updatedMembers
     * @param original
     */
    public static void fireUpdateMemberEvent(Collection<GroupMember> updatedMembers,String original){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            if (ObjectUtil.collectionIsEmpty(updatedMembers)) {
                break;
            }
            listener.onEvent(IContactEvent.EVENT_MEMBER_UPDATED, updatedMembers, null, null);
        }
    }

    /**
     * 根据群id获取群信息完成
     */
    public static void fireGroupInfoGet(String groupId, int size) {
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_GROUP_INFO_GET, groupId, size, null);
        }
    }



    /**
     * 不同的退出群方式（移除， 退出，解散）事件通知
     * <p/>
     * **
     */
    public static void fireGroupQuit(String groupId, String account, int quitType) {
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_GROUP_QUIT, groupId, account, quitType);
        }
    }

    /**
     * 通知密信模块  添加群成员/创建群加入成员时 发出提示告诉用户
     * @param outRangeNames
     * @param notAccounts
     */
    public static void fireGroupMemberTips(List<String> outRangeNames,List<String> notAccounts,String groupID){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_GROUP_MEMBER_TIPS, outRangeNames, notAccounts, groupID);
        }
    }
    /**
     * 通知密信模块  添加群成员/创建群加入成员时 存在未关联安全设备的账号 发出提示告诉用户
     * @param noSecNames
     */
    public static void fireNoSecGroupMemberTips(List<String> noSecNames,String groupID){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_NO_SEC_GROUP_MEMBER_TIPS, noSecNames, groupID, null);
        }
    }
    /**
     * 通知密信模块  添加群成员/创建群加入成员时 存在已经是群成员账号 发出提示告诉用户
     * @param inGroupNames
     */
    public static void fireMemberInGroupTips(List<String> inGroupNames,String groupID){
        for (IContactEvent listener :  CONTACT_LISTENERS) {
            listener.onEvent(IContactEvent.EVENT_MEMBER_IN_GROUP_TIPS, inGroupNames, groupID, null);
        }
    }

    /**
     *
     */
    public static void pushFireUpdateGroupList(List<Group> updateGroups){
        if(!ObjectUtil.collectionIsEmpty(updateGroups)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                for (Group group : updateGroups) {
                    listener.onEvent(IContactEvent.EVENT_GROUP_REFRESH, group.getGroupId(), GroupInternalService.getInstance().queryGroupMembers(group.getGroupId()).size(), null);
                }
                listener.onEvent(IContactEvent.EVENT_GROUP_LIST_REFRESH, null, null, null);
            }
        }
    }


    /***
     * modify by wal@xdja.com
     * 好友请求列表点击接受按钮
     */
    public static void pushFriendClickedAcceptButton(String friendAccount,String friendShowName){
        if(!ObjectUtil.stringIsEmpty(friendAccount)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                listener.onEvent(IContactEvent.EVENT_FRIEND_CLICKED_ACCEPT, friendAccount, friendShowName, null);
            }
        }
    }

    /**
     * 好友解除绑定关系
     * @param params
     */
    public static void pushFriendClickedDeleteButton(String...params){
        if(!ObjectUtil.arrayIsEmpty(params)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                listener.onEvent(IContactEvent.EVENT_FRIEND_CLICKED_DELETE, params[0], null, null);
            }
        }
    }
    /**
     * 好友设置备注
     * @param params
     */
    public static void pushFriendUpdateRemarkButton(String...params){
        if(!ObjectUtil.arrayIsEmpty(params)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                listener.onEvent(IContactEvent.EVENT_FRIEND_UPDATE_REMARK, params[0], params[1], null);
            }
        }
    }

    /**
     * 好友被添加方接受好友请求,主动添加方收到推送  给密信发出通知
     * @param params
     */
    public static void pushFriendRequestedAcceptPush(List<String> params){
        if(!ObjectUtil.collectionIsEmpty(params)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                listener.onEvent(IContactEvent.EVENT_FRIEND_REQUESTED_ACCEPT_PUSH, params, null, null);
            }
        }
    }

    /**
     * 好友列表下拉刷新，增量对应的账户信息，账户信息发生变更通知密信和电话更新显示
     * @param params
     */
    public static void pushFriendUpdateNickName(List<String> params){
        if(!ObjectUtil.collectionIsEmpty(params)){
            for (IContactEvent listener :  CONTACT_LISTENERS) {
                listener.onEvent(IContactEvent.EVENT_FRIEND_UPDATE_NICKNAME, params, null, null);
            }
        }
    }



}
