package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.xdja.contact.database.columns.TableEncryptRecord;

/**
 * Created by wanghao on 2015/7/9.
 * 安全通信记录表
 */
public class EncryptRecord implements BaseContact {

    private String id;

    private String account;

    private String lastConnectedTime;
    /**
     * 0 : 关闭 ；1 : 开启
     */
    private String openTransfer;

    public static final String CLOSE = "0";

    public static final String OPEN = "1";



    public EncryptRecord(){}

    public EncryptRecord(Cursor cursor){
        setId(getStringFromCursor(cursor, TableEncryptRecord.ID));
        setAccount(getStringFromCursor(cursor, TableEncryptRecord.ACCOUNT));
        setLastConnectedTime(getStringFromCursor(cursor, TableEncryptRecord.LAST_CONNECTED_TIME));
        setOpenTransfer(getStringFromCursor(cursor, TableEncryptRecord.OPEN_TRANSFER));
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableEncryptRecord.ACCOUNT,getAccount());
        contentValues.put(TableEncryptRecord.LAST_CONNECTED_TIME,getLastConnectedTime());
        contentValues.put(TableEncryptRecord.OPEN_TRANSFER,getOpenTransfer());
        return contentValues;
    }

    public static class Builder{

        private String builderAccount;

        public Builder(String account){

            this.builderAccount = account;
        }

        public EncryptRecord open(){
            EncryptRecord encryptRecord = new EncryptRecord();
            encryptRecord.setAccount(builderAccount);
            encryptRecord.setLastConnectedTime(String.valueOf(System.currentTimeMillis()));
            encryptRecord.setOpenTransfer(EncryptRecord.OPEN);
            return encryptRecord;
        }

        public EncryptRecord close(){
            EncryptRecord encryptRecord = new EncryptRecord();
            encryptRecord.setAccount(builderAccount);
            encryptRecord.setLastConnectedTime(String.valueOf(System.currentTimeMillis()));
            encryptRecord.setOpenTransfer(EncryptRecord.CLOSE);
            return encryptRecord;
        }
    }


    private String getStringFromCursor(Cursor cursor,String columnName){
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));

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

    public String getLastConnectedTime() {
        return lastConnectedTime;
    }

    public void setLastConnectedTime(String lastConnectedTime) {
        this.lastConnectedTime = lastConnectedTime;
    }

    public String getOpenTransfer() {
        return openTransfer;
    }

    public void setOpenTransfer(String openTransfer) {
        this.openTransfer = openTransfer;
    }

}
