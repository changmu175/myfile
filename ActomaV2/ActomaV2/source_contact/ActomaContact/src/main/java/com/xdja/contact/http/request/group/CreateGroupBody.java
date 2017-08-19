package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

import java.util.List;

/**
 * Created by wanghao on 2015/7/21.
 * 创建群组参数请求体
    */
public class CreateGroupBody extends RequestBody {

    private String groupName;

    private List<String> members;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
