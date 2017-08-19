package com.xdja.contact.http.request.friend;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by wanghao on 2015/7/23.
 *
 *  添加好友接口  需要传入的参数
 */
public class AddFriendBody extends RequestBody {

    private String verification;

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

}
