package com.xdja.contactopproxy;

import android.support.v4.util.LruCache;

import com.xdja.contactopproxy.Bean.ContactInfo;

/**
 * 联系人/群组缓存
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-09-09 13:57
 */
public final class ContactCache {// 群组50 ， 账号200

    private static ContactCache instance = null;

    private LruCache<String, ContactInfo> contactsCache = null;
    private LruCache<String, ContactInfo> groupsCache = null;

    public static synchronized ContactCache getInstance() {
        if (instance == null) {
            instance = new ContactCache();
        }
        return instance;
    }

    /**
     * 将数据保存到缓存中
     * @param groupId 群组ID
     * @param info ContactInfo
     * @return ContactInfo
     */
    public ContactInfo putContact(String groupId, ContactInfo info){
        //[S]modify by lixiaolong on 20160918. fix bug 4167. review gbc.
        if (info != null) {
            String key = info.getAccount();
            if (key != null && !"".equals(key)) {
                if (groupId != null && !"".equals(groupId)) {
                    key = info.getAccount() + "|" + groupId;
                }
                return putInCache(false, key, info);
            }
        }
        return null;
        //[E]modify by lixiaolong on 20160918. fix bug 4167. review gbc.
    }

    /**
     * 获取缓存中的数据
     * @param groupId 群组ID
     * @param account 账号
     * @return ContactInfo
     */
    public ContactInfo getContact(String groupId, String account){
        if (account != null && !"".equals(account)) {
            String key = account;
            if (groupId != null && !"".equals(groupId)) {
                key = account + "|" + groupId;
            }
            return getOnCache(false, key);
        } else {
            return null;
        }
    }

    /**
     * 将数据保存到缓存中
     * @param groupId 群组ID
     * @param info ContactInfo
     * @return ContactInfo
     */
    public ContactInfo putGroup(String groupId, ContactInfo info){
        if (info == null) return null;
        if (groupId != null && !"".equals(groupId)) {
            return putInCache(true, groupId, info);
        } else {
            return null;
        }
    }

    /**
     * 获取缓存中的数据
     * @param groupId 群组ID
     * @return ContactInfo
     */
    public ContactInfo getGroup(String groupId){
        if (groupId != null && !"".equals(groupId)) {
            return getOnCache(true, groupId);
        } else {
            return null;
        }
    }

    public void clearCache(){
        if (groupsCache != null){
            groupsCache.evictAll();
        }
        if (contactsCache != null){
            contactsCache.evictAll();
        }
    }

    private ContactCache() {
        this.groupsCache = new LruCache<>(500); //扩大缓冲到500
        this.contactsCache = new LruCache<>(300); //扩大缓冲到300
    }

    private ContactInfo putInCache(boolean isGroup, String key, ContactInfo value){
        if (isGroup) {
            return groupsCache.put(key, value);
        } else {
            return contactsCache.put(key, value);
        }
    }

    private ContactInfo getOnCache(boolean isGroup, String key){
        if (isGroup) {
            return groupsCache.get(key);
        } else {
            return contactsCache.get(key);
        }
    }
}
