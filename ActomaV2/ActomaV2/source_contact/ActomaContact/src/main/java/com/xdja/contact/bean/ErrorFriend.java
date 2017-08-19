package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.xdja.contact.database.columns.error.TableErrorFriend;

/**
 * Created by wanghao on 2015/7/15.
 */
public class ErrorFriend implements BaseContact {

    private String id;

    private String account;

    private String type;

    private String reason;

    private String createTime;

    private String updateTime;



    public ErrorFriend(){}

    public ErrorFriend(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(TableErrorFriend.ID)));
        setAccount(cursor.getString(cursor.getColumnIndex(TableErrorFriend.ACCOUNT)));
        setType(cursor.getString(cursor.getColumnIndex(TableErrorFriend.TYPE)));
        setReason(cursor.getString(cursor.getColumnIndex(TableErrorFriend.REASON)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(TableErrorFriend.CREATE_TIME)));
        setUpdateTime(cursor.getString(cursor.getColumnIndex(TableErrorFriend.UPDATE_TIME)));

    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableErrorFriend.ACCOUNT,getAccount());
        values.put(TableErrorFriend.TYPE,getType());
        values.put(TableErrorFriend.REASON,getReason());
        values.put(TableErrorFriend.CREATE_TIME,getCreateTime());
        values.put(TableErrorFriend.UPDATE_TIME,getUpdateTime());
        return values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
