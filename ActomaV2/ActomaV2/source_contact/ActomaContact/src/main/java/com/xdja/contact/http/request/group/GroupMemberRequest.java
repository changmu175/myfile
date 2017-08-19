package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

/**
 * Created by XDJA_XA on 2015/7/23.
 *
 */
public class GroupMemberRequest extends RequestBody {
    /**
     * 更新的群组ID
     */
    private String groupId;
    /**
     * 群组的成员更新序号
     */
    private String seq;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
