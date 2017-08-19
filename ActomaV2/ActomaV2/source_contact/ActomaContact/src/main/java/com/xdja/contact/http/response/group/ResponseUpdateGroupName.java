package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by XDJA_XA on 2015/7/23.
 *
 */
public class ResponseUpdateGroupName implements Serializable {

    private String groupNamePy;

    private String groupNamePinyin;

    public String getGroupNamePy() {
        return groupNamePy;
    }

    public void setGroupNamePy(String groupNamePy) {
        this.groupNamePy = groupNamePy;
    }

    public String getGroupNamePinyin() {
        return groupNamePinyin;
    }

    public void setGroupNamePinyin(String groupNamePinyin) {
        this.groupNamePinyin = groupNamePinyin;
    }
}
