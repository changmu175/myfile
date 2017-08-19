package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by tangsha on 2017/1/11.
 */
public class PushTypeMessage implements Serializable {
    public static final String REMOVE_MEMBER_TAG = "removeGroupMemberlist";
    public String flag;
    public String groupId;

    public String getFlag(){
        return flag;
    }

    public String getGroupId(){
        return groupId;
    }
}
