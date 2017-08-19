package com.xdja.contact.service;


import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.bean.dto.ContactNameDto;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.dao.GroupDao;
import com.xdja.contact.dao.GroupMemberDao;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XDJA_XA on 2015/7/25.
 * wanghao:
 * 对于不正确或者繁杂的逻辑进行重构
 * 对于不能理解的函数、变量进行重构
 * 如果有必要的话请抽取对象进行重构
 * 如果还有时间的话请设计扩展性好的框架或者模式进行重构
 *
 * 群组功能内部查询调用当前对象
 *
 */
public final class GroupInternalService {

    private static GroupInternalService instance;

    private GroupDao dao;

    private GroupMemberDao memberDao;

    private GroupInternalService() {
        this.dao = new GroupDao();
        this.memberDao = new GroupMemberDao();
    }

    public static synchronized GroupInternalService getInstance() {
        if (instance == null) {
            instance = new GroupInternalService();
        }
        return instance;
    }

    //start:modeify by wal@xdja.com
    public static  void setInstanceToEmpty() {
        //在群组进行数据更新是，需要在数据库中读取最大更新序列，而在切换账号是instance没有释放，导致数据库读取异常
        LogUtil.getUtils().e("wallog =====setInstanceToEmpty===");
        if (instance != null) {
            instance = null;
        }
    }
    //end:modeify by wal@xdja.com

    /**
     *  考虑到这里的查询每次都是查询本地所有群组然后过滤这里不需要考虑效率问题
     *  本地数据为空  所有数据都执行保存<br/>
     *  本地数据不空  执行更新数据动作
     * @param paramGroups
     * @return
     */
    public synchronized boolean batchSaveOrUpdateGroups(List<Group> paramGroups) {
        Map<String, Group> localGroupMap = new HashMap<>();
        boolean result = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            try {
                localGroupMap = dao.queryMapGroups();
                dao.beginTransaction();
                if(ObjectUtil.mapIsEmpty(localGroupMap)){
                    for(Group group : paramGroups){
                        result = dao.insert(group) > -1;
                        if (!result) break;
                    }
                }else{
                    for(Group group : paramGroups){
                        String groupId = group.getGroupId();
                        Group localValue = localGroupMap.get(groupId);
                        if(ObjectUtil.objectIsEmpty(localValue)){
                            result = dao.insert(group) > -1;
                            if (!result) break;
                        }else{
                            result = dao.update(group) > 0;
                            if (!result) break;
                        }
                    }
                }
                if (result) dao.setTransactionSuccess();
            } finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }

    /**
     * 批量保存或者更新群成员数据
     * @param  groupMembers 调用方校验非空
     * @return
     */
    public boolean batchSaveOrUpdateGroupMembers(List<GroupMember> groupMembers){
        boolean result = false;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            memberDao.beginTransaction();
            try{
                Map<String,String> keyMap = memberDao.queryGroupMembersKeyMap();
                if(ObjectUtil.mapIsEmpty(keyMap)){
                    //首次保存
                    for (GroupMember member : groupMembers) {
                        result = memberDao.insert(member) >= 0 ;
                        if(!result){
                            break;
                        }
                    }
                }else{
                    String split = "#";
                    for(GroupMember groupMember : groupMembers){
                        StringBuffer keyValue = new StringBuffer();
                        keyValue.append(groupMember.getGroupId());
                        keyValue.append(split);
                        keyValue.append(groupMember.getAccount());
                        String value = keyMap.get(keyValue.toString());
                        if(ObjectUtil.stringIsEmpty(value)){//增
                            result = memberDao.insert(groupMember) >= 0;
                        }else{//删 、改
                            result = memberDao.update(groupMember) > 0;
                        }
                        if(!result){
                            break;
                        }
                    }
                }
                if (result) {
                    memberDao.setTransactionSuccess();
                }
            }catch (Exception e){
                LogUtil.getUtils().e("Actoma contact GroupInternalService batchSaveOrUpdateGroupMembers:增量群成员数据保存或者更新出现异常");
            }finally {
                memberDao.endTransaction();
                memberDao.closeDataBase();
            }
        }
        return result;
    }


    /**
     * 根据ids 查询对应的群组信息
     * @param groupIds
     * @return
     */
    public List<GroupMember> queryGroupsByIds(Set<String> groupIds){
        return queryGroupsByIds(new ArrayList<String>(groupIds));
    }

    /**
     * 根据ids 查询对应的群组信息
     * @param groupIds
     * @return
     */
    public List<GroupMember> queryGroupsByIds(List<String> groupIds){
        List<GroupMember> groupMembers = new ArrayList<>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMembers = memberDao.queryMembersInGroupIds(groupIds);
            memberDao.closeDataBase();
        }
        return groupMembers;
    }

    /**
     * 批量更新群组数据
     * @param params
     * @return
     */
    public synchronized boolean updateGroups(List<Group> params){
        boolean result = false;
        synchronized (dao.helper) {
            try {
                dao.getWriteDataBase();
                dao.beginTransaction();
                for(Group group : params){
                    result = dao.update(group) > 0;
                    if(!result)break;
                }
                if(result)dao.setTransactionSuccess();
            }finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }

    /**
     * 根据传入的批量账户数据查询对应的 账户昵称、集团名称、好友备注、
     * @param accounts
     * @return
     */
    public List<ContactNameDto> queryContactNameDto(String groupId,List<String> accounts){
        List<ContactNameDto> datasource = new ArrayList<>();
        synchronized (dao.helper){
            dao.getReadableDataBase();
            datasource = dao.queryDisplayNameByIds(groupId,accounts);
            dao.closeDataBase();
        }

        return datasource;
    }



    /**<p>
     * 获取所有<b><font color = 'red'>有效</font></b>的群组信息
     * @return List<Group> groups 有可能为空</p>
     */
    public List<Group> getValidGroups() {
        List<Group> groups;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            groups = dao.queryAll();
            dao.closeDataBase();
        }
        return groups;
    }

    /**
     * 通过本地ID查询群组
     * @param groupId 群组ID
     * @return 不成功为null
     */
    @Deprecated
    public Group queryGroupByLocalId(String groupId) {
        return queryByGroupId(groupId);
    }


    /**<p>
     * @param groupId 输入要查询的群组id
     * @return 返回对应的群组信息,返回结果有空能为null
     * </p>
     */
    public Group queryByGroupId(String groupId){
        Group group;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            group = dao.queryGroupById(groupId);
            dao.closeDataBase();
        }
        return group;
    }

    /**
     * 保存群组对象
     * @param group
     * @return true : 保存成功; false : 保存失败;
     */
    public boolean insert(Group group) {
        boolean isSuccess = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            if (dao.insert(group) >= 0) {
                isSuccess = true;
            }
            dao.closeDataBase();
        }
        return isSuccess;
    }

    /**
     * 更新群组信息
     * @return
     */
    public boolean updateGroup(Group group){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            result = dao.update(group) > 0;
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * 更新群成员信息
     * @param groupMember
     * @return true ： 成功 ; false : 失败
     */
    public boolean updateGroupMember(GroupMember groupMember){
        boolean result = false;
        synchronized (memberDao.helper){
            memberDao.getWriteDataBase();
            result = memberDao.update(groupMember) > 0;
            memberDao.closeDataBase();
        }
        return result;
    }

    /**
     * 查询当前用户所有的群组
     * @return
     */
    public Map<String,Group> queryGroupMap(){
        Map<String, Group> localGroupMap = new HashMap<>();
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            localGroupMap = dao.queryMapGroups();
            dao.closeDataBase();
        }
        return localGroupMap;
    }


    /**
     * 同步需要操作的群组信息
     * @param group 群组
     * @return true : 成功；false : 失败;
     */
    public boolean saveOrUpdate(Group group) {
        boolean bool = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            Group localGroup = dao.queryGroupById(group.getGroupId());
            if (!ObjectUtil.objectIsEmpty(localGroup)) {
                bool = dao.update(group) > 0;
            } else {
                bool = dao.insert(group) >= 0;
            }
            dao.closeDataBase();
        }
        return bool;
    }

    /**
     * 群组移除成员
     * @param groupMember
     * @return true : 移除成员成功 ; false : 失败
     */
    public boolean deleteGroupMember(GroupMember groupMember){
        boolean result = false;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            result = memberDao.delete(groupMember) > 0 ;
            memberDao.closeDataBase();
        }
        return result;
    }





    /**
     * 查询群组的全部成员
     * @param groupId    群组ID
     * @return 返回群内成员,数据可能为空,但是不会出现null
     */
    public List<GroupMember> queryGroupMembers(String groupId) {
        List<GroupMember> groupMembers = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMembers = memberDao.queryMembersInGroupIds(groupIds);
            memberDao.closeDataBase();
        }
        return groupMembers;
    }


    /**
     * 通过群组ID和账号查询成员
     *
     * @param groupId 群组ID
     * @param account 帐号
     * @return 没找到返回null
     */
    public GroupMember queryMember(String groupId, String account) {
        GroupMember groupMember;
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMember = memberDao.query(groupId, account);
            memberDao.closeDataBase();
        }
        if(!ObjectUtil.objectIsEmpty(groupMember)) {
            ActomAccountService accountService = new ActomAccountService();
            AvaterService avatarService = new AvaterService();
            groupMember.setActomaAccount(accountService.queryByAccount(account));
            groupMember.setAvatar(avatarService.queryByAccount(account));
        }
        return groupMember;
    }


    /**
     * 判断该群组成员是否存在
     *
     * @param groupId 群组ID
     * @param account 成员账号
     * @return true or false
     */
    public boolean isMemberExisted(String groupId, String account) {
        GroupMember result;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            result = memberDao.query(groupId, account);
            memberDao.closeDataBase();
        }
        return result != null && GroupConvert.UN_DELETED.equals(result.getIsDeleted());
    }


    /**
     * 根据群id查询对应的群成员对应的最大序列
     * @param groupIds
     * @return
     */
    public Map<String,Long> getGroupMemberMaxSerialByIds(List<String> groupIds) {
        Map<String, Long> groupMemberSerialMap = new HashMap<String,Long>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMemberSerialMap = memberDao.initUpdateSerialMap(groupIds);
            memberDao.closeDataBase();
        }
        return groupMemberSerialMap;
    }

    /**
     * 查询群成员表内更新序列为0的成员
     * @return
     */
    public Map<String,String> getSerialIsZeroMap(){
        Map<String, String> groupMemberSerialMap = new HashMap<String,String>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMemberSerialMap = memberDao.getSerialIsZeroMap();
            memberDao.closeDataBase();
        }
        return groupMemberSerialMap;
    }

    /**
     * 查询已经执行删除的群成员
     * key : groupId#account; value : groupId#account;
     * @return
     */
    public Map<String,String> getDeletedGroupMemberMap(){
        Map<String, String> groupMemberSerialMap = new HashMap<String,String>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            groupMemberSerialMap = memberDao.getDeletedGroupMemberMap();
            memberDao.closeDataBase();
        }
        return groupMemberSerialMap;
    }

    public List<GroupMember> getMembers(Group group) {
        List<GroupMember> members = new ArrayList<>();

        if (!ObjectUtil.objectIsEmpty(group)) {
            members = queryGroupMembers(group.getGroupId());
        }
        return members;
    }

    /**
     * 获取群组最大的更新序列
     * @return
     */
    public String queryMaxUpdateSerial() {
        String updateSerial;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            updateSerial = dao.initMaxUpdateSerial();
            dao.closeDataBase();
        }
        return updateSerial;
    }

    //add by ysp
    /**
     * 从数据库中更新删除群成员对应的字段值
     * @param groupMembers
     * @return
     */
    public boolean multiDeleteGroupMember(List<GroupMember> groupMembers) {
        boolean result;

        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            result = memberDao.deleteMultiMember(groupMembers) > 0;
            memberDao.closeDataBase();
        }

        return result;
    }

    //add by ysp@xdja.com
    /**
     *从数据库查找群成员信息
     * @param groupId  群组ID
     * @param key  模糊查询的key
     * @return
     */
    public List<UserInfo> searchGroupMemberByKey(String groupId, String key) {
        List<UserInfo> dataSource;
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            dataSource = memberDao.getUserInfoByKey(groupId, key);
            memberDao.closeDataBase();
        }
        return dataSource;
    }

    //add by ysp
    /**
     * 根据群组名字关键字查找群组
     * @param key
     * @return
     */
    public List<Group> queryGroupByKey(String key) {
        List<Group> dataSource;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            dataSource = dao.queryGroupByKey(key);
            dao.closeDataBase();
        }
        return dataSource;
    }

    public void processGroupRemovePushMsg(String groupId){
        Group group = queryByGroupId(groupId);
        if(group != null) {
            group.setIsDeleted(GroupConvert.DELETED);
            updateGroup(group);
        }else{
            LogUtil.getUtils().e("GroupInternalService processGroupRemovePushMsg group is null, "+groupId);
        }
        String currentAccount = ContactUtils.getCurrentAccount();
        GroupMember deleteMember = queryMember(groupId, currentAccount);
        if(deleteMember != null) {
            ArrayList<GroupMember> members = new ArrayList<>();
            members.add(deleteMember);
            FireEventUtils.fireDeleteMemberEvent(members);
        }else{
            LogUtil.getUtils().e("GroupInternalService processGroupRemovePushMsg deleteMember is null, "+groupId+" currentAccount "+currentAccount);
        }
    }

    /**
     * tangsha add for 8110
     * 查询没有群成员信息的群组
     */
    public List<Group> queryGroupNoMemberInfo() {
        List<Group> dataSource;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            dataSource = dao.queryGroupNoMemberInfo();
            dao.closeDataBase();
        }
        return dataSource;
    }

    public void clearGroupNoMemberInfo() {
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            memberDao.deleteAllGroupMembers();
            memberDao.closeDataBase();
        }
    }
 }
