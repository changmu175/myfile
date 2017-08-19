package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by wanghao on 2015/7/15.
 */
public class ErrorGroup implements BaseContact {

    private String id;

    private String groupId;

    private String type;

    private String reason;

    private String createTime;

    private String updateTime;


    public ErrorGroup(){}

    public ErrorGroup(Cursor cursor){}

    @Override
    public ContentValues getContentValues() {
        return null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
