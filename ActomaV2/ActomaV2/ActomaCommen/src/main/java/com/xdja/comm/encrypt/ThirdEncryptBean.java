package com.xdja.comm.encrypt;

/**
 * Created by tangsha on 2016/7/26.
 */
public class ThirdEncryptBean {
    private final String groupId;
    private final byte[] key;
    private final String secKey;

    public ThirdEncryptBean(String groupId,byte[] key,String secKey){
        this.groupId = groupId;
        this.key = key;
        this.secKey = secKey;
    }

    public String getGroupId(){
        return groupId;
    }

    public byte[] getKey(){
        return key;
    }

    public String getSecKey(){
        return secKey;
    }
}
