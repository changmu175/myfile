package com.xdja.imp.data.cache;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.persistent.PropertyUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:19</p>
 */
public class UserCacheImp implements UserCache {
    public final String PRONAME = "config.properties";

    public final String TAG_ACCOUNT = "account";

    public final String TAG_TICKET = "ticket";

    private UserEntity userEntity;

    private PropertyUtil util;

    private boolean isForeground = false;

    private String imParterner;

    //add by zya,20161027
    private LruCache<Long,String> mCacheText;
    //end

    //add by zya 20170103
    private Map<Long,Integer> mPercentMaps;
    //end by zya

    @Inject
    public UserCacheImp(PropertyUtil propertyUtil){
        this.util = propertyUtil;
        mCacheText = new LruCache<>(500);
        //add by zya 20170103
        mPercentMaps = new HashMap<>();
        //end by zya
    }

    @Override
    public void put(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public UserEntity get() {
        if (this.userEntity == null) {
            this.util.load(PRONAME);
            this.userEntity = new UserEntity();
            this.userEntity.setAccount(this.util.get(TAG_ACCOUNT));
            this.userEntity.setTicket(this.util.get(TAG_TICKET));
        }
        return this.userEntity;
    }

    @Override
    public boolean isMine(String user) {

        if (TextUtils.isEmpty(user)) {
            return false;
        }

        if (userEntity != null) {
            String account = userEntity.getAccount();
            if (!TextUtils.isEmpty(account)) {
                return account.equals(user);
            }
        }

        return false;
    }

    @Override
    public boolean isUserForeground() {
        return this.isForeground;
    }

    @Override
    public void setUserForeground(boolean isForeground) {
        this.isForeground = isForeground;
    }

    @Override
    public void setIMPartener(String talkerId) {
        this.imParterner = talkerId;
    }

    @Override
    public String getIMPartener() {
        return this.imParterner;
    }

    //add by zya,20161027
    @Override
    public void putCacheText(long key, String value) {
        LogUtil.getUtils().e("putCacheText Key:" + key);
        if(mCacheText == null){
            mCacheText = new LruCache<>(200); //消息缓冲为 200
        } 
		
		if (key > 0) { // modified by ycm for lint 2017/02/16
            mCacheText.put(key, value);
        }
    }

    @Override
    public String getCacheText(long key) {
        LogUtil.getUtils().e("getCacheText Key:" + key);
        return mCacheText != null ? mCacheText.get(key) : null;
    }

    @Override
    public void clearCacheText() {
        if(mCacheText != null) {
            mCacheText.evictAll();
            mCacheText = null;
        }
    }//end

    @Override
    public void putProgress(long msgId, int percent) {
        if(mPercentMaps == null){
            mPercentMaps = new HashMap<>();
        }

        mPercentMaps.put(msgId,percent);
    }

    @Override
    public int getProgress(long msgId) {
        return mPercentMaps.containsKey(msgId) ? mPercentMaps.get(msgId) : 0;
    }

    @Override
    public void removeProgress(long msgId) {
        if(mPercentMaps.containsKey(msgId)){
            mPercentMaps.remove(msgId);
        }
    }

    @Override
    public boolean containKey(long msgId) {
        return mPercentMaps != null && mPercentMaps.containsKey(msgId);// modified by ycm for lint 2017/02/16
    }

    @Override
    public void clearAllProgress() {
        if(mPercentMaps != null && mPercentMaps.size() > 0){
            mPercentMaps.clear();
            mPercentMaps = null;
        }
    }
}
