package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.xdja.contact.database.columns.error.TableErrorDepartment;

/**
 * Created by wanghao on 2015/7/15.
 */
public class ErrorDepartment implements BaseContact {

    private String id;

    private String deptId;

    private String memberId;

    private String type;

    private String reason;

    private String createTime;

    private String updateTime;



    public ErrorDepartment(){}

    public ErrorDepartment(Cursor cursor){
        setId(getStringFromCursor(cursor, TableErrorDepartment.ID));
        setType(getStringFromCursor(cursor, TableErrorDepartment.TYPE));
        setReason(getStringFromCursor(cursor, TableErrorDepartment.REASON));
        setCreateTime(getStringFromCursor(cursor, TableErrorDepartment.CREATE_TIME));
        setDeptId(getStringFromCursor(cursor, TableErrorDepartment.DEPT_ID));
        setMemberId(getStringFromCursor(cursor, TableErrorDepartment.MEMBER_ID));
    }
    private String getStringFromCursor(Cursor cursor,String columnName){
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }
    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableErrorDepartment.REASON,getReason());
        contentValues.put(TableErrorDepartment.UPDATE_TIME,getUpdateTime());
        contentValues.put(TableErrorDepartment.TYPE,getType());
        contentValues.put(TableErrorDepartment.MEMBER_ID,getMemberId());
        contentValues.put(TableErrorDepartment.CREATE_TIME,getCreateTime());
        return contentValues;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
