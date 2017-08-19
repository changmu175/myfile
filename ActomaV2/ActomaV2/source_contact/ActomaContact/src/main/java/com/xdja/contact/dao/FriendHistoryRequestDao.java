package com.xdja.contact.dao;

import android.database.sqlite.SQLiteStatement;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.dao.sqlbuilder.FriendHistoryRequestDaoSqlBuilder;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableFriendHistory;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/15.
 */
public class FriendHistoryRequestDao extends AbstractContactDao<FriendRequestHistory> {


    @Override
    protected String getTableName() {
        return TableFriendHistory.TABLE_NAME;
    }


    public FriendRequestHistory query(FriendRequestHistory history) {
        FriendRequestHistory requestHistory = null;
        try{
            cursor = database.rawQuery(FriendHistoryRequestDaoSqlBuilder.querySql(history.getReqAccount(),history.getRecAccount()),null);
            if(cursor.moveToFirst()) {
                requestHistory = new FriendRequestHistory(cursor);
            }
        }catch(Exception e){
            new ContactDaoException(e);
        }finally{
            closeCursor();
        }
        return requestHistory;
    }

    public FriendRequestHistory query(String reqA,String recA) {
        FriendRequestHistory requestHistory = null;
        try{
            cursor = database.rawQuery(FriendHistoryRequestDaoSqlBuilder.querySql(reqA,recA),null);
            if(cursor.moveToFirst()) {
                requestHistory = new FriendRequestHistory(cursor);
            }
        }catch(Exception e){
            new ContactDaoException(e);
        }finally{
            closeCursor();
        }
        return requestHistory;
    }

    /**
     * 查询所有历史请求记录
     *
     * @return
     */
    public List<FriendRequestHistory> queryAll() {
        List<FriendRequestHistory> dataSource = new ArrayList<FriendRequestHistory>();
        try {
            cursor = database.rawQuery(FriendHistoryRequestDaoSqlBuilder.builderHistorySql(), null);
            while (cursor.moveToNext()) {
                FriendRequestHistory history = new FriendRequestHistory(cursor);
                ActomaAccount actomaAccount = new ActomaAccount();
                String alias = cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS));
                String nickName = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME));
                String nickNamePY = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_PY));
                String nickNamePinYin = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_FULL_PY));
                actomaAccount.setAlias(alias);
                actomaAccount.setNickname(nickName);
                actomaAccount.setNicknamePy(nickNamePY);
                actomaAccount.setNicknamePinyin(nickNamePinYin);
                Avatar avatar = new Avatar();
                String thumbnailId = cursor.getString(cursor.getColumnIndex(TableAccountAvatar.THUMBNAIL));
                String avatarId = cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR));
                avatar.setThumbnail(thumbnailId);
                avatar.setAvatar(avatarId);
                history.setActomaAccount(actomaAccount);
                history.setAvatar(avatar);
                dataSource.add(history);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return dataSource;
    }

    /**
     * 保存好友请求历史数据
     *
     * @param baseContact
     * @return
     */
    public long insert(FriendRequestHistory baseContact) {
        long result = -1;
        try {
            result = database.insert(TableFriendHistory.TABLE_NAME, null, baseContact.getContentValues());
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }


    /**
     * 更新好友请求历史数据
     *
     * @param baseContact
     * @return @return result > 0 : 成功 ； result < 0 : 失败
     */
    public int update(FriendRequestHistory baseContact) {
        int result = 0;
        try {
            //String whereClause = TableFriendHistory.SHOW_ACCOUNT + " =  ? ";
            String whereClause = TableFriendHistory.C_REQ_ACCOUNT+ " =  ?  and "+TableFriendHistory.C_REC_ACCOUNT + " =  ? ";//add by lwl

            result = database.update(TableFriendHistory.TABLE_NAME, baseContact.getContentValues(), whereClause, new String[]{baseContact.getReqAccount(),baseContact.getRecAccount()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 统计未读数量
     * TableFriendHistory.IS_READ  0 : 未读 1 :已读
     * @return
     */
    public int countNewFriend() {
        int count = 0;
        try {
            cursor = database.rawQuery(FriendHistoryRequestDaoSqlBuilder.countNewFriendsSql(), null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return count;
    }

    public boolean updateRequestHistoryState(List<String> accountList,String state){
        try {
            SQLiteStatement statement = database.compileStatement(FriendHistoryRequestDaoSqlBuilder.updateHistoriesRequest(accountList, state));
            return statement.executeUpdateDelete()>0;
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return false;
    }



    /**
     * 更新未读状态
     * @return
     */
    public boolean updateIsRead() {
        int result = -1 ;
        try{
            SQLiteStatement statement = database.compileStatement(FriendHistoryRequestDaoSqlBuilder.updateIsReadSql());
            result = statement.executeUpdateDelete();
        } catch (Exception e){
            new ContactDaoException(e);
        }
        return result >= 0 ;
    }




    /**
     * 获取最大的update serial
     *
     * @return
     */
    public String queryMaxUpdateSerial() {
        String result = "-1";
        try {
            cursor = database.rawQuery(FriendHistoryRequestDaoSqlBuilder.queryMaxUpdateSerialSql(), null);
            if(cursor.moveToFirst()){
                result = cursor.getString(0);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }
}

