package com.xdja.contact.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.bean.BaseContact;
import com.xdja.contact.bean.dto.ContactNameDto;
import com.xdja.contact.dao.sqlbuilder.AbstractContactDaoSqlBuilder;
import com.xdja.contact.database.ContactDataBaseHelper;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wanghao on 2015/7/11.
 * 数据库操作基类
 */
public abstract class AbstractContactDao<T extends BaseContact> {

    private static AtomicBoolean isUsed = new AtomicBoolean(false);

    protected static final String EQUAL = " = ";

    protected static final String OR = " or ";

    public final SQLiteOpenHelper helper;

    protected SQLiteDatabase database;

    protected Context context;

    protected Cursor cursor;


    public AbstractContactDao() {
        this.context = ActomaController.getApp();
        this.helper = ContactDataBaseHelper.getInstance();
    }

    /**
     * 获取表名
     *
     * @return
     */
    protected abstract String getTableName();




    /*protected T metaData;

    public T query(String id){
        cursor = database.query(getTableName(),null,null,null,null,null,null);
        try {
            metaData = getSubClass().newInstance();
            metaData.getContentValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaData;
    }
    public abstract Class<? extends T> getSubClass();*/

    /**
     * 开启事务
     */
    public synchronized  void beginTransaction() {
        if (database != null && database.isOpen()) {
            database.beginTransaction();
        }
    }

    /**
     * 设置事务成功
     */
    public synchronized  void setTransactionSuccess() {
        if (database != null && database.isOpen()) {
            database.setTransactionSuccessful();
        }
    }

    /**
     * 提交事务
     */
    public synchronized  void endTransaction() {
        if (database != null && database.isOpen()) {
            database.endTransaction();
        }
    }

    public synchronized void getReadableDataBase(){
        isUsed.compareAndSet(false, true);
        database = helper.getReadableDatabase();
    }

    /**
     * <b>
     * 业务操作请遵循开关闭环(暂时不考虑打开数据库的性能了)
     * </b>
     *
     * @return
     */
    public synchronized void getWriteDataBase() {
        isUsed.compareAndSet(false, true);
        database = helper.getWritableDatabase();
    }

    /**
     * 关闭联系人数据库
     */
    public synchronized void closeDataBase() {
        if (isUsed.compareAndSet(true, false)) {
            database.close();
        }
    }

    public void closeCursor(){
        if (!ObjectUtil.cursorIsEmpty(cursor)) {
            cursor.close();
        }
    }

    /**
     * 根据账号查询账户表 关联好友表 关联集团成员表 关联 群组成员表
     * @param accounts
     * @return
     */
    public List<ContactNameDto> queryDisplayNameByIds(String groupId,List<String> accounts){
        List<ContactNameDto> datasource = new ArrayList<>();
        synchronized (helper){
            try {
                getReadableDataBase();
                cursor = database.rawQuery(AbstractContactDaoSqlBuilder.queryCommonNameSql(groupId,accounts).toString(), null);
                if (ObjectUtil.cursorIsEmpty(cursor)) return datasource;
                while (cursor.moveToNext()) {
                    ContactNameDto nameDto = new ContactNameDto(groupId,cursor);//midify by wal@xdja.com for 2575
                    datasource.add(nameDto);
                }
            }finally {
                closeCursor();
                closeDataBase();
            }
        }
        return datasource;
    }

}
