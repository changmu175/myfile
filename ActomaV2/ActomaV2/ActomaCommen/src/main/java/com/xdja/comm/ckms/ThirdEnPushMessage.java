package com.xdja.comm.ckms;

import android.util.Log;

import com.alibaba.fastjson.JSON;


/**
 * Created by tangsha on 2016/7/26.
 */
public class ThirdEnPushMessage {
    private String groupId;
    private String secKey;

    ThirdEnPushMessage(String id, String secKey){
        this.groupId = id;
        this.secKey = secKey;
    }

    public void setGroupId(String id){
        this.groupId = id;
    }

    public void setSecKey(String secKey){
        this.secKey = secKey;
    }

    public String getGroupId(){
        return groupId;
    }

    public String getSecKey(){
        return secKey;
    }

    public String toJsonString() {
        String str =  JSON.toJSONString(this);
        Log.d("ThirdEnPushMessage","toJsonString str "+str);
        return str;
    }

}
