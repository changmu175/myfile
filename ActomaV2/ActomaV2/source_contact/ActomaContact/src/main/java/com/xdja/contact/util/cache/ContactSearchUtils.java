package com.xdja.contact.util.cache;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.service.MemberService;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，联系人搜索接口
 * 创建人：ycm
 * 创建时间：2016/11/1 19:36
 * 修改人：ycm
 * 修改时间：2016/11/1 19:36
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ContactSearchUtils {

    public static void preSearch(){
        GroupInternalService groupInternalService = GroupInternalService.getInstance();
        List<Group> localGroups = groupInternalService.getValidGroups();

        FriendService friendService = new FriendService();
        List<Friend> localFriends = friendService.queryFriends();

        MemberService memberService = new MemberService();
        List<Member> localMembers = memberService.getAllMembersJoinDepartment();

        CacheManager.getInstance().clearData();
        CacheManager.getInstance().putFriendsCache(localFriends);
        CacheManager.getInstance().putGroupsCache(localGroups);
        CacheManager.getInstance().putMembersCache(localMembers);
    }

    public static List<LocalCacheDto> startSearch(String keyword){
        ArrayList<Group> groupList = CacheManager.getInstance().filterGroupsByKeyWord(keyword.trim().toLowerCase());
        ArrayList<Friend> friends = CacheManager.getInstance().filterFriendsByKeyWord(keyword.trim().toLowerCase());
        ArrayList<Member> members = CacheManager.getInstance().filterMembersByKeyWord(keyword.trim().toLowerCase());
        ArrayList<LocalCacheDto> contactList = CacheManager.getInstance().getCacheBeans(friends, members);

        List<LocalCacheDto> groupSource = new ArrayList<>();
        if(!ObjectUtil.collectionIsEmpty(contactList)){
            for(LocalCacheDto localCacheDto : contactList){
                localCacheDto.setViewType(LocalCacheDto.FRIEND_ITEM);
            }
        }
        if(!ObjectUtil.collectionIsEmpty(groupList)){
            for(Group group : groupList){
                LocalCacheDto localCacheDto = new LocalCacheDto(group);
                groupSource.add(localCacheDto);
            }
        }
        List<LocalCacheDto> dataSource = new ArrayList<>();
        if(!ObjectUtil.collectionIsEmpty(contactList)){
            dataSource.addAll(contactList);
        }
        if(!ObjectUtil.collectionIsEmpty(groupList)) {
            dataSource.addAll(groupSource);
        }
        return dataSource;
    }

    public static void endSearch(){
        CacheManager.getInstance().clearData();
    }

}
