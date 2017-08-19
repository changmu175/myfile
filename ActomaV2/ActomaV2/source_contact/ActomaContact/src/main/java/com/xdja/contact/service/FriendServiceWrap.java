package com.xdja.contact.service;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.convert.FriendConvert;
import com.xdja.contact.exception.FriendServiceWrapException;

import java.util.List;

/**
 * Created by wanghao on 2016/4/27.
 * 针对好友业务进行封装，
 * FriendService.class 只是针对数据访问以及部分业务的封装，当前对象对好友业务模块内交叉业务进行组合
 */
public class FriendServiceWrap {

    /**
     * 更新本地好友列表记录
     * @param friendList 服务端返回数据解析出来的数据对象
     * @throws FriendServiceWrapException 执行业务出错时抛出该异常
     */
    public static void updateLocalFriends(List<Friend> friendList) throws FriendServiceWrapException {
        try {
            new FriendService().batchSaveOrUpdate(friendList);
        }catch (Exception e){
            throw new FriendServiceWrapException();
        }
    }

    /**
     * 更新本地好友请求历史记录
     * @param friendList
     * @throws FriendServiceWrapException
     */
    public static void updateLocalFriendHistory(List<Friend> friendList)throws FriendServiceWrapException{
        try {
            List<String> accountList = FriendConvert.extractNewlyIncreasedAccount(friendList);
            new FriendRequestService().updateHistories(accountList, FriendHistoryState.ALREADY_FRIEND);
        }catch (Exception e){
            throw new FriendServiceWrapException();
        }
    }

}
