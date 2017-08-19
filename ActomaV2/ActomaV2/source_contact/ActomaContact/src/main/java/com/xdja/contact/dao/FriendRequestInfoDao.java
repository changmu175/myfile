package com.xdja.contact.dao;

import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.dao.sqlbuilder.FriendRequestInfoDaoSqlBuilder;
import com.xdja.contact.database.columns.TableRequestInfo;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/15.
 */
public class FriendRequestInfoDao extends AbstractContactDao<AuthInfo> {

    @Override
    protected String getTableName() {
        return TableRequestInfo.TABLE_NAME;
    }

    public AuthInfo query(String id) {
        return null;
    }

    public List<AuthInfo> queryAll(){
        return null;
    }

    /**
     * 根据账户获取所有的请求信息
     * @param account
     * @return dataSource dataSource.size >= 0
     */
    public List<AuthInfo> queryByAccount(String account){
        List<AuthInfo> dataSource = new ArrayList<AuthInfo>();
        try{
            cursor = database.rawQuery(FriendRequestInfoDaoSqlBuilder.buildQuerySql(account),null);
            while(cursor.moveToNext()){
                AuthInfo info = new AuthInfo(cursor);
                dataSource.add(info);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }



    /**
     * 保存请求信息
     * @param authInfo
     * @return
     */
    public long insert(AuthInfo authInfo) {
        long result = -1 ;
        try {
            result = database.insert(TableRequestInfo.TABLE_NAME, null, authInfo.getContentValues());
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * @param authInfo
     * @return
     */
    public int update(AuthInfo authInfo) {
        int result = -1 ;
        try {
            String whereClause = TableRequestInfo.ACCOUNT + " =  ? ";
            result = database.update(TableRequestInfo.TABLE_NAME, authInfo.getContentValues(),whereClause,new String[]{authInfo.getAccount()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    //start:add for 1113 by wal@xdja.com
    /**
     * @param account
     */
    public long delete(String account){
        long result = -1 ;
        try {
            String whereClause = TableRequestInfo.ACCOUNT + " =  ? ";
            result = database.delete(TableRequestInfo.TABLE_NAME,whereClause,new String[]{account});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }
    //end:add for 1113 by wal@xdja.com
}
