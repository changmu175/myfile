package com.xdja.contact.http.response.group;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XDJA_XA on 2015/7/23.
 *  wanghao 2016-01-21 修正
 */
public class ResponseGroupMembers implements Serializable {

    private String groupId;

    private List<ResponseGroupMember> members;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<ResponseGroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<ResponseGroupMember> members) {
        this.members = members;
    }
}
