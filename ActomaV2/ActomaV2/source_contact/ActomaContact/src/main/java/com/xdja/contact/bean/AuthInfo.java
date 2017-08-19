package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.xdja.contact.database.columns.TableRequestInfo;

/**
 * Created by wanghao on 2015/7/8.
 * 好友请求验证信息
 */
public class AuthInfo implements BaseContact {

    private String id;
    /**
     * 帐号
     */
    private String account;

    /**
     * 保存时间
     */
    private String createTime;
    /**
     * 验证信息
     */
    private String validateInfo;

    public AuthInfo(){}

    public AuthInfo(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(TableRequestInfo.ID)));
        setAccount(cursor.getString(cursor.getColumnIndex(TableRequestInfo.ACCOUNT)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(TableRequestInfo.CREATE_TIME)));
        setValidateInfo(cursor.getString(cursor.getColumnIndex(TableRequestInfo.VALIDATE_INFO)));
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableRequestInfo.ACCOUNT, getAccount());
        values.put(TableRequestInfo.CREATE_TIME,getCreateTime());
        values.put(TableRequestInfo.VALIDATE_INFO,getValidateInfo());
        return values;
    }


    public AuthInfo(String reqAccount,String createTime, String validateInfo){
        setAccount(reqAccount);
        setValidateInfo(validateInfo);
        setCreateTime(createTime);
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValidateInfo() {
        return validateInfo;
    }

    public void setValidateInfo(String validateInfo) {
        this.validateInfo = validateInfo;
    }
}

