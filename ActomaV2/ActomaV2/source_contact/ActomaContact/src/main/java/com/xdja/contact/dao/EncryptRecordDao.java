package com.xdja.contact.dao;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.database.columns.TableEncryptRecord;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * 加密通道记录表
 *
 * @author hkb.
 * @since 2015/8/5/0005.
 */
public class EncryptRecordDao extends AbstractContactDao<EncryptRecord> {

    public EncryptRecord query(String account) {
        EncryptRecord encryptRecord = null;
        try {
            cursor = database.query(TableEncryptRecord.TABLE_NAME, null, TableEncryptRecord.ACCOUNT + " = ? ", new String[]{account}, null, null, null);
            if(cursor.moveToFirst()){
                encryptRecord = new EncryptRecord(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return encryptRecord;
    }

    /**
     * 查询历史选中的人
     * @return
     */
    public EncryptRecord lastSelectedRecord() {
        EncryptRecord encryptRecord = null;
        try{
            cursor = database.query(TableEncryptRecord.TABLE_NAME, null, TableEncryptRecord.OPEN_TRANSFER + " = ? ", new String[]{"1"}, null, null, null);
            if(cursor.moveToFirst()) {
                encryptRecord = new EncryptRecord(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return encryptRecord;
    }

    /**
     * 关闭 指定账号的安全通道
     * @param account
     * @return
     */
    public int updateCloseTransferByAccount(String account){
        EncryptRecord record = new EncryptRecord.Builder(account).close();
        String whereArgs = TableEncryptRecord.ACCOUNT + EQUAL + " ? " ;
        String[] args = new String[]{account};
        return database.update(getTableName(),record.getContentValues(), whereArgs, args);
    }

    /**
     * 开启指定账号的安全通道
     * @param account
     * @return
     */
    public int updateOpenTransferByAccount(String account){
        EncryptRecord record = new EncryptRecord.Builder(account).open();
        String whereArgs = TableEncryptRecord.ACCOUNT + EQUAL + " ? " ;
        String[] args = new String[]{account};
        return database.update(getTableName(),record.getContentValues(), whereArgs, args);
    }

    /**
     * 根据账号保存一条加密通道记录
     * @param account
     * @return
     */
    public long insertOpenTransferByAccount(String account){
        EncryptRecord record = new EncryptRecord.Builder(account).open();
        return database.insert(getTableName(),null,record.getContentValues());
    }



    public List<EncryptRecord> queryAll() {
        List<EncryptRecord> records= new ArrayList<>();
        try{
            cursor = database.query(TableEncryptRecord.TABLE_NAME, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                records.add(new EncryptRecord(cursor));
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return records;
    }

    @Override
    protected String getTableName() {
        return TableEncryptRecord.TABLE_NAME;
    }

    public long insert(@NonNull EncryptRecord encryptRecord) {
        return database.insert(getTableName(), null,encryptRecord.getContentValues());
    }

    public int update(EncryptRecord encryptRecord) {
        return database.update(getTableName(), encryptRecord.getContentValues(), TableEncryptRecord.ID + EQUAL + "?", new String[]{encryptRecord.getId()});
    }
    public int update(EncryptRecord newEncryptRecord,String account) {
        SQLiteStatement statement = database.compileStatement(updateSqlBuilder(newEncryptRecord,account));
        return statement.executeUpdateDelete();
    }

    private String updateSqlBuilder(EncryptRecord encryptRecord,String account){
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(getTableName());
        builder.append(" set ");
        builder.append(TableEncryptRecord.ACCOUNT);
        builder.append(" = '" + encryptRecord.getAccount());
        builder.append("',");
        builder.append(TableEncryptRecord.LAST_CONNECTED_TIME);
        builder.append(" = '"+encryptRecord.getLastConnectedTime());
        builder.append("',");
        builder.append(TableEncryptRecord.OPEN_TRANSFER);
        builder.append(" = '"+encryptRecord.getOpenTransfer());
        builder.append("' where ");
        builder.append(TableEncryptRecord.ACCOUNT);
        builder.append("='");
        builder.append(account+"';");

        return builder.toString();
    }

    public int delete(EncryptRecord encryptRecord) {
        return database.delete(getTableName(),TableEncryptRecord.ACCOUNT+EQUAL+"?",new String[]{encryptRecord.getAccount()});
    }
}
