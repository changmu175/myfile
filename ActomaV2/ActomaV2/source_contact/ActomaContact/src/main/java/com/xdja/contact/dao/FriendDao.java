package com.xdja.contact.dao;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.dao.sqlbuilder.FriendDaoSqlBuilder;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableFriendHistory;
import com.xdja.contact.database.columns.TableRequestInfo;
import com.xdja.contact.exception.ContactDaoException;
import com.xdja.contact.util.BroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/9.
 * 操作好友功能数据对象
 */
public class FriendDao extends AbstractContactDao<Friend> {


    public Friend query(String id) {
        return queryFriendByAccount(id, true);
    }

    public Friend findById(String account){
        Friend friend = null;
        try {
            cursor = database.rawQuery(FriendDaoSqlBuilder.findByIdSql(account), null);
            if (cursor.moveToFirst()) {
                friend = new Friend(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return friend;
    }
    /*******以下代码未重构*******************/


    public List<Friend> queryFriends() {
        List<Friend> dataSource = new ArrayList<>();
        try{
            String sql = FriendDaoSqlBuilder.queryAllFriendSql();
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Friend friend = new Friend(cursor);

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

                Member member = new Member();
                member.setCursor(cursor);


                friend.setActomaAccount(actomaAccount);
                friend.setAvatar(avatar);
                friend.setMember(member);
                dataSource.add(friend);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return dataSource;
    }

    /**
     * 查询所有好友数据
     *
     * @return
     */
    public List<Friend> queryAll() {
        List<Friend> dataSource = new ArrayList<Friend>();
        try {
            String sql = FriendDaoSqlBuilder.queryAllFriendSql() + FriendDaoSqlBuilder.whereShowFriendSql().toString();
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Friend friend = new Friend(cursor);


                ActomaAccount actomaAccount = new ActomaAccount();
                String alias = cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS));
                String nickName = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME));
                String nickNamePY = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_PY));
                String nickNamePinYin = cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_FULL_PY));
                //start:fix 1212 by wal@xdja.com
                String identify = cursor.getString(cursor.getColumnIndex(TableActomaAccount.IDENTIFY));
                actomaAccount.setIdentify(identify);
                //end:fix 1212 by wal@xdja.com

                actomaAccount.setAlias(alias);
                actomaAccount.setNickname(nickName);
                actomaAccount.setNicknamePy(nickNamePY);
                actomaAccount.setNicknamePinyin(nickNamePinYin);



                Avatar avatar = new Avatar();
                String thumbnailId = cursor.getString(cursor.getColumnIndex(TableAccountAvatar.THUMBNAIL));
                String avatarId = cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR));
                avatar.setThumbnail(thumbnailId);
                avatar.setAvatar(avatarId);

                Member member = new Member();
                member.setCursor(cursor);

                friend.setActomaAccount(actomaAccount);
                friend.setAvatar(avatar);
                friend.setMember(member);
                dataSource.add(friend);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return dataSource;
    }






    /**
     * 根据账号获取联系人数据
     *
     * @param account
     * @return 可能返回为null
     */
    public Friend queryFriendByAccount(String account, boolean containIsDel) {
        Friend friend = null;
        StringBuffer sqlBuilder = FriendDaoSqlBuilder.baseSqlBuilder();
        sqlBuilder.append(FriendDaoSqlBuilder.queryFriendSql(account, containIsDel));
        String sqls = sqlBuilder.toString();
        try {
            cursor = database.rawQuery(sqls, null);
            if (cursor.moveToFirst()) {
                friend = new Friend(cursor);

                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                Avatar avatarInfo = new Avatar(cursor);

                Member member = new Member();
                member.setCursor(cursor);
                friend.setAccount(account);
                friend.setAvatar(avatarInfo);
                friend.setActomaAccount(actomaAccount);
                friend.setMember(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return friend;
    }


    /**
     * 搜索
     *
     * @param key
     * @return 可能数据的大小:0
     */
    public List<Friend> searchFriends(String key) {
        List<Friend> dataSource = new ArrayList<Friend>();
        try {
            cursor = database.rawQuery(buildSearchSql(key).toString(), null);
            while (cursor.moveToNext()) {
                Friend friend = new Friend(cursor);
                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                Avatar avatar = new Avatar(cursor);
                Member member = new Member();
                member.setCursor(cursor);
                friend.setAvatar(avatar);
                friend.setActomaAccount(actomaAccount);
                friend.setMember(member);
                dataSource.add(friend);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return dataSource;
    }


    /**
     * 本地好友关联账户信息查询
     * @param key 账号或者手机号码,业务层之外进行校验
     * @return Friend : 返回数据有可能为空，关联账户表有可能没有账户信息
     */
    public Friend searchFriend(String key) {
        Friend friend = null;
        try {
            cursor = database.rawQuery(FriendDaoSqlBuilder.buildLocalSearchSql(key).toString(), null);
            if (cursor.moveToFirst()) {
                friend = new Friend(cursor);
                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                friend.setActomaAccount(actomaAccount);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return friend ;
    }

    /**
     * 保存好友数据
     *
     * @param baseContact
     * @return result > -1 ：成功 ; result <= -1 ： 失败
     */
    public long insert(Friend baseContact) {
        long result = -1;
        try {
            result = database.insert(TableFriend.TABLE_NAME, null, baseContact.getContentValues());
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 更新好友数据
     *
     * @param baseContact
     * @return @return result > 0 : 成功 ； result < 0 : 失败
     */
    public int update(Friend baseContact) {
        int result = 0;
        try {
            String whereClause = TableFriend.ACCOUNT + " =  ? ";
            result = database.update(TableFriend.TABLE_NAME, baseContact.getContentValues(), whereClause, new String[]{baseContact.getAccount()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }


    /**
     * 搜索的时候支持使用拼音的首字母进行模糊搜索。
     * 同时对安通帐号、昵称（若存在）、备注名（若存在）
     * ------>当前不提供如下搜索:手机（若存在）、邮箱（若存在）
     *
     * @param key
     * @return
     */
    private StringBuffer buildSearchSql(String key) {
        StringBuffer sql = FriendDaoSqlBuilder.baseSqlBuilder();
        sql.append(FriendDaoSqlBuilder.searchSql(key));
        return sql;
    }

    @Override
    protected String getTableName() {
        return TableFriend.TABLE_NAME;
    }

    /**
     * <b>删除好友
     * <p>不是执行业务上的删除,只是更新表内一个字段不让显示在列表</p></b>
     *
     * @param baseContact
     * @return result > 0 : 成功 ； result < 0 : 失败
     */
    public int delete(Friend baseContact) {
        int result = 0;
        try {
            boolean isDeleteInfo=false;//add by lwl just in case diss historyrequest
            Friend friend= query(baseContact.getAccount());
            if(friend!=null&&friend.isShow()){
                isDeleteInfo=true;
            }
            String whereClause = TableFriend.ACCOUNT + " =  ? ";
            baseContact.setIsShow("0");
            result = database.update(TableFriend.TABLE_NAME, baseContact.getContentValues(), whereClause, new String[]{baseContact.getAccount()});
            //add by lwl start
            if(isDeleteInfo) {
                database.delete(TableFriendHistory.TABLE_NAME, TableFriendHistory.SHOW_ACCOUNT + "=? ", new String[]{baseContact.getAccount() + ""});
                database.delete(TableRequestInfo.TABLE_NAME, TableRequestInfo.ACCOUNT + "=? ", new String[]{baseContact.getAccount() + ""});
                BroadcastManager.refreshFriendRequestList();
            }
            //add by lwl end
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 获取最大的update serial
     * @return
     */
    public String queryMaxUpdateSerial() {
        String result = "-1";
        try {
//            String sql = " select max( cast(" + TableFriend.UPDATE_SERIAL + " as int )) from " + getTableName();
            cursor = database.rawQuery(FriendDaoSqlBuilder.queryMaxUpdateSerialSql(), null);
            if (cursor.moveToFirst()){
                result = cursor.getString(0);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    /**
     * 统计好友总数
     * @return
     */
    public int countFriends() {
        int result = 0 ;
        try{
            String sql = " select count(*) from " + getTableName();
            cursor = database.rawQuery(sql,null);
            if(cursor.moveToFirst()){
                result = cursor.getInt(0);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return result;
    }
}
