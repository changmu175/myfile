package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

import java.util.List;

/**
 * Created by wanghao on 2015/7/23.
 * 群内添加成员请求体
 */
@Deprecated
public class AddGroupMembersBody extends RequestBody {

    private List<String> toAddMembers;

    public void setToAddMembers(List<String> toAddMembers) {
        this.toAddMembers = toAddMembers;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this.toAddMembers);
    }
}
