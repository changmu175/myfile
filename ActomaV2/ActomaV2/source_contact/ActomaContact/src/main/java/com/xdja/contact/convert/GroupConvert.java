package com.xdja.contact.convert;

import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.http.response.group.ResponseGroup;
import com.xdja.contact.http.response.group.ResponseGroupMember;

/**
 * Created by wanghao on 2016/2/24.
 *
 */
public class GroupConvert {

    public final static String DELETED = "1";

    public final static String UN_DELETED = "0";

    /**
     * 把服务器返回的群组数据转换为群组数据
     * @param responseGroup
     * @return
     */
    public static Group responseGroup2Group(ResponseGroup responseGroup) {
        Group group = new Group();
        group.setGroupId(responseGroup.getGroupId());
        group.setGroupName(responseGroup.getGroupName());
        group.setNameFullPY(responseGroup.getGroupNamePinyin());
        group.setNamePY(responseGroup.getGroupNamePy());
        group.setGroupOwner(responseGroup.getOwner());

        group.setAvatar(responseGroup.getAvatarId());
        group.setThumbnail(responseGroup.getThumbnailId());
        group.setAvatarHash(responseGroup.getAvatarHash());
        group.setThumbnailHash(responseGroup.getThumbnailHash());
        group.setCreateTime(responseGroup.getCreateTime());
        group.setStatus(String.valueOf(responseGroup.getStatus()));
        group.setUpdateSerial(responseGroup.getUpdateSerial());
        group.setIsDeleted(responseGroup.getStatus().equals(Group.DELETE) ? DELETED : UN_DELETED);
        return group;
    }

    /**
     * 把服务端返回的成员数据转换为本地成员数据
     * @param syncMember
     * @param groupId
     * @return
     */
    public static GroupMember convertSyncDataToMember(ResponseGroupMember syncMember, String groupId) {
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setAccount(syncMember.getAccount());
        member.setNickName(syncMember.getNickname());
        member.setNickNamePY(syncMember.getNicknamePy());
        member.setNickNameFullPY(syncMember.getNicknamePinyin());
        member.setCreateTime(syncMember.getCreateTime());
        member.setUpdateSerial(syncMember.getUpdateSerial());
        member.setInviteAccount(syncMember.getInviteAccount());
        member.setStatus(syncMember.getStatus());
        member.setIsDeleted(syncMember.getStatus().equals(Group.DELETE) ? DELETED : UN_DELETED);
        return member;
    }


}
