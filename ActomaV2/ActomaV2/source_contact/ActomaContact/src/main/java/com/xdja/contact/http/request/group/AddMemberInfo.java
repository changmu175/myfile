package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by XDJA_XA on 2015/7/23.
 *
 */
@Deprecated
public class AddMemberInfo extends RequestBody {

    private String account;

    public AddMemberInfo(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
