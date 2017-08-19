package com.xdja.contact.service;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.bean.dto.ContactNameDto;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.dao.GroupMemberDao;
import com.xdja.contact.exception.ATJsonParseException;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.http.response.group.GetGroupInfoResponse;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2016/2/3.
 *
 * 群组功能外部调用接口统一调整到当前对象
 *
 */
public class GroupExternalService {

    private GroupMemberDao memberDao;

    public GroupExternalService() {
    }

    public GroupExternalService(Context context) {

        this.memberDao = new GroupMemberDao();

    }


    /**
     * <pre>
     * @param groupId 参数id
     * @return 返回群内成员;数据不会为空,但是大小可能出现0的情况
     * </pre><br>
     * <b>
     *     <note>这里UserInfo数据组合Friend;ActomaAccount</note>
     *
     *     数据里面不包含已经删除的群成员数据
     * </b>
     */
    public List<UserInfo> getUserInfosByGroupId(String groupId){
        List<UserInfo> dataSource = new ArrayList<>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            dataSource = memberDao.queryUserInfosByGroupId(groupId);
            memberDao.closeDataBase();
        }
        return dataSource;
    }


    /**
     * 从本地群组中过滤序列为0 的群组信息
     * @return
     */
    public Map<String,Group> getGroupSerialIsZeroMap(){
        GroupInternalService service = GroupInternalService.getInstance();
        List<Group> dataSource = service.getValidGroups();
        String currentAccount = ContactUtils.getCurrentAccount();
        Map<String,Group> groupMap = new HashMap<>();
        for(Group group : dataSource){
            String updateSerial = group.getUpdateSerial();
            if(ObjectUtil.stringIsEmpty(updateSerial) || "0".equals(updateSerial) || group.getGroupOwner().equals(currentAccount)){
                groupMap.put(group.getGroupId(),group);
            }
        }
        return groupMap;
    }



    /**
     * 查询已经删除的群组信息
     * @return
     */
    public Map<String,Group> queryDeletedGroupMap(){
        Map<String,Group> deletedGroupMap = new HashMap<>();
        Map<String,Group> groupMap = GroupInternalService.getInstance().queryGroupMap();
        for(Group group : groupMap.values()){
            if(GroupConvert.DELETED.equals(group.getIsDeleted())){
                deletedGroupMap.put(group.getGroupId(),group);
            }
        }
        return deletedGroupMap;
    }

    /**
     * 根据传递的群id 查询对应的群信息
     * @param groupIds
     * @return
     */
    public Map<String,Group> queryGroupsByGroupIds(List<String> groupIds){
        Map<String,Group> localGroups = GroupInternalService.getInstance().queryGroupMap();
        Map<String,Group> resultGroupMap = new HashMap<>();
        for(String groupId : groupIds){
            Group group = localGroups.get(groupId);
            if(!ObjectUtil.objectIsEmpty(group)) {
                resultGroupMap.put(groupId,group);
            }
        }
        return resultGroupMap;
    }

    /**
     * 根据账号查询对应的显示名
     * @param accounts 账号集合;<b><font color = 'red'>调用方校验输入参数</font></b>
     * @return 返回数据不会为null
     */
    public List<String> queryDisplayNameByAccounts(String groupId,List<String> accounts){
        List<String> dataSource = new ArrayList<>();
        List<ContactNameDto> nameDtoList = GroupInternalService.getInstance().queryContactNameDto(groupId,accounts);
        for(ContactNameDto contactNameDto : nameDtoList){
            dataSource.add(contactNameDto.getDisplayName());
        }
        return dataSource;
    }

    /**
     * 根据账号查询对应的临时对象
     * @param accounts
     * @return
     */
    public List<ContactNameDto> queryContactDto(String groupId,String... accounts){
        List<String> params = new ArrayList<>();
        if(accounts.length <= 1 ){
            params.add(accounts[0]);
        }else {
            params = Arrays.asList(accounts);
        }
        List<ContactNameDto> nameDtoList = GroupInternalService.getInstance().queryContactNameDto(groupId,params);
        return nameDtoList;
    }


    /**
     * 根据群id查询群成员数据
     * @param groupId
     * @return
     */
    public List<GroupMember> queryGroupMembers(String groupId){
        GroupInternalService service = GroupInternalService.getInstance();
        List<GroupMember> members = service.queryGroupMembers(groupId);
        return members;
    }

    /**
     * 根据群id查询对应的群信息
     * @param groupId
     * @return
     */
    public Group queryByGroupId(String groupId){
        GroupInternalService service = GroupInternalService.getInstance();
        return service.queryByGroupId(groupId);
    }

    /**
     * 查询群成员内已经删除的群成员
     * key : groupId#account; value : groupId#account;
     * @return
     */
    public Map<String,String> queryDeleteGroupMembers(){
        GroupInternalService internalService = GroupInternalService.getInstance();
        return internalService.getDeletedGroupMemberMap();

    }



    /**
     * 根据群组ID从服务器获取群组信息
     * @param context 上下文对象
     * @param groupId 需要获取信息的群组ID
     */

    public void getGroupInfoWithServerById(final Context context, final String groupId) {
        GroupHttpServiceHelper.getGroupInfoById(groupId, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
                LogUtil.getUtils().e("Actoma contact GroupExternalService getGroupInfoWithServerById:调用getGroupInfoWithServerById获取群信息动作出错------>" + httpErrorBean.getErrCode());
            }

            @Override
            public void onSuccess(String body) {
                try {
                    GetGroupInfoResponse response = JSON.parseObject(body, GetGroupInfoResponse.class);
                    ArrayList<GroupMember> members = new ArrayList<GroupMember>();
                    if (!ObjectUtil.objectIsEmpty(response)) {
                        GetGroupInfoResponse.G g = response.getGroup();
                        Group group = new Group();
                        parseGroupInfo(group, g);
                        if (!ObjectUtil.collectionIsEmpty(response.getMembers())) {
                            String curAccount = ContactUtils.getCurrentAccount();
                            for (GetGroupInfoResponse.M m : response.getMembers()) {
                                if (curAccount != null && curAccount.equals(m.getAccount()) && GroupMember.DELETE.equals(m.getStatus())) {
                                    group.setIsDeleted(GroupConvert.DELETED);
                                }
                                parseGroupMemberInfo(members, group, m);
                            }
                        }
                        GroupInternalService internalService = GroupInternalService.getInstance();
                        boolean bool = internalService.batchSaveOrUpdateGroupMembers(members);
                        if (bool) {
                            boolean result = internalService.saveOrUpdate(group);
                            if (result) {
                                FireEventUtils.fireGroupInfoGet(groupId, members.size());
                                LogUtil.getUtils().i("调用getGroupInfoWithServerById------success");
                            } else {
                                LogUtil.getUtils().e("Actoma contact GroupExternalService getGroupInfoWithServerById:调用getGroupInfoWithServerById------保存群信息出错");
                            }
                        } else {
                            LogUtil.getUtils().e("Actoma contact GroupExternalService getGroupInfoWithServerById:调用getGroupInfoWithServerById------保存群成员信息出错");
                        }
                    }
                } catch (Exception e) {
                    new ATJsonParseException("Actoma contact GroupExternalService getGroupInfoWithServerById:调用getGroupInfoWithServerById-----json 解析出错");
                }
            }

            @Override
            public void onErr() {

            }
        });
    }

    private void parseGroupInfo(Group group,GetGroupInfoResponse.G g){
        group.setGroupId(g.getGroupId());
        group.setGroupName(g.getGroupName());
        group.setNamePY(g.getGroupNamePy());
        group.setNameFullPY(g.getGroupNamePinyin());
        group.setGroupOwner(g.getOwner());
        group.setAvatar(g.getAvatarUrl());
        group.setAvatarHash(g.getAvatarHash());
        group.setThumbnail(g.getThumbnailUrl());
        group.setThumbnailHash(g.getThumbnailHash());
        group.setCreateTime(g.getCreateTime());
        group.setIsDeleted(g.getStatus().equals(Group.DELETE) ? GroupConvert.DELETED : GroupConvert.UN_DELETED);
    }

    private void parseGroupMemberInfo(List<GroupMember> members,Group group,GetGroupInfoResponse.M m){
        GroupMember member = new GroupMember();
        member.setGroupId(group.getGroupId());
        member.setAccount(m.getAccount());
        member.setNickName(m.getNickname());
        member.setNickNamePY(m.getNicknamePy());
        member.setNickNameFullPY(m.getNicknamePinyin());
        member.setInviteAccount(m.getInviteAccount());
        member.setCreateTime(m.getCreateTime());
        member.setIsDeleted(m.getStatus().equals(GroupMember.DELETE) ? GroupConvert.DELETED : GroupConvert.UN_DELETED);
        members.add(member);
    }



}
