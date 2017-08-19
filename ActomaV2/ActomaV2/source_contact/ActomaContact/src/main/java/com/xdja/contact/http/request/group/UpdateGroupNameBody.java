package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by XDJA_XA on 2015/7/23.
 * modify wanghao 20116-02-27
 * 更新群成名请求体
 */
public class UpdateGroupNameBody extends RequestBody {

    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
