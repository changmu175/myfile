package com.xdja.contact.service;

import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.dao.FriendRequestInfoDao;

import java.util.List;

/**
 * Created by wanghao on 2015/7/26.
 */
public class FriendRequestInfoService {

    private FriendRequestInfoDao friendRequestInfoDao;

    public FriendRequestInfoService() {

        this.friendRequestInfoDao = new FriendRequestInfoDao();
    }


    /**
     * 批量保存请求验证的消息
     *
     * @param dataSource
     * @return
     */
    public boolean batchInsert(List<AuthInfo> dataSource) {
        boolean result = true;
        synchronized (friendRequestInfoDao.helper) {
            friendRequestInfoDao.getWriteDataBase();
            try {
                friendRequestInfoDao.beginTransaction();
                for (AuthInfo info : dataSource) {
                    if (insert(info, false)) {
                        continue;
                    } else {
                        result = false;
                    }
                }
                if (result) friendRequestInfoDao.setTransactionSuccess();
            } catch (Exception e) {
                result = false;
            } finally {
                friendRequestInfoDao.endTransaction();
                friendRequestInfoDao.closeDataBase();
            }
        }
        return result;
    }

    /**
     * 根据账号查询请求验证消息
     *
     * @param account
     * @return
     */
    public List<AuthInfo> queryVerificationInfo(String account) {
        List<AuthInfo> dataSource = null;
        synchronized (friendRequestInfoDao.helper) {
            friendRequestInfoDao.getReadableDataBase();
            dataSource = friendRequestInfoDao.queryByAccount(account);
            friendRequestInfoDao.closeDataBase();
        }
        return dataSource;
    }

    /**
     * 保存一条请求验证消息
     *
     * @param info
     * @return
     */
    public boolean insert(AuthInfo info) {
        return insert(info, true);
    }

    private boolean insert(AuthInfo info, boolean openDatabase) {
        long result = 0;
        synchronized (friendRequestInfoDao.helper) {
            if (openDatabase) friendRequestInfoDao.getWriteDataBase();
            result = friendRequestInfoDao.insert(info);
            if (openDatabase) friendRequestInfoDao.closeDataBase();
        }
        return result >= 1;
    }
    //start:add for 1113 by wal@xdja.com

    /**
     * 删除请求验证消息
     *
     * @param account
     * @return
     */
    public boolean delete(String account) {
        return delete(account, true);
    }

    private boolean delete(String account, boolean openDatabase) {
        long result = 0;
        synchronized (friendRequestInfoDao.helper) {
            if (openDatabase) friendRequestInfoDao.getWriteDataBase();
            result = friendRequestInfoDao.delete(account);
            if (openDatabase) friendRequestInfoDao.closeDataBase();
        }
        return result >= 1;
    }
    //end:add for 1113 by wal@xdja.com

}
