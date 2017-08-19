package com.xdja.contact.convert;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.http.response.friend.ResponseFriend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2016/4/27.
 * 服务器返回好友相关数据转换为客户端实体对象
 */
public class FriendConvert {

    /**
     * 抽取新增的好友账号
     * @param responseFriends
     * @return
     */
    public static List<String> extractNewlyIncreasedAccount(List<Friend> responseFriends){
        List<String> accountList = new ArrayList<>();
        for (Friend responseFriend : responseFriends) {
            String friendState = responseFriend.getState();
            if(Friend.ADD.equals(friendState)){
                accountList.add(responseFriend.getAccount());
            }
        }
        return accountList;
    }

    /**
     * 提取新增的好友数据
     * @param responseFriends
     * @return
     */
    public static List<Friend> extractFriend(List<ResponseFriend> responseFriends){
        List<Friend> friends = new ArrayList<Friend>();
        for (ResponseFriend responseFriend : responseFriends) {
            Friend friend = responseFriend.convert2Friend();
            friends.add(friend);
        }
        return friends;
    }

    /**
     * 根据服务器返回的好友数据提取要删除的好友数据
     * @param responseFriends
     * @return
     */
    public static List<String> extractDeleteFriends(List<ResponseFriend> responseFriends){
        List<String> deleteFriendList = new ArrayList<>();
        for (ResponseFriend responseFriend : responseFriends) {
            if (Friend.DELETE.equals(responseFriend.getState())) {
                Friend deleteFriend = responseFriend.convert2Friend();
                deleteFriendList.add(deleteFriend.getAccount());
            }
        }
        return deleteFriendList;
    }


    /**
     * 抽取增量的好友键值
     * @param responseFriends 服务端返回的数据对象
     * @return
     */
    public static Map<String,Friend> extractFriendMap(List<ResponseFriend> responseFriends){
        Map<String,Friend> friendMap = new HashMap<String,Friend>();
        for (ResponseFriend responseFriend : responseFriends) {
            Friend friend = responseFriend.convert2Friend();
            friendMap.put(responseFriend.getAccount(),friend);
        }
        return friendMap;
    }



}
