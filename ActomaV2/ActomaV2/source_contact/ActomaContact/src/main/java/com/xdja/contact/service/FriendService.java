package com.xdja.contact.service;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.dao.FriendDao;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/7/13..
 * 好友业务操作数据对象
 * 当前对象函数里面添加业务逻辑判断
 */
public class FriendService {

    private final String TAG = "FriendService";

    private FriendDao dao;


    public FriendService(){
        this.dao = new FriendDao();
    }

    /**
     * <pre>
     * 功能:添加好友
     * 根据输入的账号或者手机号执行本地搜索,返回未删除的好友数据
     * </pre>
     * @param key 账号或者手机号码，<b>外界校验key业务之内不再校验</b>
     * @return Friend : 返回好友数据，有可能为空,
     */
    public Friend searchFriend(String key){
        Friend friend;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            friend = dao.searchFriend(key);
            dao.closeDataBase();
        }
        return friend;
    }


    /**
     * 查询所有好友已经排除了已经删除的好友
     * @return
     */
    public List<Friend> queryFriends(){
        List<Friend> result = new ArrayList<Friend>();
        synchronized (dao.helper){
            dao.getReadableDataBase();
            result = dao.queryAll();
            dao.closeDataBase();
        }
        return result;
    }

    public Friend findById(String account){
        Friend friend;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            friend = dao.findById(account);
            dao.closeDataBase();
        }
        return friend;
    }

    /**
     * 根据账号获取好友信息(包含已被删除的好友)
     * @param account
     * @return Friend friend : 可能为null
     */
    public Friend queryFriendByAccount(String account){
        LogUtil.getUtils().e("Actoma contact FriendService,queryFriendByAccount");
        Friend friend;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            friend = dao.queryFriendByAccount(account, true);
            dao.closeDataBase();
        }
        return friend;
    }
    /**
     * 根据账号获取好友信息,不包含已被删除的好友
     * @param account
     * @return Friend friend : 可能为null
     */
    public Friend queryFriendByAccountNonDeleted(String account){
        LogUtil.getUtils().e("Actoma contact FriendService,queryFriendByAccountNonDeleted");
        Friend friend;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            friend = dao.queryFriendByAccount(account, false);
            dao.closeDataBase();
        }
        return friend;
    }

    /**
     * 搜索本地好友
     * @param key
     * @return
     */
    public List<Friend> searchFriends(String key){
        List<Friend> result = new ArrayList<Friend>();
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            result =dao.searchFriends(key);
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * 删除好友不在好友列表展示
     * 主动删除的时候不删除物理数据，只是更新isShow状态同时清除已经设置的备注信息
     * @return bool : true 成功 ; false 失败
     */
    public boolean delete(Friend contact){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            contact.setRemark("");
            contact.setRemarkPy("");
            contact.setRemarkPinyin("");
            result = dao.delete(contact) > 0 ;
            dao.closeDataBase();
        }
        return  result;
    }

    /**
     * 批量保存或者更新好友数据
     * @param array
     * @return
     */
    public boolean batchSaveOrUpdate(List<Friend> array){
        Map<String,Friend> friendMap = new HashMap<String,Friend>();
        boolean result = true;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            try {
                List<Friend> localFriends = dao.queryFriends();
                if(!ObjectUtil.collectionIsEmpty(localFriends)) {
                    for (Friend friend : localFriends) {
                        friendMap.put(friend.getAccount(), friend);
                    }
                }
                dao.beginTransaction();
                if(ObjectUtil.mapIsEmpty(friendMap)){
                    for (Friend contact : array) {
                        if (ObjectUtil.stringIsEmpty(contact.getAccount())) {
                            result = false;
                            break;
                        } else {
                            if(dao.insert(contact) >= 0) {
                                continue;
                            } else {
                                result = false;
                                break;
                            }
                        }
                    }
                }else {
                    for (Friend contact : array) {
                        if (ObjectUtil.stringIsEmpty(contact.getAccount())) {
                            result = false;
                            break;
                        } else {
                            Friend localFriend = friendMap.get(contact.getAccount());
                            if (ObjectUtil.objectIsEmpty(localFriend)) {
                                if (dao.insert(contact) >= 0) {
                                    continue;
                                } else {
                                    result = false;
                                    break;
                                }
                            } else {
                                if (contact.getState().equals("-1")) {
                                    if (dao.delete(contact) > 0) {
                                        continue;
                                    } else {
                                        result = false;
                                        break;
                                    }
                                } else if (contact.getState().equals("1")) {
                                    if (dao.update(contact) > 0) {
                                        continue;
                                    } else {
                                        result = false;
                                        break;
                                    }
                                } else {
                                    //2015-12-31 如果是0 则针对在本地存在数据进行更新 主要是为了更新最大序列
                                    if (dao.update(contact) > 0) {
                                        continue;
                                    } else {
                                        result = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (result) dao.setTransactionSuccess();
            } catch (Exception e) {
                result = false;
                LogUtil.getUtils().d(" FriendService error, e=" + e);
            } finally {
                dao.endTransaction();
                dao.closeDataBase();
            }
        }
        return result;
    }

    /**
     * 统计本地存在的好友数量(包含已经删除的好友数据)
     * @return 好友数量
     */
    public int countFriends(){
        int result = 0;
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            result = dao.countFriends();
            dao.closeDataBase();
        }
        return result;
    }

    /**
     * <font color = 'red'>当前函数只能被接受好友时使用</font>
     * 注意在调用该函数时，如果执行更新动作，则需要清除原有已经设置的备注
     * 保存或者更新 主要用户显示状态
     * @return
     */
    public boolean saveOrUpdate(Friend friend){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            Friend localFriend = dao.query(friend.getAccount());
            if(ObjectUtil.objectIsEmpty(localFriend)){
                result = dao.insert(friend) >= 0;
            }else{
                localFriend.setRemark("");
                localFriend.setRemarkPinyin("");
                localFriend.setRemarkPy("");
                localFriend.setIsShow("1");
                result = dao.update(localFriend) > 0;
            }
            dao.closeDataBase();
        }
        return result;
    }


    /**
     * 保存是否成功
     * @param friend
     * @return
     */
    public boolean insert(Friend friend){
        boolean result = false;
        synchronized (dao.helper){
            dao.getWriteDataBase();
            result = dao.insert(friend)>= 0;
            dao.closeDataBase();
        }
        return result;
    }

    public boolean update(Friend friend){
        boolean bool = false;
        synchronized (dao.helper) {
            dao.getWriteDataBase();
            bool = dao.update(friend) > 0;
            dao.closeDataBase();
        }
        return bool;
    }



    /**
     * 查询最大的 更新序列
     * @return
     */
    public String queryMaxUpdateSerial(){
        String result = "-1";
        synchronized (dao.helper) {
            dao.getReadableDataBase();
            result = dao.queryMaxUpdateSerial();
            dao.closeDataBase();
        }
        if(ObjectUtil.stringIsEmpty(result)) result = "0";
        return result;
    }
}
