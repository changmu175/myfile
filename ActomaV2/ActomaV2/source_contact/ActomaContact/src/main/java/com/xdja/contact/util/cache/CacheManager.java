package com.xdja.contact.util.cache;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanghao on 2015/10/23.
 *
 * <note>当前缓存中的分割符通过";"来切割</note>
 */
public final class CacheManager {

    public final Map<String,Group> cacheGroups = new ConcurrentHashMap<>();
    public final Map<String,Friend> cacheFriends = new ConcurrentHashMap<String,Friend>();
    public final Map<String,Member> cacheMembers = new ConcurrentHashMap<String,Member>();

    private static CacheManager instance;

    private CacheManager(){}

    public static CacheManager getInstance() {
        if(ObjectUtil.objectIsEmpty(instance)){
            instance = new CacheManager();
        }
        return instance;
    }

    /**
     * 根据keyword模糊匹配到 friends, members ，过滤并组装临时的缓存对象
     * @param friends
     * @param members
     * @return
     */
    public ArrayList<LocalCacheDto> getCacheBeans(List<Friend> friends, List<Member> members){
        String currentAccount = ContactUtils.getCurrentAccount();
        ArrayList<LocalCacheDto> dataSource = new ArrayList<>();
        Map<String, LocalCacheDto> friendValues = new HashMap<>();
        Map<String, Member> memberValues = new HashMap<>();
        if(!ObjectUtil.collectionIsEmpty(friends) && !ObjectUtil.collectionIsEmpty(members)){
            for (Friend friend : friends) {
                LocalCacheDto localSearchBean = new LocalCacheDto(friend);
                friendValues.put(friend.getAccount(), localSearchBean);
            }
            //过滤重复的集团联系人数据
            for (Member member : members) {
                memberValues.put(member.getWorkId(), member);
            }
            List<Member> filterMembers = new ArrayList<>();
            for(Map.Entry<String,Member> entry : memberValues.entrySet()){
                filterMembers.add(entry.getValue());
            }
            for(Member member : filterMembers){
                if(ObjectUtil.stringIsEmpty(member.getAccount())){
                    LocalCacheDto localSearchBean = new LocalCacheDto(member);
                    dataSource.add(localSearchBean);
                }else{
                    String memberAccount = member.getAccount();
                    if(currentAccount.equals(memberAccount)){
                        continue;
                    }
                    LocalCacheDto bean = friendValues.get(memberAccount);
                    if(!ObjectUtil.objectIsEmpty(bean)){
                        bean.setMember(member);
                        friendValues.remove(memberAccount);
                        dataSource.add(bean);
                    }else{
                        LocalCacheDto localSearchBean = new LocalCacheDto(member);
                        dataSource.add(localSearchBean);
                    }
                }
            }
            if(!ObjectUtil.collectionIsEmpty(friendValues.values())) {
                dataSource.addAll(0,friendValues.values());
            }
        }else{
            if(ObjectUtil.collectionIsEmpty(friends) && !ObjectUtil.collectionIsEmpty(members)){
                //过滤重复的集团联系人数据
                for (Member member : members) {
                    memberValues.put(member.getWorkId(), member);
                }
                List<Member> filterMembers = new ArrayList<>();
                for(Map.Entry<String,Member> entry : memberValues.entrySet()){
                    filterMembers.add(entry.getValue());
                }
                for(Member member : filterMembers){
                    String memberAccount = member.getAccount();
                    if(currentAccount.equals(memberAccount)){
                        continue;
                    }
                    LocalCacheDto localSearchBean = new LocalCacheDto(member);
                    dataSource.add(localSearchBean);
                }
            }else if(ObjectUtil.collectionIsEmpty(members) && !ObjectUtil.collectionIsEmpty(friends)){

                Map<String, Friend> filterFriendsMap = new HashMap<>();
                for (Friend friend : friends) {
                    filterFriendsMap.put(friend.getAccount(), friend);
                }
                List<Friend> filterFriends = new ArrayList<>();
                for(Map.Entry<String,Friend> entry : filterFriendsMap.entrySet()){
                    filterFriends.add(entry.getValue());
                }
                for (Friend friend : filterFriends) {
                    LocalCacheDto localSearchBean = new LocalCacheDto(friend);
                    dataSource.add(localSearchBean);
                }
            }
        }
        return dataSource;
    }



    /**
     * 缓存匹配模糊搜索字段的群组数据
     * @param localGroups
     */
    public void putGroupsCache(List<Group> localGroups){
        if(!ObjectUtil.mapIsEmpty(cacheGroups))cacheGroups.clear();
        for(Group group : localGroups){
            String groupId = group.getGroupId();
            String groupName = group.getGroupName();
            String groupNamePY = group.getNamePY();
            String groupNamePinYin = group.getNameFullPY();
            if(!ObjectUtil.stringIsEmpty(groupName)){
                cacheGroups.put(groupId+";"+groupName,group);
            }
            if(!ObjectUtil.stringIsEmpty(groupNamePY)){
                cacheGroups.put(groupId+";"+groupNamePY,group);
            }
            if(!ObjectUtil.stringIsEmpty(groupNamePinYin)) {
                cacheGroups.put(groupId + ";" + groupNamePinYin,group);
            }
        }
    }

    /**
     * 缓存匹配好友数据
     * @param localFriends
     */
    public void putFriendsCache(List<Friend> localFriends){
        if(!ObjectUtil.mapIsEmpty(cacheFriends))cacheFriends.clear();
        String currentAccount = ContactUtils.getCurrentAccount();
        if(ObjectUtil.stringIsEmpty(currentAccount))return;
        for(Friend friend : localFriends){
            String account = friend.getAccount();
            if(account.equals(currentAccount)){
                continue;
            }
            String remark = friend.getRemark();
            String remarkPy = friend.getRemarkPy();
            String remarkPinYin = friend.getRemarkPinyin();

            ActomaAccount actomaAccount = friend.getActomaAccount();
            String nickName = actomaAccount.getNickname();
            String nickNamePy = actomaAccount.getNicknamePy();
            String nickNamePinYin = actomaAccount.getNicknamePinyin();
            // 这里如果alias 不为空则赋值给account 用户在搜索只能通过alias 来搜索
            String alias = actomaAccount.getAlias();
            if(!ObjectUtil.stringIsEmpty(alias)){
                account = alias;
                friend.setAlias(alias);
            }
            if(!ObjectUtil.stringIsEmpty(account)) {
                cacheFriends.put(account+";",friend);
            }
            if(!ObjectUtil.stringIsEmpty(remark)) {
                cacheFriends.put(account + ";" +remark,friend);
            }
            if(!ObjectUtil.stringIsEmpty(remarkPy)) {
                cacheFriends.put(account + ";" + remarkPy, friend);
            }
            if(!ObjectUtil.stringIsEmpty(remarkPinYin)) {
                cacheFriends.put(account + ";" + remarkPinYin, friend);
            }
            if(!ObjectUtil.stringIsEmpty(nickName)) {
                cacheFriends.put(account + ";" + nickName, friend);
            }
            if(!ObjectUtil.stringIsEmpty(nickNamePy)) {
                cacheFriends.put(account + ";" + nickNamePy, friend);
            }
            if(!ObjectUtil.stringIsEmpty(nickNamePinYin)) {
                cacheFriends.put(account + ";" + nickNamePinYin, friend);
            }
        }
    }

    /**
     * 缓存集团成员匹配的数据
     * @param localMembers
     */
    public void putMembersCache(List<Member> localMembers){
        String currentAccount = ContactUtils.getCurrentAccount();
        if(ObjectUtil.stringIsEmpty(currentAccount))return;
        for(Member member : localMembers){
            String account = member.getAccount();
            if(currentAccount.equals(account)){
                continue;
            }
            ActomaAccount actomaAccount = member.getActomaAccount();
            // 这里如果alias 不为空则赋值给account 用户在搜索只能通过alias 来搜索
            String alias = actomaAccount.getAlias();
            if(!ObjectUtil.stringIsEmpty(alias)){
                account = alias;
                member.setAlias(alias);
            }
            String name = member.getName();
            String namePy = member.getNamePy();
            String namePinYin = member.getNameFullPy();
            String phone = member.getMobile();
            if(!ObjectUtil.stringIsEmpty(account)) {
                cacheMembers.put(account + ";", member);
            }
            if(!ObjectUtil.stringIsEmpty(name)){
                if(!ObjectUtil.stringIsEmpty(account)){
                    cacheMembers.put(account+";"+name,member);
                }else{
                    cacheMembers.put(name,member);
                }
            }
            if(!ObjectUtil.stringIsEmpty(namePy)){
                if(!ObjectUtil.stringIsEmpty(account)){
                    cacheMembers.put(account+";"+namePy,member);
                }else{
                    cacheMembers.put(namePy,member);
                }
            }
            if(!ObjectUtil.stringIsEmpty(namePinYin)){
                if(!ObjectUtil.stringIsEmpty(account)){
                    cacheMembers.put(account+";"+namePinYin,member);
                }else{
                    cacheMembers.put(namePinYin,member);
                }
            }
            if(!ObjectUtil.stringIsEmpty(phone)){
                String[] phones = phone.split("#");
                for(String phon : phones){
                    if(!ObjectUtil.stringIsEmpty(account)){
                        cacheMembers.put(account+";"+phon,member);
                    }else{
                        cacheMembers.put(phon,member);
                    }
                }
            }
            if(!ObjectUtil.objectIsEmpty(actomaAccount)){
                String nickName = actomaAccount.getNickname();
                String nicknamePy = actomaAccount.getNicknamePy();
                String nickNamePinYin = actomaAccount.getNicknamePinyin();
                if(!ObjectUtil.stringIsEmpty(nickName)){
                    if(!ObjectUtil.stringIsEmpty(account)){
                        cacheMembers.put(account+";"+nickName,member);
                    }else{
                        cacheMembers.put(nickName,member);
                    }
                }
                if(!ObjectUtil.stringIsEmpty(nicknamePy)){
                    if(!ObjectUtil.stringIsEmpty(account)){
                        cacheMembers.put(account+";" + nicknamePy, member);
                    }else{
                        cacheMembers.put(nicknamePy,member);
                    }
                }
                if(!ObjectUtil.stringIsEmpty(nickNamePinYin)){
                    if(!ObjectUtil.stringIsEmpty(account)){
                        cacheMembers.put(account+";" + nickNamePinYin, member);
                    }else{
                        cacheMembers.put(nickNamePinYin,member);
                    }
                }
            }
        }
    }

    /**
     * 根据关键字从缓存中查询匹配到的数据
     * @param keyword
     * @return
     */
    public ArrayList<Group> filterGroupsByKeyWord(String keyword){
        ArrayList<Group> groups = new ArrayList<>();
        Map<String,Group> groupMap = new HashMap<>();
        Set<String> keys = cacheGroups.keySet();
        for(String key : keys){
            String[] keyArray = key.split(";");
            if(keyArray.length<=1){
                continue;
            }else{
                if(keyArray[1].toLowerCase().contains(keyword)){
                    Group group = cacheGroups.get(key);
                    groupMap.put(group.getGroupId(),group);
                }
            }
        }
        for(Map.Entry<String,Group> entry : groupMap.entrySet()){
            groups.add(entry.getValue());
        }
        return groups;
    }

    /**
     * 根据关键字从缓存中查询匹配到的数据
     * @param keyword
     * @return
     */
    public ArrayList<Friend> filterFriendsByKeyWord(String keyword){
        ArrayList<Friend> friends = new ArrayList<Friend>();
        Set<String> friendKeys = cacheFriends.keySet();
        for(String key : friendKeys){
            String[] keyArray = key.split(";");
            if(keyArray[0].toLowerCase().contains(keyword)){//fix bug 2300 by wal@xdja.com
                friends.add(cacheFriends.get(key));
            }else{
                if(keyArray.length>1){
                    if(keyArray[1].toLowerCase().contains(keyword)){
                        friends.add(cacheFriends.get(key));
                    }
                }
            }
        }
        return friends;
    }

    /**
     * 根据关键字从缓存中查询匹配到的数据
     * @param keyword
     * @return
     */
    public ArrayList<Member> filterMembersByKeyWord(String keyword){
        ArrayList<Member> members = new ArrayList<Member>();
        Set<String> memberKeys = cacheMembers.keySet();
        for(String key : memberKeys){
            String[] keyArray = key.split(";");
            if(keyArray[0].contains(keyword)){
                members.add(cacheMembers.get(key));
            }else{
                if(keyArray.length>1){
                    if(keyArray[1].toLowerCase().contains(keyword)){
                        members.add(cacheMembers.get(key));
                    }
                }
            }
        }
        return members;
    }

    /**
     * 每次进入搜索界面,清除之前的旧数据
     */
    public void clearData(){
        cacheFriends.clear();
        cacheGroups.clear();
        cacheMembers.clear();
    }

}
