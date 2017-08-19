package com.xdja.contact.util.cache;

import android.os.AsyncTask;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.service.MemberService;

import java.util.List;

/**
 * Created by wanghao on 2015/10/23.
 *
 * 本地搜索的时候查询出来所有数据缓存起来
 */
public class CacheDataTask extends AsyncTask<String, Integer, Void> {

    @Override
    protected Void doInBackground(String... params) {
        GroupInternalService groupInternalService = GroupInternalService.getInstance();
        List<Group> localGroups = groupInternalService.getValidGroups();

        FriendService friendService = new FriendService();
        List<Friend> localFriends = friendService.queryFriends();

        MemberService memberService = new MemberService();
        List<Member> localMembers = memberService.getAllMembersJoinDepartment();

        CacheManager.getInstance().putFriendsCache(localFriends);
        CacheManager.getInstance().putGroupsCache(localGroups);
        CacheManager.getInstance().putMembersCache(localMembers);
        return null;
    }

}
