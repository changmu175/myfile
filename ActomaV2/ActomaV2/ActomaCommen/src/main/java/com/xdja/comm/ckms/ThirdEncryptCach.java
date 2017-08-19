package com.xdja.comm.ckms;

import com.xdja.comm.encrypt.ThirdEncryptBean;

import java.util.HashMap;

/**
 * Created by tangsha on 2016/7/26.
 */
public class ThirdEncryptCach {
    private static HashMap<String,ThirdEncryptBean> cachSecKeyInfo;

    private static ThirdEncryptCach instance;
    public static ThirdEncryptCach getInstance() {
        if(instance == null) {
            synchronized (ThirdEncryptCach.class) {
                if (instance == null) {
                    instance = new ThirdEncryptCach();
                    cachSecKeyInfo = new HashMap<>();
                }
            }
        }
        return instance;
    }

    public void putCacheKeyInfo(String groupId, byte[] key, String secKey){
        ThirdEncryptBean infoBean = new ThirdEncryptBean(groupId,key,secKey);
        cachSecKeyInfo.put(groupId,infoBean);
    }

    public ThirdEncryptBean getCacheKeyInfo(String groupId){
        return cachSecKeyInfo.get(groupId);
    }
}
