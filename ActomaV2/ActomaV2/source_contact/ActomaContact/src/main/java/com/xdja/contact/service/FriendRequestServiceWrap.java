package com.xdja.contact.service;

import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.exception.FriendServiceWrapException;
import com.xdja.contact.http.response.account.ResponseActomaAccount;

import java.util.List;

/**
 * Created by wanghao on 2016/5/4.
 * 好友历史请求，针对所有业务进行包装
 */
public class FriendRequestServiceWrap {

    /**
     * 增量好友历史请求业务 保存更新本地请求验证信息
     * @param authInfos
     */
    public static void updateLocalAutoInfos(List<AuthInfo> authInfos) throws FriendServiceWrapException {
        try {
            new FriendRequestInfoService().batchInsert(authInfos);
        }catch (Exception e){
            throw new FriendServiceWrapException();
        }
    }

    /**
     * 保存更新本地的账户数据信息
     * @param responseAccounts
     */
    public static void updateAccounts(List<ResponseActomaAccount> responseAccounts) throws FriendServiceWrapException {
        try {
            new ActomAccountService().batchSaveAccountsAssociateWithAvatar(responseAccounts);
        }catch (Exception e){
            throw new FriendServiceWrapException();
        }
    }

    /**
     * 保存更新本地的好友历史请求数据
     * @param historyList
     */
    public static void updateLocalRequestHistory(List<FriendRequestHistory> historyList) throws FriendServiceWrapException {
        try{
            new FriendRequestService().batchSaveOrUpdate(historyList);
        }catch (Exception e){
            throw new FriendServiceWrapException();
        }
    }

}
