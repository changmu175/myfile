package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.xdja.contact.database.columns.error.TableErrorPush;

/**
 * Created by wanghao on 2015/7/15.
 *
 */
public class ErrorPush implements BaseContact {

    private String id;
    //推送类型唯一标示
    private String transId;

    private String reason;

    private String transType;

    private String createTime;

    private String updateTime;





    public ErrorPush(){}

    public ErrorPush(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(TableErrorPush.ID)));
        setTransId(cursor.getString(cursor.getColumnIndex(TableErrorPush.TRANS_ID)));
        setReason(cursor.getString(cursor.getColumnIndex(TableErrorPush.REASON)));
        setTransType(cursor.getString(cursor.getColumnIndex(TableErrorPush.TRANS_TYPE)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(TableErrorPush.CREATE_TIME)));
        setUpdateTime(cursor.getString(cursor.getColumnIndex(TableErrorPush.UPDATE_TIME)));
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableErrorPush.TRANS_ID,getTransId());
        values.put(TableErrorPush.REASON,getReason());
        values.put(TableErrorPush.TRANS_TYPE,getTransType());
        values.put(TableErrorPush.CREATE_TIME,getCreateTime());
        values.put(TableErrorPush.UPDATE_TIME,getUpdateTime());
        return values;
    }


    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
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
