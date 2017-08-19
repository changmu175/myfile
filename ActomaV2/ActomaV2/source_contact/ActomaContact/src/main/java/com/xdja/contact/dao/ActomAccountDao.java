package com.xdja.contact.dao;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.dao.sqlbuilder.ActomAccountDaoSqlBuilder;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/7/25.
 *
 * 20161-01-14 重构部分代码
 */
public class ActomAccountDao extends AbstractContactDao<ActomaAccount> {

    /**
     *
     * @param accounts
     * @return
     */
    public Map<String,ActomaAccount> findMapByAccounts(String... accounts){
        Map<String,ActomaAccount> resultMap = new HashMap<>();
        try{
            cursor = database.rawQuery(ActomAccountDaoSqlBuilder.queryByAccounts(accounts), null);
            while(cursor.moveToNext()){
                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                resultMap.put(actomaAccount.getAccount(),actomaAccount);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return resultMap;

    }


    /**
     * 根据传入批量的账号查询对应的账户信息
     * @param accounts 当前函数不校验此参数
     * @return
     */
    public List<ActomaAccount> findAccountsByIds(String... accounts){
        List<ActomaAccount> dataSource = new ArrayList<ActomaAccount>();
        try{
            cursor = database.rawQuery(ActomAccountDaoSqlBuilder.queryByAccounts(accounts), null);
            while(cursor.moveToNext()){
                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                dataSource.add(actomaAccount);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }

    /**
     * 根据账号获取账户信息
     * @param account
     * @return
     */
    public ActomaAccount query(String account) {
        ActomaAccount actomaAccount = null;
        try{
            cursor = database.rawQuery(ActomAccountDaoSqlBuilder.queryByAccounts(account),null);
            if(cursor.moveToFirst()){
                actomaAccount = new ActomaAccount(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return actomaAccount;
    }

    public List<ActomaAccount> queryAll() {
        return null;
    }

    /**
     * 保存账户信息
     * @param actomaAccount
     * @return result> -1 : true; result <= -1 : false
     */
    public long insert(ActomaAccount actomaAccount) {
        long result = -1;
        try {
            result = database.insert(getTableName(), null, actomaAccount.getContentValues());
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 保存头像信息
     * <b>这里的数据库操作本来是要做账户信息的操作的但是这里额外做了头像的动作</b>
     * @param avatar
     * @return result> -1 : true; result <= -1 : false
     */
    public long insertAvatar(Avatar avatar){
        long result = -1;
        try {
            result = database.insert(TableAccountAvatar.TABLE_NAME, null, avatar.getContentValues());
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 更新账户信息
     * @param actomaAccount
     * @return result > 0 : 成功 ;result <= 0 : 失败
     */
    public int update(ActomaAccount actomaAccount) {
        int result = 0;
        try {
            String where = TableActomaAccount.ACCOUNT + " = ? ";
            result = database.update(getTableName(), actomaAccount.getContentValues(), where, new String[]{actomaAccount.getAccount()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }


    /**
     * 更新图片数据
     * @param avatar
     * @return result > 0 : 成功 ;result <= 0 : 失败
     */
    public int updateAvatar(Avatar avatar) {
        int result = 0;
        try {
            String whereArg = TableAccountAvatar.ACCOUNT + "=?";
            result = database.update(TableAccountAvatar.TABLE_NAME, avatar.getContentValues(), whereArg, new String[]{avatar.getAccount()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }





    /*******以下代码未重构*******************/

    @Override
    protected String getTableName() {
        return TableActomaAccount.TABLE_NAME;
    }

    public int delete(ActomaAccount actomaAccount) {
        return 0;
    }


    //根据账号 关联查询 帐号 好友 集团人 头像 部门
    public CommonDetailDto queryCommonDetailByAccount(String account){

        LogUtil.getUtils().i("------sql------:"+ ActomAccountDaoSqlBuilder.queryCommonDetailSql(account));
        CommonDetailDto detailDto = new CommonDetailDto();
        try {
            cursor = database.rawQuery(ActomAccountDaoSqlBuilder.queryCommonDetailSql(account), null);
            if (cursor.moveToFirst()) {
                Friend friend = new Friend(cursor);
                Avatar avatar = new Avatar(cursor);
                Member member = new Member(cursor);
                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                detailDto.setFriend(friend);
                detailDto.setActomaAccount(actomaAccount);
                detailDto.setAvatar(avatar);
                detailDto.setMember(member);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return detailDto;
    }
}
