package com.xdja.contact.dao;

import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.database.columns.error.TableErrorPush;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/15.
 */
public class ErrorPushDao extends AbstractContactDao<ErrorPush> {

    @Override
    protected String getTableName() {
        return TableErrorPush.TABLE_NAME;
    }

    public ErrorPush query(String transId) {
        ErrorPush errorPush = null;
        try{
            String selection = TableErrorPush.TRANS_ID + EQUAL + "?";
            cursor = database.query(getTableName(),null,selection,new String[]{transId},null,null,null);
            if (cursor.moveToFirst()){
                errorPush = new ErrorPush(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
           closeCursor();
        }
        return errorPush;
    }

    /**
     * 查询推送出错的记录
     * @return
     */
    public List<ErrorPush> queryAll() {
        List<ErrorPush> dataSource = new ArrayList<ErrorPush>();
        try{
            cursor = database.query(getTableName(),null,null,null,null,null,null);
            while(cursor.moveToNext()){
                ErrorPush push = new ErrorPush(cursor);
                dataSource.add(push);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }

    /**
     * 保存推送记录
     * @param errorPush
     * @return
     */
    public long insert(ErrorPush errorPush) {
        long result = -1 ;
        try {
            result = database.insert(TableErrorPush.TABLE_NAME, null, errorPush.getContentValues());
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 更新推送错误信息
     * @param errorPush
     * @return
     */
    public int update(ErrorPush errorPush) {
        int result = 0;
        try {
            String whereClause = TableErrorPush.TRANS_ID + " =  ? ";
            result = database.update(TableErrorPush.TABLE_NAME, errorPush.getContentValues(), whereClause, new String[]{errorPush.getTransId()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 删除推送异常数据
     * @param errorPush
     * @return
     */
    public int delete(ErrorPush errorPush) {
        String whereClause = TableErrorPush.TRANS_ID + " =  ? ";
        int result = -1;
        try {
            result = database.delete(TableErrorPush.TABLE_NAME, whereClause, new String[]{errorPush.getTransId()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }
}
